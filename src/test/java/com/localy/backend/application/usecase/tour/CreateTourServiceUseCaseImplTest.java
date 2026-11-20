package com.localy.backend.application.usecase.tour;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.localy.backend.application.dto.request.TourServiceRequest;
import com.localy.backend.application.exception.BusinessRuleException;
import com.localy.backend.application.mapper.TourServiceMapper;
import com.localy.backend.application.port.output.CloudinaryPort;
import com.localy.backend.application.port.output.GuideRepositoryPort;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.enums.GuideStatus;
import com.localy.backend.domain.enums.ServiceStatus;
import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.domain.model.TourService;
import com.localy.backend.domain.model.User;

class CreateTourServiceUseCaseImplTest {

    private CurrentUserPort currentUserPort;
    private GuideRepositoryPort guideRepositoryPort;
    private TourServiceRepositoryPort tourServiceRepositoryPort;
    private TourServiceMapper tourServiceMapper;
    private CloudinaryPort cloudinaryPort;
    private CreateTourServiceUseCaseImpl useCase;
    private User user;

    @BeforeEach
    void setUp() {
        currentUserPort = mock(CurrentUserPort.class);
        guideRepositoryPort = mock(GuideRepositoryPort.class);
        tourServiceRepositoryPort = mock(TourServiceRepositoryPort.class);
        tourServiceMapper = mock(TourServiceMapper.class);
        cloudinaryPort = mock(CloudinaryPort.class);
        useCase = new CreateTourServiceUseCaseImpl(
                tourServiceRepositoryPort,
                tourServiceMapper,
                guideRepositoryPort,
                currentUserPort,
                cloudinaryPort);
        user = User.builder().id(1L).build();
        when(currentUserPort.getCurrentUser()).thenReturn(user);
    }

    @Test
    void createsTourForApprovedGuide() {
        GuideProfile approvedGuide = GuideProfile.builder()
                .id(2L)
                .user(user)
                .guideStatus(GuideStatus.CORFIRMED)
                .build();
        when(guideRepositoryPort.findByUserId(user.getId())).thenReturn(Optional.of(approvedGuide));
        when(tourServiceMapper.toDomain(any(TourServiceRequest.class))).thenReturn(new TourService());
        when(tourServiceRepositoryPort.save(any(TourService.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(request(), List.of());

        verify(tourServiceRepositoryPort).save(org.mockito.ArgumentMatchers.argThat(tour ->
                tour.getGuideProfile() == approvedGuide
                        && tour.getServiceStatus() == ServiceStatus.APPROVED));
    }

    @Test
    void rejectsTourCreationForPendingGuide() throws Exception {
        GuideProfile pendingGuide = GuideProfile.builder()
                .user(user)
                .guideStatus(GuideStatus.PENDING)
                .build();
        when(guideRepositoryPort.findByUserId(user.getId())).thenReturn(Optional.of(pendingGuide));

        assertThrows(BusinessRuleException.class, () -> useCase.execute(request(), List.of()));

        verify(tourServiceRepositoryPort, never()).save(any());
        verify(cloudinaryPort, never()).upload(any(byte[].class), any(String.class));
    }

    @Test
    void rejectsTourCreationForUserWithoutGuideProfile() {
        when(guideRepositoryPort.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessRuleException.class, () -> useCase.execute(request(), List.of()));

        verify(tourServiceRepositoryPort, never()).save(any());
        verify(tourServiceMapper, never()).toDomain(any(TourServiceRequest.class));
    }

    private TourServiceRequest request() {
        return new TourServiceRequest(null, null, null, null, null, List.of(),
                null, null, List.of(), null);
    }
}
