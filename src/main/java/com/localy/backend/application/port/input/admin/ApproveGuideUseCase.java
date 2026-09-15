package com.localy.backend.application.port.input.admin;

import com.localy.backend.application.dto.response.GuideProfileResponse;

public interface ApproveGuideUseCase {
    GuideProfileResponse execute(Long guideId);
}