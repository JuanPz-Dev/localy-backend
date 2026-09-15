package com.localy.backend.application.port.input.guide;

import com.localy.backend.application.dto.request.FileUpload;
import com.localy.backend.application.dto.request.GuideProfileRequest;
import com.localy.backend.application.dto.response.GuideProfileResponse;

public interface RequestGuideUseCase {
    GuideProfileResponse execute(GuideProfileRequest guideProfileRequest,FileUpload profilePhoto,FileUpload cv);
}