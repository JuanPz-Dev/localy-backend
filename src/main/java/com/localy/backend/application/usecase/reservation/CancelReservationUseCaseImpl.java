package com.localy.backend.application.usecase.reservation;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.dto.response.ReservationResponse;
import com.localy.backend.application.exception.BusinessRuleException;
import com.localy.backend.application.mapper.ReservationMapper;
import com.localy.backend.application.port.input.reservation.CancelReservationUseCase;
import com.localy.backend.application.port.output.ReservationRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.enums.ReservationStatus;
import com.localy.backend.domain.model.Reservation;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CancelReservationUseCaseImpl implements CancelReservationUseCase{
    
    private final CurrentUserPort currentUserPort;
    private final ReservationMapper reservationMapper;
    private final ReservationRepositoryPort reservationPort;

    @Override
    @Transactional
    public ReservationResponse execute(Long reservationId) {
        User currentUser = currentUserPort.getCurrentUser();
        Reservation reservation = reservationPort.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        if (reservation.getUser() == null
                || !Objects.equals(reservation.getUser().getId(), currentUser.getId())) {
            throw new BusinessRuleException("No puedes cancelar esta reserva");
        }
        if (reservation.getReservationStatus()!= ReservationStatus.PENDING) {
            throw new BusinessRuleException("La reserva no puede ser cancelada");
        }
        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        Reservation savedReservation = reservationPort.save(reservation);
        return reservationMapper.toResponse(savedReservation);
    }
}