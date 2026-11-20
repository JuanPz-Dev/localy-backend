package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.dto.request.TourServiceFilterRequest;
import com.localy.backend.application.port.input.tour.GetTourServiceUseCase;
import com.localy.backend.application.port.input.tour.SearchTourServicesUseCase;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.Category;
import com.localy.backend.domain.enums.GuideAvailability;
import com.localy.backend.domain.enums.GuideStatus;
import com.localy.backend.domain.enums.Rol;
import com.localy.backend.domain.enums.ServiceStatus;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.GuideEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.LocationEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.ReviewEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.TourServiceEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.UserEntity;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:localy-review-repository-test;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class ReviewJpaRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ReviewJpaRepository reviewJpaRepository;

    @Autowired
    private SearchTourServicesUseCase searchTourServicesUseCase;

    @Autowired
    private GetTourServiceUseCase getTourServiceUseCase;

    @Test
    void summarizesGuideReviewsGloballyAndTourReviewsIndividually() {
        GuideEntity guide = persistGuide("guide@example.com");
        TourServiceEntity firstTour = persistTour(guide, "Tour 1");
        TourServiceEntity secondTour = persistTour(guide, "Tour 2");
        TourServiceEntity tourWithoutReviews = persistTour(guide, "Tour 3");

        persistReview(firstTour, "reviewer1@example.com", 5);
        persistReview(firstTour, "reviewer2@example.com", 3);
        persistReview(secondTour, "reviewer3@example.com", 2);

        entityManager.flush();
        entityManager.clear();

        ReviewJpaRepository.ReviewStatistics guideStatistics =
                reviewJpaRepository.summarizeByGuideId(guide.getId());
        ReviewJpaRepository.ReviewStatistics firstTourStatistics =
                reviewJpaRepository.summarizeByTourServiceId(firstTour.getId());
        var statisticsByTour = reviewJpaRepository.summarizeByTourServiceIds(
                List.of(firstTour.getId(), secondTour.getId()));

        assertEquals(3L, guideStatistics.getReviewCount());
        assertEquals(10.0 / 3.0, guideStatistics.getAverageRating(), 0.0001);
        assertEquals(2L, firstTourStatistics.getReviewCount());
        assertEquals(4.0, firstTourStatistics.getAverageRating());
        assertEquals(2L, statisticsByTour.stream()
                .filter(statistics -> statistics.getTourServiceId().equals(firstTour.getId()))
                .findFirst().orElseThrow().getReviewCount());
        assertEquals(1L, statisticsByTour.stream()
                .filter(statistics -> statistics.getTourServiceId().equals(secondTour.getId()))
                .findFirst().orElseThrow().getReviewCount());

        var searchResults = searchTourServicesUseCase.execute(
                new TourServiceFilterRequest(null, null),
                new PaginationRequest(0, 10));
        var firstTourResponse = searchResults.getContent().stream()
                .filter(tour -> tour.id().equals(firstTour.getId()))
                .findFirst().orElseThrow();
        var secondTourResponse = getTourServiceUseCase.execute(secondTour.getId());
        var emptyTourResponse = getTourServiceUseCase.execute(tourWithoutReviews.getId());

        assertEquals(4.0, firstTourResponse.averageRating());
        assertEquals(2, firstTourResponse.reviewCount());
        assertEquals(2.0, secondTourResponse.averageRating());
        assertEquals(1, secondTourResponse.reviewCount());
        assertNull(emptyTourResponse.averageRating());
        assertEquals(0, emptyTourResponse.reviewCount());
    }

    private GuideEntity persistGuide(String email) {
        UserEntity user = new UserEntity();
        user.setName("Guide");
        user.setLastName("Test");
        user.setEmail(email);
        user.setRoles(Set.of(Rol.USER));
        entityManager.persist(user);

        GuideEntity guide = new GuideEntity();
        guide.setPhone("1234567890");
        guide.setAddress("Test address");
        guide.setLicense("license1");
        guide.setLanguages(List.of("es"));
        guide.setGuideAvailability(GuideAvailability.AVAILABLE);
        guide.setGuideStatus(GuideStatus.CORFIRMED);
        guide.setUser(user);
        entityManager.persist(guide);
        return guide;
    }

    private TourServiceEntity persistTour(GuideEntity guide, String title) {
        LocationEntity location = new LocationEntity();
        location.setCity("Madrid");
        location.setMeetingAddress("Test address");

        TourServiceEntity tour = new TourServiceEntity();
        tour.setTitle(title);
        tour.setDescription("Test tour");
        tour.setPrice(BigDecimal.TEN);
        tour.setMinParticipants(1);
        tour.setMaxParticipants(10);
        tour.setPhotos(List.of());
        tour.setDurationMinutes(60);
        tour.setCategory(Category.CULTURAL);
        tour.setServiceStatus(ServiceStatus.APPROVED);
        tour.setFeatures(List.of());
        tour.setMeetingLocation(location);
        tour.setGuideEntity(guide);
        entityManager.persist(tour);
        return tour;
    }

    private void persistReview(TourServiceEntity tour, String reviewerEmail, int rating) {
        UserEntity reviewer = new UserEntity();
        reviewer.setName("Reviewer");
        reviewer.setLastName("Test");
        reviewer.setEmail(reviewerEmail);
        reviewer.setRoles(Set.of(Rol.USER));
        entityManager.persist(reviewer);

        ReviewEntity review = new ReviewEntity();
        review.setRating(rating);
        review.setComment("Test review");
        review.setUser(reviewer);
        review.setTourService(tour);
        entityManager.persist(review);
    }
}
