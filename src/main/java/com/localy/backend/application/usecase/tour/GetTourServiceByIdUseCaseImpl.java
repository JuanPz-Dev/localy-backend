package com.localy.backend.application.usecase.tour;

import org.springframework.stereotype.Service;

import com.localy.backend.application.dto.response.TourServiceResponse;
import com.localy.backend.application.mapper.TourServiceMapper;
import com.localy.backend.application.port.input.tour.GetTourServiceUseCase;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.domain.model.TourService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetTourServiceByIdUseCaseImpl implements GetTourServiceUseCase{

    private final TourServiceRepositoryPort tourServicePort;
    private final TourServiceMapper tourServiceMapper;

    @Override
    public TourServiceResponse execute(Long id) {
        TourService tour = tourServicePort.findById(id)
        .orElseThrow(()-> new IllegalArgumentException("El tour no existe"));
        return tourServiceMapper.toResponse(tour);
    }
}