package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.port.output.GuideRepositoryPort;

import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.GuideStatus;
import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.GuideEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.mapper.GuidePersistenceMapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GuideRepositoryAdapter implements GuideRepositoryPort{

    private final GuideJpaRepository guideJpaRepository;
    private final GuidePersistenceMapper guidePersistenceMapper;

    @Override
    @Transactional
    public GuideProfile save(GuideProfile guideProfile) {
        GuideEntity entity;
        if (guideProfile.getId() == null) {
            entity = guidePersistenceMapper.toEntity(guideProfile);
        } else {
            entity = guideJpaRepository.findById(guideProfile.getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Guía no encontrada: " + guideProfile.getId()));
            guidePersistenceMapper.updateEntity(guideProfile, entity);
        }
        GuideEntity savedEntity = guideJpaRepository.save(entity);
        return guidePersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<GuideProfile> findById(Long id) {
        return guideJpaRepository.findById(id)
        .map(guidePersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByLicense(String license) {
        return guideJpaRepository.existsByLicense(license);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GuideProfile> findByUserId(Long id) {
        return guideJpaRepository.findByUserId(id)
        .map(guidePersistenceMapper::toDomain);
    }

    @Override
    public PageResult<GuideProfile> findByGuideStatus(GuideStatus guideStatus, PaginationRequest paginationRequest) {
        Pageable pageable = PageRequest.of(
            paginationRequest.getPage(),
            paginationRequest.getSize(),
            Sort.by(Sort.Direction.ASC, "id")
        );

        Page<GuideEntity> page = guideJpaRepository.findByGuideStatus(guideStatus, pageable);

        return new PageResult<>(
            page.getContent().stream().map(guidePersistenceMapper::toDomain).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @Override
    public void deleteById(Long id) {
        guideJpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public GuideProfile updateStatus(Long id, GuideStatus guideStatus) {
        GuideEntity entity = guideJpaRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Guía no encontrada: " + id));
        entity.setGuideStatus(guideStatus);
        GuideEntity savedEntity = guideJpaRepository.save(entity);
        return guidePersistenceMapper.toDomain(savedEntity);
    }
    
}