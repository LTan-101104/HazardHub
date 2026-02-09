package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.SOSEventDTO;
import hazardhub.com.hub.model.enums.SOSEventStatus;
import hazardhub.com.hub.repository.SOSEventRepository;
import hazardhub.com.hub.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@ActiveProfiles("integration")
class SOSEventControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SOSEventRepository sosEventRepository;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @BeforeEach
    void setUp() {
        sosEventRepository.deleteAll();
    }

    @Test
    void fullCrudFlow_Integration() throws Exception {
        // ==================== CREATE ====================
        SOSEventDTO createDTO = SOSEventDTO.builder()
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/sos-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.tripId").value("trip-456"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.triggeredAt").exists())
                .andExpect(jsonPath("$.dispatchNotified").value(false))
                .andReturn();

        // Extract the created ID for subsequent operations
        String responseJson = createResult.getResponse().getContentAsString();
        SOSEventDTO createdEvent = objectMapper.readValue(responseJson, SOSEventDTO.class);
        String eventId = createdEvent.getId();

        // ==================== READ BY ID ====================
        mockMvc.perform(get("/api/v1/sos-events/" + eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.tripId").value("trip-456"));

        // ==================== READ ALL ====================
        mockMvc.perform(get("/api/v1/sos-events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(eventId));

        // ==================== UPDATE ====================
        SOSEventDTO updateDTO = SOSEventDTO.builder()
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .status(SOSEventStatus.HELP_ARRIVING)
                .dispatchNotified(true)
                .dispatchReference("DISPATCH-001")
                .build();

        mockMvc.perform(put("/api/v1/sos-events/" + eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.status").value("HELP_ARRIVING"))
                .andExpect(jsonPath("$.dispatchNotified").value(true))
                .andExpect(jsonPath("$.dispatchReference").value("DISPATCH-001"));

        // Verify the update persisted
        mockMvc.perform(get("/api/v1/sos-events/" + eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("HELP_ARRIVING"));

        // ==================== DELETE ====================
        mockMvc.perform(delete("/api/v1/sos-events/" + eventId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/v1/sos-events/" + eventId))
                .andExpect(status().isNotFound());

        // Verify list is empty
        mockMvc.perform(get("/api/v1/sos-events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void create_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Missing required fields
        SOSEventDTO invalidDTO = SOSEventDTO.builder()
                .userId(null)
                .longitude(-122.4194)
                .latitude(37.7749)
                .build();

        mockMvc.perform(post("/api/v1/sos-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_WithNonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/sos-events/non-existing-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByStatus_Integration() throws Exception {
        // Create an active SOS event
        SOSEventDTO createDTO = SOSEventDTO.builder()
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .build();

        mockMvc.perform(post("/api/v1/sos-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated());

        // Find by ACTIVE status
        mockMvc.perform(get("/api/v1/sos-events/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        // Find by RESOLVED status (should be empty)
        mockMvc.perform(get("/api/v1/sos-events/status/RESOLVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void findByTripId_Integration() throws Exception {
        // Create SOS events for different trips
        SOSEventDTO event1 = SOSEventDTO.builder()
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .build();

        SOSEventDTO event2 = SOSEventDTO.builder()
                .userId("user-123")
                .tripId("trip-789")
                .longitude(-122.4000)
                .latitude(37.7800)
                .locationAccuracyMeters(10.0)
                .build();

        mockMvc.perform(post("/api/v1/sos-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/sos-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event2)))
                .andExpect(status().isCreated());

        // Find by trip-456
        mockMvc.perform(get("/api/v1/sos-events/trip/trip-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tripId").value("trip-456"));

        // Find by trip-789
        mockMvc.perform(get("/api/v1/sos-events/trip/trip-789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tripId").value("trip-789"));
    }

    @Test
    void resolveSOSEvent_Integration() throws Exception {
        // Create an active SOS event
        SOSEventDTO createDTO = SOSEventDTO.builder()
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/sos-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn();

        String responseJson = createResult.getResponse().getContentAsString();
        SOSEventDTO createdEvent = objectMapper.readValue(responseJson, SOSEventDTO.class);
        String eventId = createdEvent.getId();

        // Update to HELP_ARRIVING
        SOSEventDTO helpArrivingDTO = SOSEventDTO.builder()
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .status(SOSEventStatus.HELP_ARRIVING)
                .dispatchNotified(true)
                .dispatchReference("DISPATCH-001")
                .build();

        mockMvc.perform(put("/api/v1/sos-events/" + eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(helpArrivingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("HELP_ARRIVING"));

        // Update to RESOLVED
        SOSEventDTO resolvedDTO = SOSEventDTO.builder()
                .userId("user-123")
                .tripId("trip-456")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .status(SOSEventStatus.RESOLVED)
                .dispatchNotified(true)
                .dispatchReference("DISPATCH-001")
                .build();

        mockMvc.perform(put("/api/v1/sos-events/" + eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resolvedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));

        // Verify no active events
        mockMvc.perform(get("/api/v1/sos-events/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // Verify resolved event exists
        mockMvc.perform(get("/api/v1/sos-events/status/RESOLVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}