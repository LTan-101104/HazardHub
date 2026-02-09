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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
        registry.add("spring.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
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

    // --- selectRoute tests ---

    private Route buildRoute(String tripId, String polyline, boolean isSelected) {
        return Route.builder()
                .tripId(tripId)
                .polyline(polyline)
                .waypoints(Map.of("origin", "A", "destination", "B"))
                .distanceMeters(1000)
                .durationSeconds(600)
                .safetyScore(0.5)
                .isSelected(isSelected)
                .build();
    }

    @Test
    void selectRoute_SetsTargetSelectedAndDeselectsSiblings() {
        Route r1 = routeRepository.save(buildRoute("trip-1", "poly-1", true));
        Route r2 = routeRepository.save(buildRoute("trip-1", "poly-2", false));
        Route r3 = routeRepository.save(buildRoute("trip-1", "poly-3", false));

        RouteDTO result = routeService.selectRoute(r2.getId());

        assertTrue(result.getIsSelected());
        assertEquals(r2.getId(), result.getId());

        Route persisted1 = routeRepository.findById(r1.getId()).orElseThrow();
        Route persisted2 = routeRepository.findById(r2.getId()).orElseThrow();
        Route persisted3 = routeRepository.findById(r3.getId()).orElseThrow();
        assertFalse(persisted1.getIsSelected());
        assertTrue(persisted2.getIsSelected());
        assertFalse(persisted3.getIsSelected());
    }

    @Test
    void selectRoute_DoesNotAffectRoutesInOtherTrips() {
        Route otherTrip = routeRepository.save(buildRoute("trip-other", "poly-other", true));
        Route r1 = routeRepository.save(buildRoute("trip-1", "poly-1", false));

        routeService.selectRoute(r1.getId());

        Route otherPersisted = routeRepository.findById(otherTrip.getId()).orElseThrow();
        assertTrue(otherPersisted.getIsSelected(), "Route in a different trip should remain unchanged");
    }

    @Test
    void selectRoute_ReselectingSameRouteKeepsItSelected() {
        Route r1 = routeRepository.save(buildRoute("trip-1", "poly-1", true));
        Route r2 = routeRepository.save(buildRoute("trip-1", "poly-2", false));

        RouteDTO result = routeService.selectRoute(r1.getId());

        assertTrue(result.getIsSelected());

        Route persisted1 = routeRepository.findById(r1.getId()).orElseThrow();
        Route persisted2 = routeRepository.findById(r2.getId()).orElseThrow();
        assertTrue(persisted1.getIsSelected());
        assertFalse(persisted2.getIsSelected());
    }

    @Test
    void selectRoute_WithNonExistentId_ThrowsResourceNotFound() {
        assertThrows(hazardhub.com.hub.exception.ResourceNotFoundException.class,
                () -> routeService.selectRoute("nonexistent-id"));
    }

    @Test
    void selectRoute_SingleRouteInTrip_SelectsIt() {
        Route only = routeRepository.save(buildRoute("trip-solo", "poly-solo", false));

        RouteDTO result = routeService.selectRoute(only.getId());

        assertTrue(result.getIsSelected());
        Route persisted = routeRepository.findById(only.getId()).orElseThrow();
        assertTrue(persisted.getIsSelected());
    }

    @Test
    void selectRoute_SwitchSelectionMultipleTimes_OnlyLastSelectedIsTrue() {
        Route r1 = routeRepository.save(buildRoute("trip-1", "poly-1", false));
        Route r2 = routeRepository.save(buildRoute("trip-1", "poly-2", false));
        Route r3 = routeRepository.save(buildRoute("trip-1", "poly-3", false));

        routeService.selectRoute(r1.getId());
        routeService.selectRoute(r2.getId());
        routeService.selectRoute(r3.getId());

        List<Route> allRoutes = routeRepository.findByTripId("trip-1");
        long selectedCount = allRoutes.stream().filter(Route::getIsSelected).count();
        assertEquals(1, selectedCount, "Exactly one route should be selected");

        Route persisted3 = routeRepository.findById(r3.getId()).orElseThrow();
        assertTrue(persisted3.getIsSelected(), "Last selected route should be the selected one");
    }
}
