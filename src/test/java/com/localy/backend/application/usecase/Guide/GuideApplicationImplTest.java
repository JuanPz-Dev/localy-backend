package com.localy.backend.application.usecase.Guide;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.localy.backend.application.dto.request.FileUpload;
import com.localy.backend.application.dto.request.GuideProfileRequest;
import com.localy.backend.application.dto.response.GuideProfileResponse;
import com.localy.backend.application.exception.DuplicateGuideApplicationException;
import com.localy.backend.application.exception.DuplicateLicenseException;
import com.localy.backend.application.mapper.GuideProfileMapper;
import com.localy.backend.application.port.output.CloudinaryPort;
import com.localy.backend.application.port.output.GuideRepositoryPort;
import com.localy.backend.application.port.output.UserRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.enums.GuideAvailability;
import com.localy.backend.domain.enums.GuideStatus;
import com.localy.backend.domain.enums.Rol;
import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.domain.model.User;

class GuideApplicationImplTest {

    private GuideRepositoryPort guideRepositoryPort;
    private UserRepositoryPort userRepositoryPort;
    private GuideProfileMapper guideProfileMapper;
    private CurrentUserPort currentUserPort;
    private CloudinaryPort cloudinaryPort;
    private GuideApplicationImpl useCase;
    private User user;
    private GuideProfileRequest request;
    private FileUpload cv;

    @BeforeEach
    void setUp() throws Exception {
        guideRepositoryPort = mock(GuideRepositoryPort.class);
        userRepositoryPort = mock(UserRepositoryPort.class);
        guideProfileMapper = mock(GuideProfileMapper.class);
        currentUserPort = mock(CurrentUserPort.class);
        cloudinaryPort = mock(CloudinaryPort.class);
        useCase = new GuideApplicationImpl(
                guideRepositoryPort,
                userRepositoryPort,
                guideProfileMapper,
                currentUserPort,
                cloudinaryPort);

        user = User.builder()
                .id(1L)
                .roles(Set.of(Rol.USER))
                .build();
        request = new GuideProfileRequest(
                "5551234567",
                "Calle 1",
                "LIC-123",
                "Guía local",
                null,
                List.of("es", "en"));
        cv = new FileUpload("%PDF-test".getBytes(), "cv.pdf");

        when(currentUserPort.getCurrentUser()).thenReturn(user);
        when(cloudinaryPort.upload(any(byte[].class), anyString(), anyString()))
                .thenReturn("https://example.test/cv.pdf");
        when(guideProfileMapper.toDomain(request)).thenReturn(
                GuideProfile.builder()
                        .phone(request.phone())
                        .address(request.address())
                        .license(request.license())
                        .description(request.description())
                        .languages(request.languages())
                        .build());
        when(guideProfileMapper.toResponse(any(GuideProfile.class)))
                .thenReturn(new GuideProfileResponse(
                        null, null, null, null, List.of(), null,
                        GuideStatus.PENDING, null, null, null, null, null));
        when(guideRepositoryPort.save(any(GuideProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void resubmitsRejectedProfileWithSameLicenseAndKeepsItsIdentity() throws Exception {
        GuideProfile rejected = GuideProfile.builder()
                .id(42L)
                .user(user)
                .license(request.license())
                .guideStatus(GuideStatus.REJECTED)
                .reviewCount(3)
                .averageRating(4.5)
                .build();
        when(guideRepositoryPort.findByUserId(user.getId()))
                .thenReturn(Optional.of(rejected));
        when(guideRepositoryPort.existsByLicense(request.license())).thenReturn(true);

        useCase.execute(request, null, cv);

        verify(guideRepositoryPort).save(org.mockito.ArgumentMatchers.argThat(saved ->
                saved.getId().equals(rejected.getId())
                        && saved.getUser() == user
                        && saved.getGuideStatus() == GuideStatus.PENDING
                        && saved.getGuideAvailability() == GuideAvailability.AVAILABLE
                        && saved.getLicense().equals(request.license())
                        && saved.getLanguages().equals(request.languages())
                        && saved.getReviewCount() == rejected.getReviewCount()
                        && saved.getAverageRating().equals(rejected.getAverageRating())));
        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    void rejectsAnotherApplicationWhenExistingProfileIsPendingOrApproved() {
        for (GuideStatus status : List.of(GuideStatus.PENDING, GuideStatus.CORFIRMED)) {
            when(guideRepositoryPort.findByUserId(user.getId())).thenReturn(
                    Optional.of(GuideProfile.builder().guideStatus(status).build()));

            assertThrows(DuplicateGuideApplicationException.class,
                    () -> useCase.execute(request, null, cv));
        }

        verify(guideRepositoryPort, never()).save(any(GuideProfile.class));
    }

    @Test
    void rejectsLicenseOwnedByAnotherProfile() {
        GuideProfile rejected = GuideProfile.builder()
                .id(42L)
                .user(user)
                .license("LIC-OLD")
                .guideStatus(GuideStatus.REJECTED)
                .build();
        when(guideRepositoryPort.findByUserId(user.getId()))
                .thenReturn(Optional.of(rejected));
        when(guideRepositoryPort.existsByLicense(request.license())).thenReturn(true);

        assertThrows(DuplicateLicenseException.class,
                () -> useCase.execute(request, null, cv));

        verify(guideRepositoryPort, never()).save(any(GuideProfile.class));
    }
}
