package com.localy.backend.application.usecase.reservation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Instant;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.localy.backend.application.dto.request.ReservationRequest;
import com.localy.backend.application.dto.response.ReservationResponse;
import com.localy.backend.application.exception.BusinessRuleException;
import com.localy.backend.application.mapper.ReservationMapper;
import com.localy.backend.application.port.input.reservation.CreateReservationUseCase;
import com.localy.backend.application.port.output.ReservationRepositoryPort;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.enums.ReservationStatus;
import com.localy.backend.domain.model.Reservation;
import com.localy.backend.domain.model.TourService;
import com.localy.backend.domain.model.TourSchedule;
import com.localy.backend.domain.model.User;
import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateReservationUseCaseImpl implements CreateReservationUseCase{
    
    private final CurrentUserPort currentUserPort;
    private final ReservationMapper reservationMapper;
    private final ReservationRepositoryPort reservationPort;
    private final TourServiceRepositoryPort tourServiceRepositoryPort;
    @Value("${localy.commission.rate:0.10}")
    private BigDecimal commissionRate;

    @Override
    public ReservationResponse execute(Long tourServiceId, ReservationRequest reservationRequest) {
        if (reservationRequest == null
                || reservationRequest.reservationDate() == null
                || reservationRequest.startTime() == null
                || reservationRequest.adults() == null
                || reservationRequest.children() == null
                || reservationRequest.adults() < 1
                || reservationRequest.children() < 0) {
            throw new BusinessRuleException("La cantidad de participantes no es válida");
        }

        User user = currentUserPort.getCurrentUser();
        TourService tourService = tourServiceRepositoryPort.findById(tourServiceId)
        .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));

        if (tourService.isDeleted()) {
            throw new BusinessRuleException("Este servicio ya no está disponible para nuevas reservas");
        }

        if (tourService.getGuideProfile() == null || tourService.getGuideProfile().getUser() == null) {
            throw new BusinessRuleException("El servicio no tiene un guía válido");
        }

        if (Objects.equals(user.getId(), tourService.getGuideProfile().getUser().getId())) {
            throw new BusinessRuleException("No puedes reservar tu propio servicio");
        }

        DayOfWeek reservationDay = reservationRequest.reservationDate().getDayOfWeek();
        TourSchedule selectedSchedule = tourService.getSchedules() == null
                ? null
                : tourService.getSchedules().stream()
                        .filter(schedule -> schedule.getDay() == reservationDay
                                && reservationRequest.startTime().equals(schedule.getStartTime()))
                        .findFirst()
                        .orElse(null);
        if (selectedSchedule == null) {
            throw new BusinessRuleException("El horario seleccionado no está disponible para la fecha indicada");
        }

        long participants = (long) reservationRequest.adults() + reservationRequest.children();
        if (tourService.getMinParticipants() == null || tourService.getMaxParticipants() == null
                || participants < tourService.getMinParticipants()
                || participants > tourService.getMaxParticipants()) {
            throw new BusinessRuleException("La cantidad de participantes no está permitida");
        }
        if (commissionRate == null || commissionRate.compareTo(BigDecimal.ZERO) < 0
        || commissionRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("La comisión configurada no es válida");
        }
        BigDecimal basePrice = tourService.getPrice().multiply(BigDecimal.valueOf(participants));
        BigDecimal commissionAmount = basePrice.multiply(commissionRate).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalPrice = basePrice;
        Reservation reservation = reservationMapper.toDomain(reservationRequest);
        reservation.setUser(user);
        reservation.setTourService(tourService);
        reservation.setTourSchedule(selectedSchedule);
        reservation.setBasePrice(basePrice);
        reservation.setCommissionAmount(commissionAmount);
        reservation.setTotalPrice(totalPrice);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(Instant.now());

        Reservation savedReservation = reservationPort.save(reservation);
        return reservationMapper.toResponse(savedReservation);
    }
}