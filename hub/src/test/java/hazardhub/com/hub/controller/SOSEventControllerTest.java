package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.SOSEventDTO;
import hazardhub.com.hub.model.enums.SOSEventStatus;
import hazardhub.com.hub.service.SOSEventService;
import hazardhub.com.hub.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
class SOSEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @MockitoBean
    private SOSEventService sosEventService;


    @MockitoBean
    private UserService userService;

    // ==================== CREATE TESTS ====================

    @Test
    void create_WithValidSOSEventDTO_ReturnsCreatedSOSEvent() throws Exception {
        // Arrange
        SOSEventDTO inputDTO = SOSEventDTO.builder()
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .build();

        SOSEventDTO savedDTO = SOSEventDTO.builder()
                .id("sos-001")
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .triggeredAt(Instant.now())
                .status(SOSEventStatus.ACTIVE)
                .dispatchNotified(false)
                .build();

        when(sosEventService.create(any(SOSEventDTO.class))).thenReturn(savedDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/sos-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("sos-001"))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.tripId").value("trip-456"))
                .andExpect(jsonPath("$.longitude").value(-122.4194))
                .andExpect(jsonPath("$.latitude").value(37.7749))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.dispatchNotified").value(false));
    }

    @Test
    void create_WithNullUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        SOSEventDTO inputDTO = SOSEventDTO.builder()
                .userId(null)
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/sos-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithNullCoordinates_ReturnsBadRequest() throws Exception {
        // Arrange - null longitude
        SOSEventDTO inputWithNullLongitude = SOSEventDTO.builder()
                .userId("user-123")
                .longitude(null)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .build();

        // Act & Assert - null longitude
        mockMvc.perform(post("/api/v1/sos-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWithNullLongitude)))
                .andExpect(status().isBadRequest());

        // Arrange - null latitude
        SOSEventDTO inputWithNullLatitude = SOSEventDTO.builder()
                .userId("user-123")
                .longitude(-122.4194)
                .latitude(null)
                .locationAccuracyMeters(10.0)
                .build();

        // Act & Assert - null latitude
        mockMvc.perform(post("/api/v1/sos-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWithNullLatitude)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithInvalidCoordinates_ReturnsBadRequest() throws Exception {
        // Arrange - longitude out of range
        SOSEventDTO inputWithInvalidLongitude = SOSEventDTO.builder()
                .userId("user-123")
                .longitude(200.0)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .build();

        // Act & Assert - invalid longitude
        mockMvc.perform(post("/api/v1/sos-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWithInvalidLongitude)))
                .andExpect(status().isBadRequest());

        // Arrange - latitude out of range
        SOSEventDTO inputWithInvalidLatitude = SOSEventDTO.builder()
                .userId("user-123")
                .longitude(-122.4194)
                .latitude(100.0)
                .locationAccuracyMeters(10.0)
                .build();

        // Act & Assert - invalid latitude
        mockMvc.perform(post("/api/v1/sos-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWithInvalidLatitude)))
                .andExpect(status().isBadRequest());
    }

    // ==================== GET BY ID TESTS ====================

    @Test
    void findById_WithExistingId_ReturnsSOSEvent() throws Exception {
        // Arrange
        SOSEventDTO sosEventDTO = SOSEventDTO.builder()
                .id("sos-001")
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .triggeredAt(Instant.now())
                .status(SOSEventStatus.ACTIVE)
                .dispatchNotified(false)
                .build();

        when(sosEventService.findById("sos-001")).thenReturn(Optional.of(sosEventDTO));

        // Act & Assert
        mockMvc.perform(get("/api/v1/sos-events/sos-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("sos-001"))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void findById_WithNonExistingId_ReturnsNotFound() throws Exception {
        // Arrange
        when(sosEventService.findById("non-existing-id")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/sos-events/non-existing-id"))
                .andExpect(status().isNotFound());
    }

    // ==================== GET ALL TESTS ====================

    @Test
    void findAll_ReturnsListOfSOSEvents() throws Exception {
        // Arrange
        SOSEventDTO event1 = SOSEventDTO.builder()
                .id("sos-001")
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .status(SOSEventStatus.ACTIVE)
                .build();

        SOSEventDTO event2 = SOSEventDTO.builder()
                .id("sos-002")
                .userId("user-456")
                .tripId("trip-789")
                .longitude(-122.4000)
                .latitude(37.7800)
                .locationAccuracyMeters(5.0)
                .status(SOSEventStatus.RESOLVED)
                .build();

        when(sosEventService.findAll()).thenReturn(List.of(event1, event2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/sos-events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("sos-001"))
                .andExpect(jsonPath("$[1].id").value("sos-002"));
    }

    @Test
    void findAll_WhenEmpty_ReturnsEmptyList() throws Exception {
        // Arrange
        when(sosEventService.findAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/sos-events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ==================== GET BY USER ID TESTS ====================

    @Test
    void findByUserId_ReturnsListOfSOSEvents() throws Exception {
        // Arrange
        SOSEventDTO event1 = SOSEventDTO.builder()
                .id("sos-001")
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .status(SOSEventStatus.ACTIVE)
                .build();

        SOSEventDTO event2 = SOSEventDTO.builder()
                .id("sos-002")
                .userId("user-123")
                .tripId("trip-789")
                .longitude(-122.4000)
                .latitude(37.7800)
                .status(SOSEventStatus.RESOLVED)
                .build();

        when(sosEventService.findByUserId("user-123")).thenReturn(List.of(event1, event2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/sos-events/user/user-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("user-123"))
                .andExpect(jsonPath("$[1].userId").value("user-123"));
    }

    // ==================== GET BY TRIP ID TESTS ====================

    @Test
    void findByTripId_ReturnsListOfSOSEvents() throws Exception {
        // Arrange
        SOSEventDTO event = SOSEventDTO.builder()
                .id("sos-001")
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .status(SOSEventStatus.ACTIVE)
                .build();

        when(sosEventService.findByTripId("trip-456")).thenReturn(List.of(event));

        // Act & Assert
        mockMvc.perform(get("/api/v1/sos-events/trip/trip-456"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tripId").value("trip-456"));
    }

    // ==================== GET BY STATUS TESTS ====================

    @Test
    void findByStatus_ReturnsListOfSOSEvents() throws Exception {
        // Arrange
        SOSEventDTO event = SOSEventDTO.builder()
                .id("sos-001")
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .status(SOSEventStatus.ACTIVE)
                .build();

        when(sosEventService.findByStatus(SOSEventStatus.ACTIVE)).thenReturn(List.of(event));

        // Act & Assert
        mockMvc.perform(get("/api/v1/sos-events/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    // ==================== UPDATE TESTS ====================

    @Test
    void update_WithValidData_ReturnsUpdatedSOSEvent() throws Exception {
        // Arrange
        SOSEventDTO inputDTO = SOSEventDTO.builder()
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .status(SOSEventStatus.HELP_ARRIVING)
                .dispatchNotified(true)
                .dispatchReference("DISPATCH-001")
                .build();

        SOSEventDTO updatedDTO = SOSEventDTO.builder()
                .id("sos-001")
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .triggeredAt(Instant.now())
                .status(SOSEventStatus.HELP_ARRIVING)
                .dispatchNotified(true)
                .dispatchReference("DISPATCH-001")
                .build();

        when(sosEventService.update(eq("sos-001"), any(SOSEventDTO.class))).thenReturn(updatedDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/sos-events/sos-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("sos-001"))
                .andExpect(jsonPath("$.status").value("HELP_ARRIVING"))
                .andExpect(jsonPath("$.dispatchNotified").value(true))
                .andExpect(jsonPath("$.dispatchReference").value("DISPATCH-001"));
    }

    @Test
    void update_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Arrange - missing required fields
        SOSEventDTO inputDTO = SOSEventDTO.builder()
                .userId(null)
                .longitude(-122.4194)
                .latitude(37.7749)
                .build();

        // Act & Assert
        mockMvc.perform(put("/api/v1/sos-events/sos-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    // ==================== DELETE TESTS ====================

    @Test
    void delete_WithExistingId_ReturnsNoContent() throws Exception {
        // Arrange
        doNothing().when(sosEventService).delete("sos-001");

        // Act & Assert
        mockMvc.perform(delete("/api/v1/sos-events/sos-001"))
                .andExpect(status().isNoContent());
    }

    // ==================== NEARBY TESTS ====================

    @Test
    void findNearby_WithValidParams_ReturnsListOfSOSEvents() throws Exception {
        // Arrange
        SOSEventDTO event = SOSEventDTO.builder()
                .id("sos-001")
                .userId("user-123")
                .longitude(-122.4194)
                .latitude(37.7749)
                .status(SOSEventStatus.ACTIVE)
                .build();

        when(sosEventService.findNearby(-122.4194, 37.7749, 1000.0)).thenReturn(List.of(event));

        // Act & Assert
        mockMvc.perform(get("/api/v1/sos-events/nearby")
                        .param("longitude", "-122.4194")
                        .param("latitude", "37.7749")
                        .param("maxDistanceMeters", "1000.0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("sos-001"));
    }

    @Test
    void findNearbyActive_WithValidParams_ReturnsListOfActiveSOSEvents() throws Exception {
        // Arrange
        SOSEventDTO event = SOSEventDTO.builder()
                .id("sos-001")
                .userId("user-123")
                .longitude(-122.4194)
                .latitude(37.7749)
                .status(SOSEventStatus.ACTIVE)
                .build();

        when(sosEventService.findNearbyActive(-122.4194, 37.7749, 1000.0)).thenReturn(List.of(event));

        // Act & Assert
        mockMvc.perform(get("/api/v1/sos-events/nearby/active")
                        .param("longitude", "-122.4194")
                        .param("latitude", "37.7749")
                        .param("maxDistanceMeters", "1000.0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }
}