package com.localy.backend.application.usecase.reservation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.dto.response.ReservationResponse;
import com.localy.backend.application.exception.BusinessRuleException;
import com.localy.backend.application.mapper.ReservationMapper;
import com.localy.backend.application.port.input.reservation.GetGuideReservationsUseCase;
import com.localy.backend.application.port.output.GuideRepositoryPort;
import com.localy.backend.application.port.output.ReservationRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.domain.model.Reservation;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetGuideReservationsUseCaseImpl implements GetGuideReservationsUseCase{
    
    private final CurrentUserPort currentUserPort;
    private final ReservationMapper reservationMapper;
    private final ReservationRepositoryPort reservationRepositoryPort;
    private final GuideRepositoryPort guideRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public PageResult<ReservationResponse> execute(PaginationRequest pageRequest) {
        User user = currentUserPort.getCurrentUser();
        GuideProfile guide = guideRepositoryPort.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessRuleException("El usuario no tiene un perfil de guía"));
        PageResult<Reservation> reservations = reservationRepositoryPort.findByGuide(guide.getId(), pageRequest);
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