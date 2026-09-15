package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.Category;
import com.localy.backend.domain.enums.ServiceStatus;
import com.localy.backend.domain.model.TourService;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.TourServiceEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.mapper.TourServicePersistenceMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TourServiceAdapter implements TourServiceRepositoryPort{
    
    private final TourJpaRepository tourJpaRepository;
    private final TourServicePersistenceMapper tourServicePersistenceMapper;

    @Override
    public TourService save(TourService tourService) {
        TourServiceEntity tour = tourServicePersistenceMapper.toEntity(tourService);
        TourServiceEntity saveTour = tourJpaRepository.save(tour);
        return tourServicePersistenceMapper.toDomain(saveTour);
    }

    @Override
    public Optional<TourService> findById(Long id) {
        return tourJpaRepository.findById(id)
        .map(tourServicePersistenceMapper::toDomain);
    }

    @Override
    public PageResult<TourService> findAll(PaginationRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        Page <TourServiceEntity> page = tourJpaRepository.findAll(pageable);
        List<TourService> tours = page.getContent()
        .stream()
        .map(tourServicePersistenceMapper::toDomain)
        .toList();
        
        return new PageResult<>(
            tours,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @Override
    public PageResult<TourService> findByCategory(Category category, PaginationRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        Page<TourServiceEntity> page = tourJpaRepository.findByCategory(category, pageable);
        List<TourService> tours = page.getContent()
        .stream()
        .map(tourServicePersistenceMapper::toDomain)
        .toList();
        return new PageResult<>(
            tours,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @Override
    public PageResult<TourService> findByCity(String city, PaginationRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        Page<TourServiceEntity> page = tourJpaRepository.findByMeetingLocationCity(city, pageable);
        List<TourService> tours = page.getContent()
        .stream()
        .map(tourServicePersistenceMapper::toDomain)
        .toList();
        return new PageResult<>(
            tours,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @Override
    public PageResult<TourService> findByGuideProfileId(Long id, PaginationRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        Page<TourServiceEntity> page = tourJpaRepository.findByGuideEntity_Id(id, pageable);
        List<TourService> tours = page.getContent()
            .stream()
            .map(tourServicePersistenceMapper::toDomain)
            .toList();

        return new PageResult<>(
            tours,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @Override
    public PageResult<TourService> findByStatus(ServiceStatus serviceStatus, PaginationRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        Page<TourServiceEntity> page = tourJpaRepository.findByServiceStatus(serviceStatus, pageable);
        List<TourService> tours = page.getContent()
            .stream()
            .map(tourServicePersistenceMapper::toDomain)
            .toList();

        return new PageResult<>(
            tours,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @Override
    public PageResult<TourService> findByTitle(String title, PaginationRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        Page<TourServiceEntity> page = tourJpaRepository.findByTitleContainingIgnoreCase(title, pageable);
        List<TourService> tours = page.getContent()
            .stream()
            .map(tourServicePersistenceMapper::toDomain)
            .toList();

        return new PageResult<>(
            tours,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @Override
    public void deleteById(Long id) {
        tourJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
       return tourJpaRepository.existsById(id);
    }
}