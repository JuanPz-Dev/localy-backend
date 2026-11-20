package com.localy.backend.application.port.input.guide;

import com.localy.backend.application.dto.response.GuideProfileResponse;
import com.localy.backend.domain.enums.GuideStatus;

public interface ReviewGuideApplicationUseCase {
    GuideProfileResponse execute(Long guideId,GuideStatus guideStatus);
}