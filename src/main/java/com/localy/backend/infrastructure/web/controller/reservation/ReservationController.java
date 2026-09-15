package com.localy.backend.infrastructure.web.controller.reservation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.localy.backend.application.dto.request.ReservationRequest;
import com.localy.backend.application.dto.response.ReservationResponse;
import com.localy.backend.application.port.input.reservation.CancelReservationUseCase;
import com.localy.backend.application.port.input.reservation.CreateReservationUseCase;
import com.localy.backend.application.port.input.reservation.GetMyReservationsUseCase;
import com.localy.backend.application.port.input.reservation.GetReservationUseCase;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;


@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final CreateReservationUseCase createReservationUseCase;
    private final GetMyReservationsUseCase getMyReservationsUseCase;
    private final GetReservationUseCase getReservationUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;

    @PostMapping("/tour/{tourServiceId}")
    public ResponseEntity<ReservationResponse> create(@PathVariable Long tourServiceId, @RequestBody ReservationRequest reservationRequest) {
        ReservationResponse reservationResponse = createReservationUseCase.execute(tourServiceId, reservationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationResponse);
    }
    
    @GetMapping("/my")
    public PageResult<ReservationResponse> getMyReservations(@ModelAttribute PaginationRequest paginationRequest) {
        return getMyReservationsUseCase.execute(paginationRequest);
    }

    @GetMapping("/{id}")
    public ReservationResponse getReservation(@PathVariable Long id) {
        return getReservationUseCase.execute(id);
    }

    @PatchMapping("/{reservationId}/cancel")
    public ReservationResponse cancelReservation(@PathVariable Long reservationId){
        return cancelReservationUseCase.execute(reservationId);
    } 
}