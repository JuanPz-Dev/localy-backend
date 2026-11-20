package com.localy.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.localy.backend.application.dto.request.TourServiceFilterRequest;
import com.localy.backend.application.port.input.tour.GetTourServiceUseCase;
import com.localy.backend.application.port.input.tour.SearchTourServicesUseCase;
import com.localy.backend.domain.common.PaginationRequest;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:localy-demo-seed-test;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.jpa.open-in-view=false"
})
class DemoDataSqlTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SearchTourServicesUseCase searchTourServicesUseCase;

    @Autowired
    private GetTourServiceUseCase getTourServiceUseCase;

    @Test
    void importsDemoProfilesServicesAndSearchesToursWithoutOpenInView() {
        new ResourceDatabasePopulator(new FileSystemResource("demo/localy-demo.sql"))
            .execute(jdbcTemplate.getDataSource());

        assertEquals(4, count("SELECT COUNT(*) FROM users WHERE email LIKE '%@localy.invalid'"));
        assertEquals(1, count("SELECT COUNT(*) FROM users WHERE name = 'Ana Sofía' AND last_name = 'Herrera'"));
        assertEquals(4, count("SELECT COUNT(*) FROM guides"));
        assertEquals(8, count("SELECT COUNT(*) FROM guide_languages"));
        assertEquals(4, count("SELECT COUNT(*) FROM tour_services"));
        assertEquals(4, count("SELECT COUNT(*) FROM tour_service_photos"));
        assertEquals(12, count("SELECT COUNT(*) FROM tour_service_features"));
        assertEquals(8, count("SELECT COUNT(*) FROM tour_schedules"));
        assertEquals(0, count("""
            SELECT COUNT(*) FROM tour_services
            WHERE price < 50000 OR price > 200000
               OR duration_minutes < 60 OR duration_minutes > 360
               OR min_participants < 1 OR max_participants < min_participants
               OR meeting_city <> 'Cartagena de Indias'
            """));
        assertEquals(0, count("SELECT COUNT(*) FROM reviews"));

        var tours = searchTourServicesUseCase.execute(
            new TourServiceFilterRequest(null, null),
            new PaginationRequest(0, 12)
        );

        assertEquals(4, tours.getContent().size());
        assertFalse(tours.getContent().stream().anyMatch(tour -> tour.guideName() == null));
        assertNotNull(tours.getContent().get(0).guideName());

        Long tourId = jdbcTemplate.queryForObject(
            "SELECT id FROM tour_services ORDER BY id LIMIT 1",
            Long.class
        );
        assertNotNull(getTourServiceUseCase.execute(tourId).guideName());
    }

    private int count(String sql) {
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
