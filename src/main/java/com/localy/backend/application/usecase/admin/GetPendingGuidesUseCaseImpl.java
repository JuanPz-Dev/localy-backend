package com.localy.backend.application.usecase.admin;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.dto.response.PendingGuideResponse;
import com.localy.backend.application.mapper.GuideProfileMapper;
import com.localy.backend.application.port.input.admin.GetPendingGuidesUseCase;
import com.localy.backend.application.port.output.GuideRepositoryPort;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.GuideStatus;
import com.localy.backend.domain.model.GuideProfile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetPendingGuidesUseCaseImpl implements GetPendingGuidesUseCase{

    private final GuideProfileMapper guideProfileMapper;
    private final GuideRepositoryPort guideRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public PageResult<PendingGuideResponse> execute(PaginationRequest paginationRequest) {
        PageResult<GuideProfile> pendingGuides = guideRepositoryPort.findByGuideStatus(GuideStatus.PENDING, paginationRequest);
        List<PendingGuideResponse> responses = pendingGuides.getContent()
        .stream()
        .map(guideProfileMapper::toPendingResponse)
        .toList(); 
        return new PageResult<>(responses,pendingGuides.getPage(),pendingGuides.getSize(),pendingGuides.getTotalElements());
    }
}