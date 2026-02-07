package hazardhub.com.hub.controller;

import hazardhub.com.hub.exception.ResourceNotFoundException;
import hazardhub.com.hub.mapper.RouteMapper;
import hazardhub.com.hub.model.dto.RouteDTO;
import hazardhub.com.hub.model.entity.Route;
import hazardhub.com.hub.service.HazardService;
import hazardhub.com.hub.service.RouteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @MockitoBean
    private RouteService routeService;

    @MockitoBean
    @SuppressWarnings("unused")
    private HazardService hazardService;

    // ── Helper builders ───────────────────────────────────────────────

    private RouteDTO validInput() {
        return RouteDTO.builder()
                .tripId("trip-001")
                .polyline("encodedPolylineString")
                .waypoints(Map.of("origin", "A", "destination", "B"))
                .distanceMeters(5000)
                .durationSeconds(600)
                .safetyScore(0.85)
                .safetyAnalysis(Map.of("level", "moderate"))
                .hazardsConsidered(List.of("hazard-1", "hazard-2"))
                .build();
    }

    private RouteDTO savedRoute(String id, boolean selected) {
        return RouteDTO.builder()
                .id(id)
                .tripId("trip-001")
                .polyline("encodedPolylineString")
                .waypoints(Map.of("origin", "A", "destination", "B"))
                .distanceMeters(5000)
                .durationSeconds(600)
                .safetyScore(0.85)
                .safetyAnalysis(Map.of("level", "moderate"))
                .hazardsConsidered(List.of("hazard-1", "hazard-2"))
                .isSelected(selected)
                .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
                .updatedAt(Instant.parse("2026-01-01T00:00:00Z"))
                .build();
    }

    // ── POST /api/v1/routes ───────────────────────────────────────────

    @Test
    void create_WithValidDTO_ReturnsCreated() throws Exception {
        RouteDTO input = validInput();
        RouteDTO saved = savedRoute("route-001", false);

        when(routeService.create(any(RouteDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("route-001"))
                .andExpect(jsonPath("$.tripId").value("trip-001"))
                .andExpect(jsonPath("$.polyline").value("encodedPolylineString"))
                .andExpect(jsonPath("$.distanceMeters").value(5000))
                .andExpect(jsonPath("$.durationSeconds").value(600))
                .andExpect(jsonPath("$.safetyScore").value(0.85))
                .andExpect(jsonPath("$.isSelected").value(false));
    }

    @Test
    void create_WithNullTripId_ReturnsBadRequest() throws Exception {
        RouteDTO input = validInput();
        input.setTripId(null);

        mockMvc.perform(post("/api/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithBlankPolyline_ReturnsBadRequest() throws Exception {
        RouteDTO input = validInput();
        input.setPolyline("");

        mockMvc.perform(post("/api/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithNullWaypoints_ReturnsBadRequest() throws Exception {
        RouteDTO input = validInput();
        input.setWaypoints(null);

        mockMvc.perform(post("/api/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithNullDistance_ReturnsBadRequest() throws Exception {
        RouteDTO input = validInput();
        input.setDistanceMeters(null);

        mockMvc.perform(post("/api/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithNegativeDistance_ReturnsBadRequest() throws Exception {
        RouteDTO input = validInput();
        input.setDistanceMeters(-100);

        mockMvc.perform(post("/api/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithZeroDistance_ReturnsBadRequest() throws Exception {
        RouteDTO input = validInput();
        input.setDistanceMeters(0);

        mockMvc.perform(post("/api/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithNullDuration_ReturnsBadRequest() throws Exception {
        RouteDTO input = validInput();
        input.setDurationSeconds(null);

        mockMvc.perform(post("/api/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithNegativeDuration_ReturnsBadRequest() throws Exception {
        RouteDTO input = validInput();
        input.setDurationSeconds(-60);

        mockMvc.perform(post("/api/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithZeroDuration_ReturnsBadRequest() throws Exception {
        RouteDTO input = validInput();
        input.setDurationSeconds(0);

        mockMvc.perform(post("/api/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithNullSafetyScore_ReturnsBadRequest() throws Exception {
        RouteDTO input = validInput();
        input.setSafetyScore(null);

        mockMvc.perform(post("/api/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithSafetyScoreAboveMax_ReturnsBadRequest() throws Exception {
        RouteDTO input = validInput();
        input.setSafetyScore(1.5);

        mockMvc.perform(post("/api/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithNegativeSafetyScore_ReturnsBadRequest() throws Exception {
        RouteDTO input = validInput();
        input.setSafetyScore(-0.1);

        mockMvc.perform(post("/api/v1/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/v1/routes/{id} ───────────────────────────────────────

    @Test
    void findById_WithExistingId_ReturnsRoute() throws Exception {
        RouteDTO route = savedRoute("route-001", false);
        when(routeService.findById("route-001")).thenReturn(Optional.of(route));

        mockMvc.perform(get("/api/v1/routes/route-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("route-001"))
                .andExpect(jsonPath("$.tripId").value("trip-001"))
                .andExpect(jsonPath("$.safetyScore").value(0.85))
                .andExpect(jsonPath("$.isSelected").value(false));
    }

    @Test
    void findById_WithNonExistingId_ReturnsNotFound() throws Exception {
        when(routeService.findById("non-existing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/routes/non-existing"))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/v1/routes/trip/{tripId} ──────────────────────────────

    @Test
    void findByTripId_ReturnsListOfRoutes() throws Exception {
        RouteDTO route1 = savedRoute("route-001", true);
        RouteDTO route2 = savedRoute("route-002", false);
        route2.setSafetyScore(0.70);

        when(routeService.findByTripId("trip-001")).thenReturn(List.of(route1, route2));

        mockMvc.perform(get("/api/v1/routes/trip/trip-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("route-001"))
                .andExpect(jsonPath("$[0].isSelected").value(true))
                .andExpect(jsonPath("$[1].id").value("route-002"))
                .andExpect(jsonPath("$[1].isSelected").value(false));
    }

    @Test
    void findByTripId_WhenNoRoutes_ReturnsEmptyList() throws Exception {
        when(routeService.findByTripId("trip-empty")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/routes/trip/trip-empty"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ── GET /api/v1/routes/trip/{tripId}/selected ─────────────────────

    @Test
    void findSelectedByTripId_WhenSelected_ReturnsRoute() throws Exception {
        RouteDTO selected = savedRoute("route-001", true);
        when(routeService.findSelectedByTripId("trip-001")).thenReturn(Optional.of(selected));

        mockMvc.perform(get("/api/v1/routes/trip/trip-001/selected"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("route-001"))
                .andExpect(jsonPath("$.isSelected").value(true));
    }

    @Test
    void findSelectedByTripId_WhenNoneSelected_ReturnsNotFound() throws Exception {
        when(routeService.findSelectedByTripId("trip-001")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/routes/trip/trip-001/selected"))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/v1/routes/{id} ───────────────────────────────────────

    @Test
    void update_WithValidData_ReturnsUpdatedRoute() throws Exception {
        RouteDTO input = validInput();
        input.setSafetyScore(0.92);
        input.setDistanceMeters(4500);

        RouteDTO updated = savedRoute("route-001", false);
        updated.setSafetyScore(0.92);
        updated.setDistanceMeters(4500);

        when(routeService.update(eq("route-001"), any(RouteDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/routes/route-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("route-001"))
                .andExpect(jsonPath("$.safetyScore").value(0.92))
                .andExpect(jsonPath("$.distanceMeters").value(4500));
    }

    @Test
    void update_WithInvalidData_ReturnsBadRequest() throws Exception {
        RouteDTO input = RouteDTO.builder()
                .tripId(null)
                .polyline(null)
                .build();

        mockMvc.perform(put("/api/v1/routes/route-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_WithNonExistingId_ThrowsFromService() throws Exception {
        RouteDTO input = validInput();

        when(routeService.update(eq("non-existing"), any(RouteDTO.class)))
                .thenThrow(new ResourceNotFoundException("Route not found with id: non-existing"));

        mockMvc.perform(put("/api/v1/routes/non-existing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/v1/routes/{id} ────────────────────────────────────

    @Test
    void delete_WithExistingId_ReturnsNoContent() throws Exception {
        doNothing().when(routeService).delete("route-001");

        mockMvc.perform(delete("/api/v1/routes/route-001"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_WithNonExistingId_ThrowsFromService() throws Exception {
        doThrow(new ResourceNotFoundException("Route not found with id: non-existing"))
                .when(routeService).delete("non-existing");

        mockMvc.perform(delete("/api/v1/routes/non-existing"))
                .andExpect(status().isNotFound());
    }

    // ── POST /api/v1/routes/{id}/select ───────────────────────────────

    @Test
    void select_WithExistingId_ReturnsSelectedRoute() throws Exception {
        RouteDTO selected = savedRoute("route-001", true);
        when(routeService.selectRoute("route-001")).thenReturn(selected);

        mockMvc.perform(post("/api/v1/routes/route-001/select"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("route-001"))
                .andExpect(jsonPath("$.isSelected").value(true));
    }

    @Test
    void select_WithNonExistingId_ThrowsFromService() throws Exception {
        when(routeService.selectRoute("non-existing"))
                .thenThrow(new ResourceNotFoundException("Route not found with id: non-existing"));

        mockMvc.perform(post("/api/v1/routes/non-existing/select"))
                .andExpect(status().isNotFound());
    }

    @Test
    void select_WhenAnotherRouteAlreadySelected_ReturnsNewlySelectedRoute() throws Exception {
        RouteDTO newlySelected = savedRoute("route-002", true);
        when(routeService.selectRoute("route-002")).thenReturn(newlySelected);

        mockMvc.perform(post("/api/v1/routes/route-002/select"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("route-002"))
                .andExpect(jsonPath("$.isSelected").value(true));
    }

    @Test
    void select_SameRouteTwice_IsIdempotent() throws Exception {
        RouteDTO selected = savedRoute("route-001", true);
        when(routeService.selectRoute("route-001")).thenReturn(selected);

        // First selection
        mockMvc.perform(post("/api/v1/routes/route-001/select"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSelected").value(true));

        // Second selection — same result
        mockMvc.perform(post("/api/v1/routes/route-001/select"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("route-001"))
                .andExpect(jsonPath("$.isSelected").value(true));

        verify(routeService, times(2)).selectRoute("route-001");
    }

    @Test
    void select_VerifiesOnlyOneRouteSelectedPerTrip() throws Exception {
        // Select route-002 — service returns it as selected
        RouteDTO selectedRoute2 = savedRoute("route-002", true);
        when(routeService.selectRoute("route-002")).thenReturn(selectedRoute2);

        mockMvc.perform(post("/api/v1/routes/route-002/select"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSelected").value(true));

        // Verify findByTripId returns only one selected route
        RouteDTO route1 = savedRoute("route-001", false);
        RouteDTO route2 = savedRoute("route-002", true);
        when(routeService.findByTripId("trip-001")).thenReturn(List.of(route1, route2));

        mockMvc.perform(get("/api/v1/routes/trip/trip-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("route-001"))
                .andExpect(jsonPath("$[0].isSelected").value(false))
                .andExpect(jsonPath("$[1].id").value("route-002"))
                .andExpect(jsonPath("$[1].isSelected").value(true));
    }

    // ── Mapper: tripId immutability ───────────────────────────────────

    @Test
    void updateEntityFromDTO_DoesNotChangeTripId() {
        Route entity = Route.builder()
                .id("route-001")
                .tripId("trip-original")
                .polyline("oldPolyline")
                .distanceMeters(1000)
                .durationSeconds(120)
                .safetyScore(0.5)
                .build();

        RouteDTO dto = RouteDTO.builder()
                .tripId("trip-different")
                .polyline("newPolyline")
                .build();

        RouteMapper.updateEntityFromDTO(dto, entity);

        assertEquals("trip-original", entity.getTripId(), "tripId must be immutable after creation");
        assertEquals("newPolyline", entity.getPolyline(), "other fields should still update");
    }
}
