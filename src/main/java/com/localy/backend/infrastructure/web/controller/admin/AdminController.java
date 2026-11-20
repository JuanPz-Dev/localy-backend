package com.localy.backend.infrastructure.web.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.localy.backend.application.dto.response.PendingGuideResponse;
import com.localy.backend.application.dto.response.UserResponse;
import com.localy.backend.application.dto.response.GuideProfileResponse;
import com.localy.backend.application.port.input.admin.ApproveGuideUseCase;
import com.localy.backend.application.port.input.admin.FindUsersByRoleUseCase;
import com.localy.backend.application.port.input.admin.GetPendingGuidesUseCase;
import com.localy.backend.application.port.input.admin.RejectGuideUseCase;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.Rol;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ApproveGuideUseCase approveGuideUseCase;
    private final RejectGuideUseCase rejectGuideUseCase;
    private final GetPendingGuidesUseCase getPendingGuidesUseCase;
    private final FindUsersByRoleUseCase findUsersByRoleUseCase;

    @GetMapping("/guides/pending")
    public PageResult<PendingGuideResponse> getPendingGuides(PaginationRequest paginationRequest) {
        return getPendingGuidesUseCase.execute(paginationRequest);
    }

    @PutMapping("/guides/{id}/approve")
    public GuideProfileResponse approve(@PathVariable Long id) {
        return approveGuideUseCase.execute(id);
    }

    @PutMapping("/guides/{id}/reject")
    public GuideProfileResponse reject(@PathVariable Long id) {
        return rejectGuideUseCase.execute(id);
    }

    @GetMapping("/by-role")
    public PageResult<UserResponse> getUsersByRole(@RequestParam Rol rol, PaginationRequest paginationRequest) {
        return findUsersByRoleUseCase.execute(rol, paginationRequest);
    }
    
}