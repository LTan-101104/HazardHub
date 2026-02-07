package hazardhub.com.hub.service.impl;

import hazardhub.com.hub.model.dto.RouteDTO;
import hazardhub.com.hub.model.entity.Route;
import hazardhub.com.hub.repository.RouteRepository;
import hazardhub.com.hub.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=",
        "firebase.enabled=false"
})
@ActiveProfiles({"integration", "test"})
@Testcontainers
class RouteServiceImplIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private RouteServiceImpl routeService;

    @Autowired
    private RouteRepository routeRepository;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        routeRepository.deleteAll();
    }

    @Test
    void update_WithDifferentTripId_KeepsOriginalTripId() {
        Route saved = routeRepository.save(Route.builder()
                .tripId("trip-original")
                .polyline("polyline-old")
                .waypoints(java.util.Map.of("origin", "A", "destination", "B"))
                .distanceMeters(1000)
                .durationSeconds(600)
                .safetyScore(0.7)
                .isSelected(false)
                .build());

        RouteDTO updateDto = RouteDTO.builder()
                .tripId("trip-updated-should-not-change")
                .polyline("polyline-new")
                .waypoints(java.util.Map.of("origin", "A", "destination", "C"))
                .distanceMeters(1200)
                .durationSeconds(700)
                .safetyScore(0.8)
                .build();

        RouteDTO updated = routeService.update(saved.getId(), updateDto);

        assertEquals("trip-original", updated.getTripId());
        assertEquals("polyline-new", updated.getPolyline());

        Route persisted = routeRepository.findById(saved.getId()).orElseThrow();
        assertEquals("trip-original", persisted.getTripId());
        assertEquals("polyline-new", persisted.getPolyline());
    }
}
