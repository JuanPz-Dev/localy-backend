package com.localy.backend.domain.repository;

import java.util.Optional;

import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.ReservationStatus;
import com.localy.backend.domain.model.Reservation;

public interface ReservationRepositoryPort {
    Reservation save (Reservation reservation);
    Optional<Reservation>findById(Long id);
    PageResult<Reservation>findAll(PaginationRequest pageRequest);
    PageResult<Reservation>findByGuide(Long GuideId, PaginationRequest pageRequest);
    PageResult<Reservation>findByUserId(Long userId, PaginationRequest pageRequest);
    PageResult<Reservation> findByTourService(Long id, PaginationRequest pageRequest);
    PageResult<Reservation>findByStatus(ReservationStatus reservationStatus, PaginationRequest pageRequest);
}