package com.localy.backend.application.usecase.reservation;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.dto.response.ReservationResponse;
import com.localy.backend.application.exception.BusinessRuleException;
import com.localy.backend.application.mapper.ReservationMapper;
import com.localy.backend.application.port.input.reservation.GetReservationUseCase;
import com.localy.backend.application.port.output.ReservationRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.model.Reservation;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetReservationUseCaseImpl implements GetReservationUseCase{
    
    private final CurrentUserPort currentUserPort;
    private final ReservationMapper reservationMapper;
    private final ReservationRepositoryPort reservationRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse execute(Long id) {
        User currentUser = currentUserPort.getCurrentUser();
        Reservation reservation = reservationRepositoryPort.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        if (reservation.getUser() == null
                || !Objects.equals(reservation.getUser().getId(), currentUser.getId())) {
            throw new BusinessRuleException("No tienes permiso para consultar esta reserva");
        }
        return reservationMapper.toResponse(reservation);
    }
}