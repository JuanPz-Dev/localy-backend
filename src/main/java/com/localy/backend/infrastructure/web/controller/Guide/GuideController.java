package com.localy.backend.infrastructure.web.controller.Guide;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.localy.backend.application.dto.request.FileUpload;
import com.localy.backend.application.dto.request.GuideProfileRequest;
import com.localy.backend.application.dto.response.GuideProfileResponse;
import com.localy.backend.application.port.input.guide.RequestGuideUseCase;
import com.localy.backend.infrastructure.web.mapper.MultipartFileToFileUploadMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/guides")
@RequiredArgsConstructor
public class GuideController {

    private final RequestGuideUseCase requestGuideUseCase;
    private final MultipartFileToFileUploadMapper multipartFileToFileUploadMapper;

    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GuideProfileResponse> apply(
        @Valid @RequestPart("data") GuideProfileRequest guideProfileRequest,
        @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto, 
        @RequestPart(value = "cv" , required = false) MultipartFile cv ) {
        FileUpload fileUpload = profilePhoto == null
            ? null
            : multipartFileToFileUploadMapper.toFileUpload(profilePhoto);
        FileUpload fileUploadcv = cv == null
            ? null
            : multipartFileToFileUploadMapper.toFileUpload(cv);
        GuideProfileResponse guideProfileResponse = requestGuideUseCase.execute(guideProfileRequest, fileUpload, fileUploadcv);
        return ResponseEntity.status(HttpStatus.CREATED).body(guideProfileResponse);
    }
}