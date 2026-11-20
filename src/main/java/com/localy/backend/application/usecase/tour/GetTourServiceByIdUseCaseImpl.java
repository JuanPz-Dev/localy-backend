package com.localy.backend.application.usecase.tour;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.dto.response.TourServiceResponse;
import com.localy.backend.application.mapper.TourServiceMapper;
import com.localy.backend.application.port.input.tour.GetTourServiceUseCase;
import com.localy.backend.application.port.output.ReviewRepositoryPort;
import com.localy.backend.application.port.output.ReviewStatistics;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.domain.model.TourService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetTourServiceByIdUseCaseImpl implements GetTourServiceUseCase{

    private final TourServiceRepositoryPort tourServicePort;
    private final TourServiceMapper tourServiceMapper;
    private final ReviewRepositoryPort reviewRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public TourServiceResponse execute(Long id) {
        TourService tour = tourServicePort.findById(id)
        .orElseThrow(()-> new IllegalArgumentException("El tour no existe"));
        ReviewStatistics statistics = reviewRepositoryPort
                .summarizeByTourServiceIds(List.of(id))
                .get(id);
        return tourServiceMapper.toResponse(
                tour,
                statistics == null ? null : statistics.averageRating(),
                statistics == null ? 0 : Math.toIntExact(statistics.reviewCount()));
    }
}