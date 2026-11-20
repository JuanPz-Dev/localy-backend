package com.localy.backend.application.port.input.admin;

import com.localy.backend.application.dto.response.PendingGuideResponse;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;

public interface GetPendingGuidesUseCase {
    PageResult<PendingGuideResponse> execute(PaginationRequest paginationRequest);
}