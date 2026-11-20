package com.localy.backend.application.usecase.Guide;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.Optional;

import com.localy.backend.application.dto.request.FileUpload;
import com.localy.backend.application.dto.request.GuideProfileRequest;
import com.localy.backend.application.dto.response.GuideProfileResponse;
import com.localy.backend.application.exception.FileProcessingException;
import com.localy.backend.application.exception.DuplicateGuideApplicationException;
import com.localy.backend.application.exception.DuplicateLicenseException;
import com.localy.backend.application.mapper.GuideProfileMapper;
import com.localy.backend.application.port.input.guide.RequestGuideUseCase;
import com.localy.backend.application.port.output.CloudinaryPort;
import com.localy.backend.application.port.output.GuideRepositoryPort;
import com.localy.backend.application.port.output.UserRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.enums.GuideAvailability;
import com.localy.backend.domain.enums.GuideStatus;
import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuideApplicationImpl implements RequestGuideUseCase {

    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Set<String> PHOTO_EXTENSIONS =Set.of("jpg", "jpeg", "png", "webp");

    private static final Set<String> CV_EXTENSIONS = Set.of("pdf", "doc", "docx");

    private final GuideRepositoryPort guideRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final GuideProfileMapper guideProfileMapper;
    private final CurrentUserPort currentUserPort;
    private final CloudinaryPort cloudinaryPort;

    @Override
    @Transactional
    public GuideProfileResponse execute( GuideProfileRequest guideProfileRequest,FileUpload profilePhoto,FileUpload cv) {

        User currentUser = currentUserPort.getCurrentUser();
        Optional<GuideProfile> existingGuideOptional = guideRepositoryPort.findByUserId(currentUser.getId());
        GuideProfile guide;
        Integer previousReviewCount = 0;
        Double previousAverageRating = 0.0;

        if (existingGuideOptional.isPresent()) {
            GuideProfile existingGuide = existingGuideOptional.get();
            if (existingGuide.getGuideStatus() != GuideStatus.REJECTED) {
                throw new DuplicateGuideApplicationException(
                        "Ya tienes una solicitud de guía pendiente o aprobada"
                );
            }
            previousReviewCount = existingGuide.getReviewCount();
            previousAverageRating = existingGuide.getAverageRating();
            guide = guideProfileMapper.toDomain(guideProfileRequest)
                    .toBuilder()
                    .id(existingGuide.getId())
                    .build();
        } else {
            guide = guideProfileMapper.toDomain(guideProfileRequest);
        }
        if (guideRepositoryPort.existsByLicense(
                guideProfileRequest.license())) {
                    boolean sameLicense = existingGuideOptional
                    .map(existing ->
                            existing.getLicense() != null
                            && existing.getLicense().equals(
                                    guideProfileRequest.license()))
                    .orElse(false);
            if (!sameLicense) {
                throw new DuplicateLicenseException(
                        "La licencia ya está registrada"
                );
            }
        }
        if (profilePhoto != null) {
            validateFile(
                    profilePhoto,
                    PHOTO_EXTENSIONS,
                    "La foto de perfil no es válida"
            );
            try {
                String photoUrl = cloudinaryPort.upload(
                        profilePhoto.contend(),
                        profilePhoto.fileName(),
                        "image"
                );

                currentUser = currentUser.toBuilder()
                        .profilePhoto(photoUrl)
                        .build();

                userRepositoryPort.save(currentUser);

            } catch (Exception e) {
                throw new FileProcessingException(
                        "Error al subir foto de perfil", e
                );
            }
        }
        if (cv == null) {
            throw new IllegalArgumentException("El CV es obligatorio");
        }
        validateFile(
                cv,
                CV_EXTENSIONS,
                "El CV debe ser PDF, DOC o DOCX y pesar máximo 5 MB"
        );
        String cvUrl;
        try {
            cvUrl = cloudinaryPort.upload(
                    cv.contend(),
                    cv.fileName(),
                    "image"
            );
        } catch (Exception e) {
            throw new FileProcessingException(
                    "Error al subir el CV", e
            );
        }
        GuideProfile guideToSave = guide.toBuilder()
                .user(currentUser)
                .cv(cvUrl)
                .guideStatus(GuideStatus.PENDING)
                .guideAvailability(GuideAvailability.AVAILABLE)
                .reviewCount(previousReviewCount)
                .averageRating(previousAverageRating)
                .build();

        GuideProfile savedGuide = guideRepositoryPort.save(guideToSave);
        return guideProfileMapper.toResponse(savedGuide);
    }
    
    private void validateFile(
            FileUpload file,
            Set<String> allowedExtensions,
            String message) {

        if (file.contend() == null
                || file.contend().length == 0
                || file.contend().length > MAX_FILE_SIZE
                || file.fileName() == null) {
            throw new IllegalArgumentException(message);
        }

        String fileName = file.fileName().toLowerCase(Locale.ROOT);

        int extensionStart = fileName.lastIndexOf('.') + 1;

        String extension = extensionStart > 0
                ? fileName.substring(extensionStart)
                : "";

        if (!allowedExtensions.contains(extension)
                || !hasExpectedSignature(file.contend(), extension)) {
            throw new IllegalArgumentException(message);
        }
    }

    private boolean hasExpectedSignature(
            byte[] content,
            String extension) {

        if (Set.of("jpg", "jpeg").contains(extension)) {
            return content.length >= 3
                    && (content[0] & 0xff) == 0xff
                    && (content[1] & 0xff) == 0xd8
                    && (content[2] & 0xff) == 0xff;
        }

        if ("png".equals(extension)) {
            return content.length >= 8
                    && (content[0] & 0xff) == 0x89
                    && content[1] == 'P'
                    && content[2] == 'N'
                    && content[3] == 'G';
        }

        if ("webp".equals(extension)) {
            return content.length >= 12
                    && new String(
                            content, 0, 4, StandardCharsets.US_ASCII
                    ).equals("RIFF")
                    && new String(
                            content, 8, 4, StandardCharsets.US_ASCII
                    ).equals("WEBP");
        }

        if ("pdf".equals(extension)) {
            return content.length >= 5
                    && new String(
                            content, 0, 5, StandardCharsets.US_ASCII
                    ).equals("%PDF-");
        }

        if ("doc".equals(extension)) {
            return content.length >= 8
                    && (content[0] & 0xff) == 0xd0
                    && (content[1] & 0xff) == 0xcf
                    && (content[2] & 0xff) == 0x11
                    && (content[3] & 0xff) == 0xe0;
        }

        return "docx".equals(extension)
                && content.length >= 2
                && content[0] == 'P'
                && content[1] == 'K';
    }
}
