package com.localy.backend.application.usecase.tour;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.dto.response.TourServiceResponse;
import com.localy.backend.application.mapper.TourServiceMapper;
import com.localy.backend.application.port.input.tour.GetMyTourServicesUseCase;
import com.localy.backend.application.port.output.GuideRepositoryPort;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.domain.model.TourService;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class GetMyTourServicesUseCaseImpl implements GetMyTourServicesUseCase {
    
    private final CurrentUserPort currentUserPort;
    private final TourServiceRepositoryPort tourServicePort;
    private final TourServiceMapper tourServiceMapper;
    private final GuideRepositoryPort guideRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<TourServiceResponse> getMyTourServices(PaginationRequest pageRequest) {
        User user = currentUserPort.getCurrentUser();
        GuideProfile guide = guideRepositoryPort.findByUserId(user.getId())
        .orElseThrow(() -> new IllegalArgumentException("El usuario no es un guia"));
        PageResult<TourService> PageResult = tourServicePort.findByGuideProfileId(guide.getId(), pageRequest);
        return PageResult.getContent().stream()
        .map(tourServiceMapper::toResponse)
        .toList();
    }
}