package com.localy.backend.application.usecase.reservation;

import org.springframework.stereotype.Service;

import com.localy.backend.application.dto.response.ReservationResponse;
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
    public ReservationResponse execute(Long id) {
        User currentUser = currentUserPort.getCurrentUser();
        Reservation reservation = reservationRepositoryPort.findById(id)
        .orElseThrow(()-> new RuntimeException("Reserva no encontrado"));
        if (!reservation.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para consultar esta reserva");
        }
        return reservationMapper.toResponse(reservation);
    }
}