package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.TripDTO;
import hazardhub.com.hub.model.enums.TripStatus;
import hazardhub.com.hub.service.TripService;
import hazardhub.com.hub.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @MockitoBean
    private TripService tripService;

    @MockitoBean
    private UserService userService;

    // ==================== CREATE TESTS ====================

    @Test
    void create_WithValidTripDTO_ReturnsCreatedTrip() throws Exception {
        // Arrange
        TripDTO inputDTO = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .originAddress("123 Main St, San Francisco")
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .destinationAddress("456 Market St, San Francisco")
                .build();

        TripDTO savedDTO = TripDTO.builder()
                .id("trip-001")
                .userId("user-123")
                .createdAt(Instant.now())
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .originAddress("123 Main St, San Francisco")
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .destinationAddress("456 Market St, San Francisco")
                .status(TripStatus.PLANNING)
                .build();

        when(tripService.create(any(TripDTO.class))).thenReturn(savedDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("trip-001"))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.originLongitude").value(-122.4194))
                .andExpect(jsonPath("$.originLatitude").value(37.7749))
                .andExpect(jsonPath("$.destinationLongitude").value(-122.4094))
                .andExpect(jsonPath("$.destinationLatitude").value(37.7849))
                .andExpect(jsonPath("$.status").value("PLANNING"));
    }

    @Test
    void create_WithNullUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        TripDTO inputDTO = TripDTO.builder()
                .userId(null)
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithNullOriginCoordinates_ReturnsBadRequest() throws Exception {
        // Arrange - null origin longitude
        TripDTO inputWithNullOriginLongitude = TripDTO.builder()
                .userId("user-123")
                .originLongitude(null)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .build();

        // Act & Assert - null origin longitude
        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWithNullOriginLongitude)))
                .andExpect(status().isBadRequest());

        // Arrange - null origin latitude
        TripDTO inputWithNullOriginLatitude = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(null)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .build();

        // Act & Assert - null origin latitude
        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWithNullOriginLatitude)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithNullDestinationCoordinates_ReturnsBadRequest() throws Exception {
        // Arrange - null destination longitude
        TripDTO inputWithNullDestLongitude = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(null)
                .destinationLatitude(37.7849)
                .build();

        // Act & Assert - null destination longitude
        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWithNullDestLongitude)))
                .andExpect(status().isBadRequest());

        // Arrange - null destination latitude
        TripDTO inputWithNullDestLatitude = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(null)
                .build();

        // Act & Assert - null destination latitude
        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWithNullDestLatitude)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithInvalidCoordinateRange_ReturnsBadRequest() throws Exception {
        // Arrange - longitude out of range
        TripDTO inputWithInvalidLongitude = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-200.0) // Invalid: must be >= -180
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWithInvalidLongitude)))
                .andExpect(status().isBadRequest());

        // Arrange - latitude out of range
        TripDTO inputWithInvalidLatitude = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(100.0) // Invalid: must be <= 90
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWithInvalidLatitude)))
                .andExpect(status().isBadRequest());
    }

    // ==================== GET BY ID TESTS ====================

    @Test
    void findById_WithExistingId_ReturnsTrip() throws Exception {
        // Arrange
        TripDTO tripDTO = TripDTO.builder()
                .id("trip-001")
                .userId("user-123")
                .createdAt(Instant.now())
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .originAddress("123 Main St, San Francisco")
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .destinationAddress("456 Market St, San Francisco")
                .status(TripStatus.PLANNING)
                .build();

        when(tripService.findById("trip-001")).thenReturn(Optional.of(tripDTO));

        // Act & Assert
        mockMvc.perform(get("/api/v1/trips/trip-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("trip-001"))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.status").value("PLANNING"));
    }

    @Test
    void findById_WithNonExistingId_ReturnsNotFound() throws Exception {
        // Arrange
        when(tripService.findById("non-existing-id")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/trips/non-existing-id"))
                .andExpect(status().isNotFound());
    }

    // ==================== GET ALL TESTS ====================

    @Test
    void findAll_ReturnsListOfTrips() throws Exception {
        // Arrange
        TripDTO trip1 = TripDTO.builder()
                .id("trip-001")
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .status(TripStatus.PLANNING)
                .build();

        TripDTO trip2 = TripDTO.builder()
                .id("trip-002")
                .userId("user-456")
                .originLongitude(-122.5000)
                .originLatitude(37.8000)
                .destinationLongitude(-122.4500)
                .destinationLatitude(37.8500)
                .status(TripStatus.ACTIVE)
                .build();

        when(tripService.findAll()).thenReturn(List.of(trip1, trip2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/trips"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("trip-001"))
                .andExpect(jsonPath("$[1].id").value("trip-002"));
    }

    @Test
    void findAll_WhenEmpty_ReturnsEmptyList() throws Exception {
        // Arrange
        when(tripService.findAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/trips"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ==================== UPDATE TESTS ====================

    @Test
    void update_WithValidData_ReturnsUpdatedTrip() throws Exception {
        // Arrange
        TripDTO inputDTO = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .status(TripStatus.ACTIVE)
                .build();

        TripDTO updatedDTO = TripDTO.builder()
                .id("trip-001")
                .userId("user-123")
                .createdAt(Instant.now())
                .startedAt(Instant.now())
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .status(TripStatus.ACTIVE)
                .build();

        when(tripService.update(eq("trip-001"), any(TripDTO.class))).thenReturn(updatedDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/trips/trip-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("trip-001"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void update_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Arrange - missing required fields
        TripDTO inputDTO = TripDTO.builder()
                .userId(null)
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .build();

        // Act & Assert
        mockMvc.perform(put("/api/v1/trips/trip-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    // ==================== DELETE TESTS ====================

    @Test
    void delete_WithExistingId_ReturnsNoContent() throws Exception {
        // Arrange
        doNothing().when(tripService).delete("trip-001");

        // Act & Assert
        mockMvc.perform(delete("/api/v1/trips/trip-001"))
                .andExpect(status().isNoContent());
    }

    // ==================== FIND BY USER ID TESTS ====================

    @Test
    void findByUserId_ReturnsUserTrips() throws Exception {
        // Arrange
        TripDTO trip1 = TripDTO.builder()
                .id("trip-001")
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .status(TripStatus.COMPLETED)
                .build();

        TripDTO trip2 = TripDTO.builder()
                .id("trip-002")
                .userId("user-123")
                .originLongitude(-122.5000)
                .originLatitude(37.8000)
                .destinationLongitude(-122.4500)
                .destinationLatitude(37.8500)
                .status(TripStatus.PLANNING)
                .build();

        when(tripService.findByUserId("user-123")).thenReturn(List.of(trip1, trip2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/trips/user/user-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("user-123"))
                .andExpect(jsonPath("$[1].userId").value("user-123"));
    }

    // ==================== FIND BY STATUS TESTS ====================

    @Test
    void findByStatus_ReturnsTripsByStatus() throws Exception {
        // Arrange
        TripDTO trip1 = TripDTO.builder()
                .id("trip-001")
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .status(TripStatus.ACTIVE)
                .build();

        when(tripService.findByStatus(TripStatus.ACTIVE)).thenReturn(List.of(trip1));

        // Act & Assert
        mockMvc.perform(get("/api/v1/trips/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    // ==================== FIND BY USER ID AND STATUS TESTS ====================

    @Test
    void findByUserIdAndStatus_ReturnsFilteredTrips() throws Exception {
        // Arrange
        TripDTO trip = TripDTO.builder()
                .id("trip-001")
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .status(TripStatus.ACTIVE)
                .build();

        when(tripService.findByUserIdAndStatus("user-123", TripStatus.ACTIVE)).thenReturn(List.of(trip));

        // Act & Assert
        mockMvc.perform(get("/api/v1/trips/user/user-123/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value("user-123"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    // ==================== FIND BY SELECTED ROUTE ID TESTS ====================

    @Test
    void findBySelectedRouteId_WithExistingRoute_ReturnsTrip() throws Exception {
        // Arrange
        TripDTO tripDTO = TripDTO.builder()
                .id("trip-001")
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .selectedRouteId("route-001")
                .status(TripStatus.ACTIVE)
                .build();

        when(tripService.findBySelectedRouteId("route-001")).thenReturn(Optional.of(tripDTO));

        // Act & Assert
        mockMvc.perform(get("/api/v1/trips/route/route-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("trip-001"))
                .andExpect(jsonPath("$.selectedRouteId").value("route-001"));
    }

    @Test
    void findBySelectedRouteId_WithNonExistingRoute_ReturnsNotFound() throws Exception {
        // Arrange
        when(tripService.findBySelectedRouteId("non-existing-route")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/trips/route/non-existing-route"))
                .andExpect(status().isNotFound());
    }

    // ==================== COUNT TESTS ====================

    @Test
    void countByUserId_ReturnsCount() throws Exception {
        // Arrange
        when(tripService.countByUserId("user-123")).thenReturn(5L);

        // Act & Assert
        mockMvc.perform(get("/api/v1/trips/user/user-123/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void countByUserIdAndStatus_ReturnsCount() throws Exception {
        // Arrange
        when(tripService.countByUserIdAndStatus("user-123", TripStatus.COMPLETED)).thenReturn(3L);

        // Act & Assert
        mockMvc.perform(get("/api/v1/trips/user/user-123/status/COMPLETED/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    // ==================== EXISTS TESTS ====================

    @Test
    void existsByUserIdAndStatus_WhenExists_ReturnsTrue() throws Exception {
        // Arrange
        when(tripService.existsByUserIdAndStatus("user-123", TripStatus.ACTIVE)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/v1/trips/user/user-123/status/ACTIVE/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void existsByUserIdAndStatus_WhenNotExists_ReturnsFalse() throws Exception {
        // Arrange
        when(tripService.existsByUserIdAndStatus("user-123", TripStatus.SOS_TRIGGERED)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/v1/trips/user/user-123/status/SOS_TRIGGERED/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}