package com.localy.backend.application.port.input.guide;

import com.localy.backend.application.dto.response.GuideProfileResponse;

public interface SuspendGuideUseCase {
    GuideProfileResponse execute(Long guideId);
}