package com.localy.backend.domain.repository;

import java.util.Optional;

import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.GuideStatus;
import com.localy.backend.domain.model.GuideProfile;

public interface GuideRepositoryPort {
    GuideProfile save (GuideProfile guideProfile);
    Optional<GuideProfile> findById(Long id);
    boolean existsByLicense(String license);
    Optional<GuideProfile>findByUserId(Long id);
    PageResult<GuideProfile>findByGuideStatus(GuideStatus guideStatus, PaginationRequest pageRequest);
    void deleteById (Long id);
}