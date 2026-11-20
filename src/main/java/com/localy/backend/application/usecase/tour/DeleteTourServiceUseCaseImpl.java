package com.localy.backend.application.usecase.tour;

import org.springframework.stereotype.Service;

import com.localy.backend.application.port.input.tour.DeleteTourServiceUseCase;
import com.localy.backend.application.port.output.GuideRepositoryPort;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.domain.model.TourService;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteTourServiceUseCaseImpl implements DeleteTourServiceUseCase{

    private final TourServiceRepositoryPort tourServicePort;
    private final GuideRepositoryPort guideRepositoryPort;
    private final CurrentUserPort currentUserPort;

    @Override
    public void execute(Long id) {
        User currentUser = currentUserPort.getCurrentUser();
        GuideProfile guide = guideRepositoryPort.findByUserId(currentUser.getId())
           .orElseThrow(()->new IllegalArgumentException("No eres Guia"));
        TourService tour = tourServicePort.findById(id)
           .orElseThrow(()-> new IllegalArgumentException("El tour no existe"));
           if (!tour.getGuideProfile().getId().equals(guide.getId())) {
            throw new IllegalArgumentException("No tienes permisos para eliminar este tour");
        }
        if (tour.isDeleted()) {
            throw new IllegalArgumentException("El tour ya fue eliminado");
        }
        tour.setDeleted(true);
        tourServicePort.save(tour);
    }   
}