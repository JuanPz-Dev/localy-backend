package com.localy.backend.application.usecase.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.dto.response.GuideProfileResponse;
import com.localy.backend.application.mapper.GuideProfileMapper;
import com.localy.backend.application.port.input.admin.RejectGuideUseCase;
import com.localy.backend.application.port.output.GuideRepositoryPort;
import com.localy.backend.domain.enums.GuideStatus;
import com.localy.backend.domain.model.GuideProfile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RejectGuideUseCaseImpl implements RejectGuideUseCase{
    
    private final GuideProfileMapper guideProfileMapper;
    private final GuideRepositoryPort guideRepositoryPort;

    @Override
    @Transactional
    public GuideProfileResponse execute(Long guideId) {
        GuideProfile guide = guideRepositoryPort.findById(guideId)
        .orElseThrow(()-> new IllegalArgumentException("Solicitud de guía no encontrada"));

        if (guide.getGuideStatus()!=GuideStatus.PENDING) {
            throw new IllegalArgumentException("La solicitud de guía ya fue procesada");
        }

        GuideProfile saveGuide = guideRepositoryPort.updateStatus(guideId, GuideStatus.REJECTED);
        return guideProfileMapper.toResponse(saveGuide);
    }
}