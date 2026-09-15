package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.localy.backend.domain.enums.GuideStatus;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.GuideEntity;

public interface GuideJpaRepository extends JpaRepository<GuideEntity,Long>{
    boolean existsByLicense(String license);
    Optional<GuideEntity>findByUserId(Long id);
    Page<GuideEntity>findByGuideStatus(GuideStatus guideStatus,Pageable pageable);
}