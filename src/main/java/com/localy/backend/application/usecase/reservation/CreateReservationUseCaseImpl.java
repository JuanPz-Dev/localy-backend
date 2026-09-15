package com.localy.backend.application.usecase.reservation;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.localy.backend.application.dto.request.ReservationRequest;
import com.localy.backend.application.dto.response.ReservationResponse;
import com.localy.backend.application.mapper.ReservationMapper;
import com.localy.backend.application.port.input.reservation.CreateReservationUseCase;
import com.localy.backend.application.port.output.ReservationRepositoryPort;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.enums.ReservationStatus;
import com.localy.backend.domain.model.Reservation;
import com.localy.backend.domain.model.TourService;
import com.localy.backend.domain.model.User;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateReservationUseCaseImpl implements CreateReservationUseCase{
    
    private final CurrentUserPort currentUserPort;
    private final ReservationMapper reservationMapper;
    private final ReservationRepositoryPort reservationPort;
    private final TourServiceRepositoryPort tourServiceRepositoryPort;

    @Override
    public ReservationResponse execute(Long tourServiceId, ReservationRequest reservationRequest) {
        User user = currentUserPort.getCurrentUser();
        TourService tourService = tourServiceRepositoryPort.findById(tourServiceId)
        .orElseThrow(()-> new RuntimeException("Servicio no encontrado"));
        
        int participants = reservationRequest.adults() + reservationRequest.children();

        if (participants < tourService.getMinParticipants() || participants > tourService.getMaxParticipants()) {
            throw new RuntimeException("La cantidad de participantes no está permitida");
        }
        BigDecimal totalPrice = tourService.getPrice().multiply(BigDecimal.valueOf(participants));
        Reservation reservation = reservationMapper.toDomain(reservationRequest);
        reservation.setUser(user);
        reservation.setTourService(tourService);
        reservation.setTotalPrice(totalPrice);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(Instant.now());

        Reservation savedReservation = reservationPort.save(reservation);
        return reservationMapper.toResponse(savedReservation);
    }
}