package com.localy.backend.application.usecase.Guide;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.dto.response.GuideProfileResponse;
import com.localy.backend.application.mapper.GuideProfileMapper;
import com.localy.backend.application.port.input.guide.GetGuideByIdUseCase;
import com.localy.backend.application.port.output.GuideRepositoryPort;
import com.localy.backend.domain.model.GuideProfile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class GetGuideByIdUseCaseImpl implements GetGuideByIdUseCase{
    
    private final GuideRepositoryPort guideRepositoryPort;
    private final GuideProfileMapper guideProfileMapper;
    
    @Override
    @Transactional(readOnly = true)
    public GuideProfileResponse execute(Long guideId) {
        GuideProfile guideProfile = guideRepositoryPort.findById(guideId)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada" ));
        return guideProfileMapper.toResponse(guideProfile);
    }
}