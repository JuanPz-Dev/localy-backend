package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.localy.backend.application.port.output.GuideRepositoryPort;

import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.GuideStatus;
import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.GuideEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.mapper.GuidePersistenceMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GuideRepositoryAdapter implements GuideRepositoryPort{

    private final GuideJpaRepository guideJpaRepository;
    private final GuidePersistenceMapper guidePersistenceMapper;

    @Override
    public GuideProfile save(GuideProfile guideProfile) {
        GuideEntity entity = guidePersistenceMapper.toEntity(guideProfile);
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
    public Optional<GuideProfile> findByUserId(Long id) {
        return guideJpaRepository.findByUserId(id)
        .map(guidePersistenceMapper::toDomain);
    }

    @Override
    public PageResult<GuideProfile> findByGuideStatus(GuideStatus guideStatus, PaginationRequest paginationRequest) {
        Pageable pageable = PageRequest.of(
            paginationRequest.getPage(),
            paginationRequest.getSize()
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
    
}