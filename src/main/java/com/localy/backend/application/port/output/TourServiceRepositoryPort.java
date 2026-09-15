package com.localy.backend.application.port.output;

import java.util.Optional;

import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.Category;
import com.localy.backend.domain.enums.ServiceStatus;
import com.localy.backend.domain.model.TourService;

public interface TourServiceRepositoryPort {
    TourService save (TourService tourService);
    Optional<TourService>findById(Long id);
    boolean existsById(Long id);
    PageResult<TourService>findAll(PaginationRequest pageRequest);
    PageResult<TourService>findByCategory(Category category,PaginationRequest pageRequest);
    PageResult<TourService>findByCity(String city,PaginationRequest pageRequest);
    PageResult<TourService>findByGuideProfileId(Long id,PaginationRequest pageRequest);
    PageResult<TourService>findByStatus(ServiceStatus serviceStatus,PaginationRequest pageRequest);
    PageResult<TourService>findByTitle(String title,PaginationRequest pageRequest);
    void deleteById(Long id);
}