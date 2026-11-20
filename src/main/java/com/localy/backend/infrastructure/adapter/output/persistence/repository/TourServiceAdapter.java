package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.Category;
import com.localy.backend.domain.enums.ServiceStatus;
import com.localy.backend.domain.model.TourService;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.GuideEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.TourScheduleEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.TourServiceEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.mapper.TourServicePersistenceMapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TourServiceAdapter implements TourServiceRepositoryPort{
    
    private final TourJpaRepository tourJpaRepository;
    private final TourServicePersistenceMapper tourServicePersistenceMapper;
    private final GuideJpaRepository guideJpaRepository;

    @Override
    @Transactional
    public TourService save(TourService tourService) {
        TourServiceEntity tour;
        if (tourService.getId() != null) {
            tour = tourJpaRepository.findById(tourService.getId())
            .orElseThrow(() -> new EntityNotFoundException("No se encontró el servicio con ID: " + tourService.getId()));
            TourServiceEntity updatedData = tourServicePersistenceMapper.toEntity(tourService);
            tour.setTitle(updatedData.getTitle());
            tour.setDescription(updatedData.getDescription());
            tour.setPrice(updatedData.getPrice());
            tour.setMinParticipants(updatedData.getMinParticipants());
            tour.setMaxParticipants(updatedData.getMaxParticipants());
            tour.setDurationMinutes(updatedData.getDurationMinutes());
            tour.setCategory(updatedData.getCategory());
            tour.setServiceStatus(updatedData.getServiceStatus());
            tour.setDeleted(updatedData.isDeleted());
            tour.setMeetingLocation(updatedData.getMeetingLocation());
            tour.setPhotos(updatedData.getPhotos());
            tour.setFeatures(updatedData.getFeatures());
            // Sincronizar horarios cuando la solicitud incluye la lista.
            if (tourService.getSchedules() != null) {
                var existingSchedules = tour.getSchedules();
                var requestedSchedules = tourService.getSchedules();
            // Eliminar los horarios que ya no vienen en la solicitud.
            existingSchedules.removeIf(existing ->
                requestedSchedules.stream().noneMatch(requested ->
                    existing.getDay().equals(requested.getDay())
                        && existing.getStartTime().equals(requested.getStartTime())
                )
            );
            // Agregar solamente los horarios que todavía no existen.
            requestedSchedules.forEach(requested -> {
                boolean exists = existingSchedules.stream().anyMatch(existing ->
                    existing.getDay().equals(requested.getDay())
                        && existing.getStartTime().equals(requested.getStartTime())
                );

                if (!exists) {
                    TourScheduleEntity newSchedule = new TourScheduleEntity();
                    newSchedule.setDay(requested.getDay());
                    newSchedule.setStartTime(requested.getStartTime());
                    newSchedule.setTourServiceEntity(tour);
                    existingSchedules.add(newSchedule);
                }
            });
        }
    } else {tour = tourServicePersistenceMapper.toEntity(tourService);
        Long guideId = tourService.getGuideProfile().getId();
        GuideEntity guide = guideJpaRepository.findById(guideId)
            .orElseThrow(() -> new EntityNotFoundException("No se encontró el perfil de guía con ID: " + guideId));
        tour.setGuideEntity(guide);
        if (tour.getSchedules() != null) {tour.getSchedules().forEach(
                schedule -> schedule.setTourServiceEntity(tour)
            );
        }
    }
    TourServiceEntity savedTour = tourJpaRepository.save(tour);
    return tourServicePersistenceMapper.toDomain(savedTour);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TourService> findById(Long id) {
        return tourJpaRepository.findById(id)
        .map(tourServicePersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPhotoUsedByAnotherTour(String photoUrl, Long tourId) {
        return tourJpaRepository.isPhotoUsedByAnotherTour(photoUrl, tourId);
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

    @Override
    @Transactional(readOnly = true)
    public List<TourService> findAllForPrediction() {
        return tourJpaRepository.findAllByDeletedFalse()
            .stream()
            .map(tourServicePersistenceMapper::toDomain)
            .toList();
    }
}