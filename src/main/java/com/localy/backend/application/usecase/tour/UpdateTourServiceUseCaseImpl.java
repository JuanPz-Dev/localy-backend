package com.localy.backend.application.usecase.tour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.localy.backend.application.dto.request.FileUpload;
import com.localy.backend.application.dto.request.TourServiceRequest;
import com.localy.backend.application.dto.response.TourServiceResponse;
import com.localy.backend.application.exception.BusinessRuleException;
import com.localy.backend.application.mapper.TourServiceMapper;
import com.localy.backend.application.port.input.tour.UpdateTourServiceUseCase;
import com.localy.backend.application.port.output.CloudinaryPort;
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
    private final CloudinaryPort cloudinaryPort;

    @Override
    public TourServiceResponse execute(
            Long id,
            TourServiceRequest tourServiceRequest,
            List<FileUpload> newPhotos,
            List<String> photosToKeep) {
        User currentUser = currentUserPort.getCurrentUser();
        GuideProfile guide = guideRepositoryPort.findByUserId(currentUser.getId()).orElseThrow
        (()-> new IllegalArgumentException("No eres Guia"));
        TourService tour = tourServicePort.findById(id).orElseThrow
        (()-> new IllegalArgumentException("El tour no existe"));
        if (!tour.getGuideProfile().getId().equals(guide.getId())) {
            throw new IllegalArgumentException("No tienes permisos para editar este tour");
        }

        List<String> currentPhotos = tour.getPhotos() == null ? List.of() : List.copyOf(tour.getPhotos());
        if (photosToKeep != null && photosToKeep.stream().anyMatch(photo -> photo == null || photo.isBlank())) {
            throw new BusinessRuleException("Las fotografías que deseas conservar no son válidas");
        }
        List<String> requestedPhotos = photosToKeep == null
                ? List.of()
                : photosToKeep.stream().distinct().toList();
        List<FileUpload> uploads = newPhotos == null ? List.of() : newPhotos;

        if (!currentPhotos.containsAll(requestedPhotos)) {
            throw new BusinessRuleException("Las fotografías que deseas conservar no son válidas");
        }
        if (requestedPhotos.size() + uploads.size() > 5) {
            throw new BusinessRuleException("Una experiencia puede tener máximo 5 fotografías");
        }
        if (requestedPhotos.isEmpty() && uploads.isEmpty()) {
            throw new BusinessRuleException("La experiencia debe tener al menos una fotografía");
        }

        List<String> uploadedUrls = uploadPhotos(uploads);
        List<String> finalPhotos = new ArrayList<>(requestedPhotos);
        finalPhotos.addAll(uploadedUrls);
        List<String> photosToDelete = currentPhotos.stream()
                .filter(photo -> !requestedPhotos.contains(photo))
                .distinct()
                .toList();

        TourService updatedTour;
        try {
            tourServiceMapper.updateDomain(tourServiceRequest, tour);
            tour.setPhotos(finalPhotos);
            updatedTour = tourServicePort.save(tour);
        } catch (RuntimeException exception) {
            compensateUploads(uploadedUrls, exception);
            throw exception;
        }

        deleteRemovedPhotos(photosToDelete, id);
        return tourServiceMapper.toResponse(updatedTour);
    }

    private List<String> uploadPhotos(List<FileUpload> photos) {
        List<String> uploadedUrls = new ArrayList<>();
        try {
            for (FileUpload photo : photos) {
                uploadedUrls.add(cloudinaryPort.upload(photo.contend(), UUID.randomUUID().toString()));
            }
        } catch (IOException exception) {
            compensateUploads(uploadedUrls, exception);
            throw new IllegalStateException("No fue posible subir las nuevas fotografías", exception);
        } catch (RuntimeException exception) {
            compensateUploads(uploadedUrls, exception);
            throw exception;
        }
        return uploadedUrls;
    }

    private void compensateUploads(List<String> uploadedUrls, Exception originalFailure) {
        for (String uploadedUrl : uploadedUrls) {
            try {
                cloudinaryPort.deleteByUrl(uploadedUrl);
            } catch (IOException | RuntimeException cleanupFailure) {
                originalFailure.addSuppressed(cleanupFailure);
            }
        }
    }

    private void deleteRemovedPhotos(List<String> photosToDelete, Long tourId) {
        RuntimeException cleanupFailure = null;
        for (String photoUrl : photosToDelete) {
            if (tourServicePort.isPhotoUsedByAnotherTour(photoUrl, tourId)) {
                continue;
            }
            try {
                cloudinaryPort.deleteByUrl(photoUrl);
            } catch (IOException | RuntimeException exception) {
                if (cleanupFailure == null) {
                    cleanupFailure = new IllegalStateException(
                            "El servicio se actualizó, pero no se pudieron eliminar todas las imágenes de Cloudinary");
                }
                cleanupFailure.addSuppressed(exception);
            }
        }
        if (cleanupFailure != null) {
            throw cleanupFailure;
        }
    }
}