package com.localy.backend.application.usecase.tour;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.localy.backend.application.dto.request.FileUpload;
import com.localy.backend.application.dto.request.TourServiceRequest;
import com.localy.backend.application.dto.response.TourServiceResponse;
import com.localy.backend.application.exception.BusinessRuleException;
import com.localy.backend.application.mapper.TourServiceMapper;
import com.localy.backend.application.port.output.CloudinaryPort;
import com.localy.backend.application.port.output.GuideRepositoryPort;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.domain.model.TourService;
import com.localy.backend.domain.model.User;

class UpdateTourServiceUseCaseImplTest {

    private CurrentUserPort currentUserPort;
    private GuideRepositoryPort guideRepositoryPort;
    private TourServiceRepositoryPort tourServiceRepositoryPort;
    private TourServiceMapper tourServiceMapper;
    private CloudinaryPort cloudinaryPort;
    private UpdateTourServiceUseCaseImpl useCase;
    private GuideProfile guide;
    private TourService tour;

    @BeforeEach
    void setUp() throws Exception {
        currentUserPort = mock(CurrentUserPort.class);
        guideRepositoryPort = mock(GuideRepositoryPort.class);
        tourServiceRepositoryPort = mock(TourServiceRepositoryPort.class);
        tourServiceMapper = mock(TourServiceMapper.class);
        cloudinaryPort = mock(CloudinaryPort.class);
        useCase = new UpdateTourServiceUseCaseImpl(
                tourServiceRepositoryPort,
                tourServiceMapper,
                guideRepositoryPort,
                currentUserPort,
                cloudinaryPort);

        User user = User.builder().id(1L).build();
        guide = GuideProfile.builder().id(2L).user(user).build();
        tour = TourService.builder()
                .id(3L)
                .guideProfile(guide)
                .photos(List.of("https://res.cloudinary.com/demo/image/upload/v123/kept.jpg",
                        "https://res.cloudinary.com/demo/image/upload/v123/removed.jpg"))
                .build();
        when(currentUserPort.getCurrentUser()).thenReturn(user);
        when(guideRepositoryPort.findByUserId(user.getId())).thenReturn(Optional.of(guide));
        when(tourServiceRepositoryPort.findById(tour.getId())).thenReturn(Optional.of(tour));
        when(tourServiceRepositoryPort.save(any(TourService.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(cloudinaryPort.upload(any(byte[].class), anyString())).thenReturn(
                "https://res.cloudinary.com/demo/image/upload/v456/new-photo.jpg");
        when(tourServiceMapper.toResponse(any(TourService.class))).thenReturn(mock(TourServiceResponse.class));
    }

    @Test
    void retainsSelectedPhotosUploadsNewPhotosAndDeletesRemovedPhotosAfterSave() throws Exception {
        String retainedPhoto = tour.getPhotos().get(0);
        String removedPhoto = tour.getPhotos().get(1);
        FileUpload newPhoto = new FileUpload(new byte[] {1}, "new-photo.jpg");

        useCase.execute(3L, request(), List.of(newPhoto), List.of(retainedPhoto));

        verify(tourServiceRepositoryPort).save(argThat(saved ->
                saved.getPhotos().equals(List.of(retainedPhoto,
                        "https://res.cloudinary.com/demo/image/upload/v456/new-photo.jpg"))));
        InOrder operations = inOrder(tourServiceRepositoryPort, cloudinaryPort);
        operations.verify(tourServiceRepositoryPort).save(any(TourService.class));
        operations.verify(cloudinaryPort).deleteByUrl(removedPhoto);
        verify(cloudinaryPort, never()).deleteByUrl(retainedPhoto);
    }

    @Test
    void rejectsUpdateWithoutAnyPhotos() throws Exception {
        assertThrows(BusinessRuleException.class,
                () -> useCase.execute(3L, request(), List.of(), List.of()));

        verify(tourServiceRepositoryPort, never()).save(any());
        verify(cloudinaryPort, never()).upload(any(byte[].class), anyString());
    }

    @Test
    void rejectsPhotoReferencesThatDoNotBelongToTheTour() throws Exception {
        assertThrows(BusinessRuleException.class, () -> useCase.execute(
                3L,
                request(),
                List.of(),
                List.of("https://res.cloudinary.com/demo/image/upload/v123/other.jpg")));

        verify(tourServiceRepositoryPort, never()).save(any());
        verify(cloudinaryPort, never()).deleteByUrl(anyString());
    }

    @Test
    void rejectsUpdatesThatWouldExceedFivePhotos() throws Exception {
        List<FileUpload> newPhotos = List.of(
                new FileUpload(new byte[] {1}, "one.jpg"),
                new FileUpload(new byte[] {2}, "two.jpg"),
                new FileUpload(new byte[] {3}, "three.jpg"),
                new FileUpload(new byte[] {4}, "four.jpg"));

        assertThrows(BusinessRuleException.class,
                () -> useCase.execute(3L, request(), newPhotos, tour.getPhotos()));

        verify(tourServiceRepositoryPort, never()).save(any());
        verify(cloudinaryPort, never()).upload(any(byte[].class), anyString());
    }

    @Test
    void rejectsUpdatesFromAnotherGuide() throws Exception {
        tour.setGuideProfile(GuideProfile.builder().id(99L).build());

        assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(3L, request(), List.of(), List.of(tour.getPhotos().get(0))));

        verify(tourServiceRepositoryPort, never()).save(any());
        verify(cloudinaryPort, never()).upload(any(byte[].class), anyString());
        verify(cloudinaryPort, never()).deleteByUrl(anyString());
    }

    @Test
    void doesNotDeletePhotosStillUsedByAnotherTour() throws Exception {
        String sharedPhoto = tour.getPhotos().get(1);
        when(tourServiceRepositoryPort.isPhotoUsedByAnotherTour(sharedPhoto, tour.getId()))
                .thenReturn(true);

        useCase.execute(3L, request(), List.of(), List.of(tour.getPhotos().get(0)));

        verify(tourServiceRepositoryPort).isPhotoUsedByAnotherTour(sharedPhoto, tour.getId());
        verify(cloudinaryPort, never()).deleteByUrl(anyString());
    }

    @Test
    void compensatesUploadedPhotosWhenPersistenceFails() throws Exception {
        String newPhotoUrl = "https://res.cloudinary.com/demo/image/upload/v456/new-photo.jpg";
        String removedPhotoUrl = tour.getPhotos().get(1);
        when(tourServiceRepositoryPort.save(any(TourService.class)))
                .thenThrow(new IllegalStateException("Database unavailable"));

        assertThrows(IllegalStateException.class, () -> useCase.execute(
                3L,
                request(),
                List.of(new FileUpload(new byte[] {1}, "new-photo.jpg")),
                List.of(tour.getPhotos().get(0))));

        verify(cloudinaryPort).deleteByUrl(newPhotoUrl);
        verify(cloudinaryPort, never()).deleteByUrl(removedPhotoUrl);
    }

    @Test
    void compensatesEarlierUploadsWhenALaterUploadFails() throws Exception {
        String uploadedPhotoUrl = "https://res.cloudinary.com/demo/image/upload/v456/new-photo.jpg";
        when(cloudinaryPort.upload(any(byte[].class), anyString()))
                .thenReturn(uploadedPhotoUrl)
                .thenThrow(new IOException("Cloudinary unavailable"));

        assertThrows(IllegalStateException.class, () -> useCase.execute(
                3L,
                request(),
                List.of(
                        new FileUpload(new byte[] {1}, "first.jpg"),
                        new FileUpload(new byte[] {2}, "second.jpg")),
                List.of()));

        verify(cloudinaryPort).deleteByUrl(uploadedPhotoUrl);
        verify(tourServiceRepositoryPort, never()).save(any());
    }

    private TourServiceRequest request() {
        return new TourServiceRequest(null, null, null, null, null, List.of(),
                null, null, List.of(), null);
    }
}
