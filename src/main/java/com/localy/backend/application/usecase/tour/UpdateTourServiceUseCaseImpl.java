package com.localy.backend.application.usecase.tour;

import org.springframework.stereotype.Service;

import com.localy.backend.application.dto.request.TourServiceRequest;
import com.localy.backend.application.dto.response.TourServiceResponse;
import com.localy.backend.application.mapper.TourServiceMapper;
import com.localy.backend.application.port.input.tour.UpdateTourServiceUseCase;
import com.localy.backend.application.port.output.GuideRepositoryPort;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.domain.model.TourService;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateTourServiceUseCaseImpl implements UpdateTourServiceUseCase{
    
    private final TourServiceRepositoryPort tourServicePort;
    private final TourServiceMapper tourServiceMapper;
    private final GuideRepositoryPort guideRepositoryPort;
    private final CurrentUserPort currentUserPort;

    @Override
    public TourServiceResponse execute(Long id,TourServiceRequest tourServiceRequest) {
        User currentUser = currentUserPort.getCurrentUser();
        GuideProfile guide = guideRepositoryPort.findByUserId(currentUser.getId()).orElseThrow
        (()-> new IllegalArgumentException("No eres Guia"));
        TourService tour = tourServicePort.findById(id).orElseThrow
        (()-> new IllegalArgumentException("El tour no existe"));
        if (!tour.getGuideProfile().getId().equals(guide.getId())) {
            throw new IllegalArgumentException("No tienes permisos para eliminar este tour");
        }
        tourServiceMapper.updateDomain(tourServiceRequest, tour);
        TourService updatedTour = tourServicePort.save(tour);
        return tourServiceMapper.toResponse(updatedTour);
    }
}