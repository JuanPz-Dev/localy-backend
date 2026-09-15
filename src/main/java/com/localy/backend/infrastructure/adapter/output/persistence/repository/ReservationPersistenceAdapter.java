package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.localy.backend.application.port.output.ReservationRepositoryPort;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.ReservationStatus;
import com.localy.backend.domain.model.Reservation;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.ReservationEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.mapper.ReservationPersistenceMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationPersistenceAdapter implements ReservationRepositoryPort{

    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationPersistenceMapper reservationPersistenceMapper;

    @Override
    public Reservation save(Reservation reservation) {
        ReservationEntity entity = reservationPersistenceMapper.toEntity(reservation);
        ReservationEntity savedReservation = reservationJpaRepository.save(entity);
        return reservationPersistenceMapper.toDomain(savedReservation);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationJpaRepository.findById(id)
        .map(reservationPersistenceMapper::toDomain);
    }

    @Override
    public PageResult<Reservation> findAll(PaginationRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        Page<ReservationEntity> page = reservationJpaRepository.findAll(pageable);
        List<Reservation> reservations = page.getContent()
            .stream()
            .map(reservationPersistenceMapper::toDomain)
            .toList();

        return new PageResult<>(
            reservations,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @Override
    public PageResult<Reservation> findByGuide(Long guideId, PaginationRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        Page<ReservationEntity> page = reservationJpaRepository.findByTourServiceEntity_GuideEntity_Id(guideId, pageable);
        List<Reservation> reservations = page.getContent()
            .stream()
            .map(reservationPersistenceMapper::toDomain)
            .toList();

        return new PageResult<>(
            reservations,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @Override
    public PageResult<Reservation> findByUserId(Long userId, PaginationRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        Page <ReservationEntity> page = reservationJpaRepository.findByUserId(userId,pageable);
        List<Reservation> reservation = page.getContent()
        .stream()
        .map(reservationPersistenceMapper::toDomain)
        .toList();
        
        return new PageResult<>(
            reservation,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @Override
    public PageResult<Reservation> findByTourService(Long id, PaginationRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        Page<ReservationEntity> page = reservationJpaRepository.findByTourServiceEntity_Id(id, pageable);
        List<Reservation> reservations = page.getContent()
            .stream()
            .map(reservationPersistenceMapper::toDomain)
            .toList();

        return new PageResult<>(
            reservations,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @Override
    public PageResult<Reservation> findByStatus(ReservationStatus reservationStatus, PaginationRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        Page<ReservationEntity> page = reservationJpaRepository.findByReservationStatus(reservationStatus, pageable);
        List<Reservation> reservations = page.getContent()
            .stream()
            .map(reservationPersistenceMapper::toDomain)
            .toList();

        return new PageResult<>(
            reservations,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }
}