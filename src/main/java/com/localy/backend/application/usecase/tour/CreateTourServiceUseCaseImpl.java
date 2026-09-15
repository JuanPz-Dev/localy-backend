package com.localy.backend.application.usecase.tour;

import java.util.List;

import org.springframework.stereotype.Service;

import com.localy.backend.application.dto.request.FileUpload;
import com.localy.backend.application.dto.request.TourServiceRequest;
import com.localy.backend.application.dto.response.TourServiceResponse;
import com.localy.backend.application.mapper.TourServiceMapper;
import com.localy.backend.application.port.input.tour.CreateTourServiceUseCase;
import com.localy.backend.application.port.output.CloudinaryPort;
import com.localy.backend.application.port.output.GuideRepositoryPort;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.enums.ServiceStatus;
import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.domain.model.TourService;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateTourServiceUseCaseImpl implements CreateTourServiceUseCase{

    private final TourServiceRepositoryPort tourServicePort;
    private final TourServiceMapper tourServiceMapper;
    private final GuideRepositoryPort guideRepositoryPort;
    private final CurrentUserPort currentUserPort;
    private final CloudinaryPort cloudinaryPort;

    @Override
    public TourServiceResponse execute(TourServiceRequest tourServiceRequest,List<FileUpload>photos) {
        User currentUser = currentUserPort.getCurrentUser();
        GuideProfile guide = guideRepositoryPort.findByUserId(currentUser.getId())
        .orElseThrow(() -> new IllegalArgumentException("El usuario no es un guia"));

        List<String>photoUrl = photos.stream()
        .map(photo -> {
            try {
                return cloudinaryPort.upload(photo.contend(), photo.fileName());
            } catch (Exception e) {
               throw new RuntimeException("Error al subir imagen", e);
            }
        }).toList();

        TourService tourService = tourServiceMapper.toDomain(tourServiceRequest);
        TourService tourToSave = tourService.toBuilder()
        .guideProfile(guide)
        .serviceStatus(ServiceStatus.APPROVED)
        .photos(photoUrl)
        .build();

        TourService saveToTour = tourServicePort.save(tourToSave);
        return tourServiceMapper.toResponse(saveToTour);
    }
}