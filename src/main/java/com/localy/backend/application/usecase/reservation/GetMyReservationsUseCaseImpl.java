package com.localy.backend.application.usecase.reservation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.localy.backend.application.dto.response.ReservationResponse;
import com.localy.backend.application.mapper.ReservationMapper;
import com.localy.backend.application.port.input.reservation.GetMyReservationsUseCase;
import com.localy.backend.application.port.output.ReservationRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.model.Reservation;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetMyReservationsUseCaseImpl implements GetMyReservationsUseCase{

    private final CurrentUserPort currentUserPort;
    private final ReservationMapper reservationMapper;
    private final ReservationRepositoryPort reservationRepositoryPort;

    @Override
    public PageResult<ReservationResponse> execute(PaginationRequest pageRequest) {
        User user = currentUserPort.getCurrentUser();

        PageResult<Reservation> reservations = reservationRepositoryPort.findByUserId(user.getId(), pageRequest);
        List<ReservationResponse> responses = reservations.getContent()
        .stream()
        .map(reservationMapper::toResponse)
        .toList();
        return new PageResult<>(
            responses,
            reservations.getPage(),
            reservations.getSize(),
            reservations.getTotalElements()
        );
    }
}