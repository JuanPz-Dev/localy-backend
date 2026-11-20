package com.localy.backend.application.port.input.guide;

import com.localy.backend.application.dto.response.GuideProfileResponse;

public interface GetGuideByIdUseCase {
    GuideProfileResponse execute(Long guideId);
}