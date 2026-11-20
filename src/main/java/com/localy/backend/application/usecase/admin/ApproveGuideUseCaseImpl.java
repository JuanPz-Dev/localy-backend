package com.localy.backend.application.usecase.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.dto.response.GuideProfileResponse;
import com.localy.backend.application.mapper.GuideProfileMapper;
import com.localy.backend.application.port.input.admin.ApproveGuideUseCase;
import com.localy.backend.application.port.output.GuideRepositoryPort;
import com.localy.backend.application.port.output.UserRepositoryPort;
import com.localy.backend.domain.enums.GuideStatus;
import com.localy.backend.domain.enums.Rol;
import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApproveGuideUseCaseImpl implements ApproveGuideUseCase{

    private final GuideProfileMapper guideProfileMapper;
    private final UserRepositoryPort userRepositoryPort;
    private final GuideRepositoryPort guideRepositoryPort;

    @Override
    @Transactional
    public GuideProfileResponse execute(Long guideId) {

        GuideProfile guide = guideRepositoryPort.findById(guideId)
        .orElseThrow(()-> new IllegalArgumentException("Solicitud de guía no encontrada"));

        if (guide.getGuideStatus() != GuideStatus.PENDING) {
            throw new IllegalArgumentException("La solicitud de guía ya fue procesada");
        }

        User user = guide.getUser();
        user.getRoles().add(Rol.GUIDE);
        userRepositoryPort.save(user);

        GuideProfile saveGuide = guideRepositoryPort.updateStatus(guideId, GuideStatus.CORFIRMED);
        
        return guideProfileMapper.toResponse(saveGuide);
    }   
}