/*package com.localy.backend.application.usecase.reservation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.localy.backend.application.exception.BusinessRuleException;
import com.localy.backend.application.dto.request.ReservationRequest;
import com.localy.backend.application.dto.response.ReservationResponse;
import com.localy.backend.application.mapper.ReservationMapper;
import com.localy.backend.application.mapper.ReservationMapperImpl;
import com.localy.backend.application.port.output.GuideRepositoryPort;
import com.localy.backend.application.port.output.ReservationRepositoryPort;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.ReservationStatus;
import com.localy.backend.domain.model.GuideProfile;
import com.localy.backend.domain.model.Reservation;
import com.localy.backend.domain.model.TourService;
import com.localy.backend.domain.model.User;
import com.localy.backend.infrastructure.adapter.output.persistence.mapper.TourSchedulePersistenceMapperImpl;

class ReservationUseCasesTest {

    private CurrentUserPort currentUserPort;
    private GuideRepositoryPort guideRepositoryPort;
    private ReservationRepositoryPort reservationRepositoryPort;
    private ReservationMapper reservationMapper;

    @BeforeEach
    void setUp() {
        currentUserPort = mock(CurrentUserPort.class);
        guideRepositoryPort = mock(GuideRepositoryPort.class);
        reservationRepositoryPort = mock(ReservationRepositoryPort.class);
        reservationMapper = mock(ReservationMapper.class);
    }

    @Test
    void createReservationRejectsInvalidParticipantCounts() {
        TourServiceRepositoryPort tourServiceRepositoryPort = mock(TourServiceRepositoryPort.class);
        CreateReservationUseCaseImpl useCase = new CreateReservationUseCaseImpl(
                currentUserPort, reservationMapper, reservationRepositoryPort, tourServiceRepositoryPort);

        when(currentUserPort.getCurrentUser()).thenReturn(user(1L));

        assertThrows(BusinessRuleException.class, () -> useCase.execute(4L, null));
        assertThrows(BusinessRuleException.class,
                () -> useCase.execute(4L, new ReservationRequest(null, null, 0, 0)));
        assertThrows(BusinessRuleException.class,
                () -> useCase.execute(4L, new ReservationRequest(null, null, 1, -1)));
        verify(tourServiceRepositoryPort, never()).findById(4L);
    }

    @Test
    void createReservationRejectsOwnTourAndParticipantsAboveLimit() {
        TourServiceRepositoryPort tourServiceRepositoryPort = mock(TourServiceRepositoryPort.class);
        CreateReservationUseCaseImpl useCase = new CreateReservationUseCaseImpl(
                currentUserPort, reservationMapper, reservationRepositoryPort, tourServiceRepositoryPort);
        User currentUser = user(1L);
        TourService tourService = new TourService();
        tourService.setGuideProfile(GuideProfile.builder().user(user(2L)).build());
        tourService.setMinParticipants(1);
        tourService.setMaxParticipants(2);
        when(currentUserPort.getCurrentUser()).thenReturn(currentUser);
        when(tourServiceRepositoryPort.findById(4L)).thenReturn(Optional.of(tourService));

        assertThrows(BusinessRuleException.class,
                () -> useCase.execute(4L, new ReservationRequest(null, null, 2, 1)));

        tourService.getGuideProfile().setUser(currentUser);
        assertThrows(BusinessRuleException.class,
                () -> useCase.execute(4L, new ReservationRequest(null, null, 1, 0)));
        verify(reservationRepositoryPort, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createReservationRejectsScheduleNotAvailableOnRequestedDate() {
        TourServiceRepositoryPort tourServiceRepositoryPort = mock(TourServiceRepositoryPort.class);
        CreateReservationUseCaseImpl useCase = new CreateReservationUseCaseImpl(
                currentUserPort, reservationMapper, reservationRepositoryPort, tourServiceRepositoryPort);
        TourService tourService = tourService();
        when(currentUserPort.getCurrentUser()).thenReturn(user(1L));
        when(tourServiceRepositoryPort.findById(4L)).thenReturn(Optional.of(tourService));

        assertThrows(BusinessRuleException.class,
                () -> useCase.execute(4L, new ReservationRequest(
                        LocalDate.of(2026, 10, 13), LocalTime.of(9, 30), 1, 0)));
        assertThrows(BusinessRuleException.class,
                () -> useCase.execute(4L, new ReservationRequest(
                        LocalDate.of(2026, 10, 12), LocalTime.of(10, 0), 1, 0)));
        verify(reservationRepositoryPort, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createReservationSavesSelectedScheduleAndReturnsItsTime() {
        TourServiceRepositoryPort tourServiceRepositoryPort = mock(TourServiceRepositoryPort.class);
        CreateReservationUseCaseImpl useCase = new CreateReservationUseCaseImpl(
                currentUserPort, reservationMapper, reservationRepositoryPort, tourServiceRepositoryPort);
        User currentUser = user(1L);
        TourService tourService = tourService();
        Reservation mappedReservation = new Reservation();
        ReservationResponse mappedResponse = new ReservationResponse(
                9L, 4L, "Tour", "Guide", "Tourist",
                LocalDate.of(2026, 10, 12), LocalTime.of(9, 30),
                1, 0, BigDecimal.TEN, ReservationStatus.PENDING, null);

        when(currentUserPort.getCurrentUser()).thenReturn(currentUser);
        when(tourServiceRepositoryPort.findById(4L)).thenReturn(Optional.of(tourService));
        when(reservationMapper.toDomain(any(ReservationRequest.class))).thenReturn(mappedReservation);
        when(reservationRepositoryPort.save(mappedReservation)).thenReturn(mappedReservation);
        when(reservationMapper.toResponse(mappedReservation)).thenReturn(mappedResponse);

        ReservationResponse response = useCase.execute(4L, new ReservationRequest(
                LocalDate.of(2026, 10, 12), LocalTime.of(9, 30), 1, 0));

        assertEquals(tourService.getSchedules().get(0), mappedReservation.getTourSchedule());
        assertEquals(LocalTime.of(9, 30), response.startTime());
        verify(reservationRepositoryPort).save(mappedReservation);
    }

    @Test
    void reservationResponseIncludesTheSavedScheduleTime() {
        Reservation reservation = new Reservation();
        reservation.setTourSchedule(new com.localy.backend.domain.model.TourSchedule(
                17L, DayOfWeek.MONDAY, LocalTime.of(9, 30)));

        ReservationResponse response = new ReservationMapperImpl().toResponse(reservation);
        assertEquals(LocalTime.of(9, 30), response.startTime());
    }

    @Test
    void schedulePersistenceMappingPreservesExistingScheduleId() {
        com.localy.backend.domain.model.TourSchedule schedule =
                new com.localy.backend.domain.model.TourSchedule(
                        17L, DayOfWeek.MONDAY, LocalTime.of(9, 30));

        var entity = new TourSchedulePersistenceMapperImpl().toEntity(schedule);

        assertEquals(17L, entity.getId());
    }

    @Test
    void reservationReadAndCancellationRejectOtherUsers() {
        User currentUser = user(1L);
        Reservation reservation = new Reservation();
        reservation.setUser(user(2L));
        when(currentUserPort.getCurrentUser()).thenReturn(currentUser);
        when(reservationRepositoryPort.findById(9L)).thenReturn(Optional.of(reservation));

        GetReservationUseCaseImpl getUseCase = new GetReservationUseCaseImpl(
                currentUserPort, reservationMapper, reservationRepositoryPort);
        CancelReservationUseCaseImpl cancelUseCase = new CancelReservationUseCaseImpl(
                currentUserPort, reservationMapper, reservationRepositoryPort);

        assertThrows(BusinessRuleException.class, () -> getUseCase.execute(9L));
        assertThrows(BusinessRuleException.class, () -> cancelUseCase.execute(9L));
        verify(reservationRepositoryPort, never()).save(reservation);
    }

    @Test
    void cancelReservationRejectsNonPendingReservation() {
        Reservation reservation = new Reservation();
        reservation.setUser(user(1L));
        reservation.setReservationStatus(ReservationStatus.PAID);
        when(currentUserPort.getCurrentUser()).thenReturn(user(1L));
        when(reservationRepositoryPort.findById(9L)).thenReturn(Optional.of(reservation));

        CancelReservationUseCaseImpl useCase = new CancelReservationUseCaseImpl(
                currentUserPort, reservationMapper, reservationRepositoryPort);

        assertThrows(BusinessRuleException.class, () -> useCase.execute(9L));
        verify(reservationRepositoryPort, never()).save(reservation);
    }

    @Test
    void ownerCanCancelPendingReservation() {
        Reservation reservation = new Reservation();
        reservation.setUser(user(1L));
        reservation.setReservationStatus(ReservationStatus.PENDING);
        when(currentUserPort.getCurrentUser()).thenReturn(user(1L));
        when(reservationRepositoryPort.findById(9L)).thenReturn(Optional.of(reservation));
        when(reservationRepositoryPort.save(reservation)).thenReturn(reservation);

        CancelReservationUseCaseImpl useCase = new CancelReservationUseCaseImpl(
                currentUserPort, reservationMapper, reservationRepositoryPort);

        useCase.execute(9L);

        assertEquals(ReservationStatus.CANCELLED, reservation.getReservationStatus());
        verify(reservationRepositoryPort).save(reservation);
    }

    @Test
    void guideReservationsUseGuideProfileIdInsteadOfUserId() {
        User user = user(1L);
        GuideProfile guide = GuideProfile.builder().id(23L).user(user).build();
        PaginationRequest paginationRequest = new PaginationRequest(0, 10);
        when(currentUserPort.getCurrentUser()).thenReturn(user);
        when(guideRepositoryPort.findByUserId(1L)).thenReturn(Optional.of(guide));
        when(reservationRepositoryPort.findByGuide(23L, paginationRequest))
                .thenReturn(new PageResult<>(List.of(), 0, 10, 0));

        GetGuideReservationsUseCaseImpl useCase = new GetGuideReservationsUseCaseImpl(
                currentUserPort, reservationMapper, reservationRepositoryPort, guideRepositoryPort);

        useCase.execute(paginationRequest);

        verify(reservationRepositoryPort).findByGuide(23L, paginationRequest);
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private TourService tourService() {
        TourService tourService = new TourService();
        tourService.setGuideProfile(GuideProfile.builder().user(user(2L)).build());
        tourService.setMinParticipants(1);
        tourService.setMaxParticipants(5);
        tourService.setPrice(BigDecimal.TEN);
        tourService.setSchedules(List.of(
                new com.localy.backend.domain.model.TourSchedule(null, DayOfWeek.MONDAY, LocalTime.of(9, 30))));
        return tourService;
    }
}*/
