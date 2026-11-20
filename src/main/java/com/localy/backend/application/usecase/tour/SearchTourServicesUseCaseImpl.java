package com.localy.backend.application.usecase.tour;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.dto.request.TourServiceFilterRequest;
import com.localy.backend.application.dto.response.TourServiceResponse;
import com.localy.backend.application.mapper.TourServiceMapper;
import com.localy.backend.application.port.input.tour.SearchTourServicesUseCase;
import com.localy.backend.application.port.output.ReviewRepositoryPort;
import com.localy.backend.application.port.output.ReviewStatistics;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.model.TourService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchTourServicesUseCaseImpl implements SearchTourServicesUseCase{

    private final TourServiceRepositoryPort tourServicePort;
    private final TourServiceMapper tourServiceMapper;
    private final ReviewRepositoryPort reviewRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public PageResult<TourServiceResponse> execute(TourServiceFilterRequest filterRequest,PaginationRequest pageRequest) {

        PageResult<TourService> tours;
        if (filterRequest.category() != null) {
            tours = tourServicePort.findByCategory(
            filterRequest.category(),
            pageRequest);
        }else if (filterRequest.city() != null && !filterRequest.city().isBlank()) {
            tours = tourServicePort.findByCity(filterRequest.city(), pageRequest);
        }else{
            tours = tourServicePort.findAll(pageRequest);
        }
        List<TourService> tourServices = tours.getContent();
        Map<Long, ReviewStatistics> statisticsByTourService = tourServices.isEmpty()
                ? Map.of()
                : reviewRepositoryPort.summarizeByTourServiceIds(
                        tourServices.stream().map(TourService::getId).toList());
        List<TourServiceResponse> mappedContent = tourServices.stream()
                .map(tour -> {
                    ReviewStatistics statistics = statisticsByTourService.get(tour.getId());
                    return tourServiceMapper.toResponse(
                            tour,
                            statistics == null ? null : statistics.averageRating(),
                            statistics == null ? 0 : Math.toIntExact(statistics.reviewCount()));
                })
                .collect(Collectors.toList());
        return new PageResult<>(mappedContent, tours.getPage(), tours.getSize(), tours.getTotalElements());
    }
}