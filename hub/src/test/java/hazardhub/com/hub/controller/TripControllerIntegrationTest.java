package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.TripDTO;
import hazardhub.com.hub.model.enums.TripStatus;
import hazardhub.com.hub.repository.TripRepository;
import hazardhub.com.hub.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@ActiveProfiles("integration")
class TripControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRepository tripRepository;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {
        tripRepository.deleteAll();
        when(userService.existsById(anyString())).thenReturn(true);
    }

    @Test
    void fullCrudFlow_Integration() throws Exception {
        // ==================== CREATE ====================
        TripDTO createDTO = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .originAddress("123 Main St, San Francisco, CA")
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .destinationAddress("456 Market St, San Francisco, CA")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.originLongitude").value(-122.4194))
                .andExpect(jsonPath("$.originLatitude").value(37.7749))
                .andExpect(jsonPath("$.originAddress").value("123 Main St, San Francisco, CA"))
                .andExpect(jsonPath("$.destinationLongitude").value(-122.4094))
                .andExpect(jsonPath("$.destinationLatitude").value(37.7849))
                .andExpect(jsonPath("$.destinationAddress").value("456 Market St, San Francisco, CA"))
                .andReturn();

        String responseJson = createResult.getResponse().getContentAsString();
        TripDTO createdTrip = objectMapper.readValue(responseJson, TripDTO.class);
        String tripId = createdTrip.getId();

        // ==================== READ BY ID ====================
        mockMvc.perform(get("/api/v1/trips/" + tripId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tripId))
                .andExpect(jsonPath("$.userId").value("user-123"));

        // ==================== READ ALL ====================
        mockMvc.perform(get("/api/v1/trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(tripId));

        // ==================== UPDATE ====================
        TripDTO updateDTO = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-122.4200)
                .originLatitude(37.7750)
                .originAddress("Updated Origin Address")
                .destinationLongitude(-122.4100)
                .destinationLatitude(37.7850)
                .destinationAddress("Updated Destination Address")
                .status(TripStatus.ACTIVE)
                .build();

        mockMvc.perform(put("/api/v1/trips/" + tripId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tripId))
                .andExpect(jsonPath("$.originAddress").value("Updated Origin Address"))
                .andExpect(jsonPath("$.destinationAddress").value("Updated Destination Address"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Verify the update persisted
        mockMvc.perform(get("/api/v1/trips/" + tripId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originAddress").value("Updated Origin Address"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // ==================== DELETE ====================
        mockMvc.perform(delete("/api/v1/trips/" + tripId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/v1/trips/" + tripId))
                .andExpect(status().isNotFound());

        // Verify list is empty
        mockMvc.perform(get("/api/v1/trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void create_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Missing required fields
        TripDTO invalidDTO = TripDTO.builder()
                .userId(null)
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .build();

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void create_WithInvalidCoordinates_ReturnsBadRequest() throws Exception {
        // Origin longitude out of range
        TripDTO invalidOriginLongitude = TripDTO.builder()
                .userId("user-123")
                .originLongitude(181.0)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .build();

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOriginLongitude)))
                .andExpect(status().isBadRequest());

        // Origin latitude out of range
        TripDTO invalidOriginLatitude = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(91.0)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .build();

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOriginLatitude)))
                .andExpect(status().isBadRequest());

        // Destination longitude out of range
        TripDTO invalidDestLongitude = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-181.0)
                .destinationLatitude(37.7849)
                .build();

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDestLongitude)))
                .andExpect(status().isBadRequest());

        // Destination latitude out of range
        TripDTO invalidDestLatitude = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(-91.0)
                .build();

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDestLatitude)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_WithNonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/trips/non-existing-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByUserId_ReturnsTripsForUser() throws Exception {
        // Create trips for user-123
        TripDTO trip1 = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .build();

        TripDTO trip2 = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-122.5000)
                .originLatitude(37.8000)
                .destinationLongitude(-122.4500)
                .destinationLatitude(37.8500)
                .build();

        // Create trip for different user
        TripDTO trip3 = TripDTO.builder()
                .userId("user-456")
                .originLongitude(-122.3900)
                .originLatitude(37.7850)
                .destinationLongitude(-122.3800)
                .destinationLatitude(37.7950)
                .build();

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip3)))
                .andExpect(status().isCreated());

        // Find by user ID should return only 2 for user-123
        mockMvc.perform(get("/api/v1/trips/user/user-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("user-123"))
                .andExpect(jsonPath("$[1].userId").value("user-123"));

        // Find by user ID for user-456 should return 1
        mockMvc.perform(get("/api/v1/trips/user/user-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value("user-456"));
    }

    @Test
    void findByUserId_WithNoTrips_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/trips/user/user-no-trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void findByStatus_ReturnsTripsWithStatus() throws Exception {
        // Create trip with PLANNING status (default)
        TripDTO planningTrip = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planningTrip)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        TripDTO createdTrip = objectMapper.readValue(responseJson, TripDTO.class);
        String tripId = createdTrip.getId();

        // Update to ACTIVE status
        TripDTO updateDTO = TripDTO.builder()
                .userId("user-123")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .status(TripStatus.ACTIVE)
                .build();

        mockMvc.perform(put("/api/v1/trips/" + tripId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());

        // Find by ACTIVE status
        mockMvc.perform(get("/api/v1/trips/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        // Find by PLANNING status should return empty
        mockMvc.perform(get("/api/v1/trips/status/PLANNING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void findByUserIdAndStatus_ReturnsFilteredTrips() throws Exception {
        // Create trips with different statuses
        TripDTO trip1 = TripDTO.builder()
                .userId("user-filter")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .build();

        TripDTO trip2 = TripDTO.builder()
                .userId("user-filter")
                .originLongitude(-122.5000)
                .originLatitude(37.8000)
                .destinationLongitude(-122.4500)
                .destinationLatitude(37.8500)
                .build();

        // Create both trips
        MvcResult result1 = mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip1)))
                .andExpect(status().isCreated())
                .andReturn();

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip2)))
                .andExpect(status().isCreated());

        // Update first trip to ACTIVE
        String trip1Id = objectMapper.readValue(result1.getResponse().getContentAsString(), TripDTO.class).getId();
        TripDTO updateDTO = TripDTO.builder()
                .userId("user-filter")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .status(TripStatus.ACTIVE)
                .build();

        mockMvc.perform(put("/api/v1/trips/" + trip1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());

        // Find user's ACTIVE trips - should be 1
        mockMvc.perform(get("/api/v1/trips/user/user-filter/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        // Find user's PLANNING trips - should be 1
        mockMvc.perform(get("/api/v1/trips/user/user-filter/status/PLANNING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("PLANNING"));
    }

    @Test
    void countByUserId_ReturnsCorrectCount() throws Exception {
        // Create multiple trips for same user
        for (int i = 0; i < 3; i++) {
            TripDTO trip = TripDTO.builder()
                    .userId("user-count")
                    .originLongitude(-122.4194 + (i * 0.01))
                    .originLatitude(37.7749 + (i * 0.01))
                    .destinationLongitude(-122.4094 + (i * 0.01))
                    .destinationLatitude(37.7849 + (i * 0.01))
                    .build();

            mockMvc.perform(post("/api/v1/trips")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(trip)))
                    .andExpect(status().isCreated());
        }

        // Count should be 3
        mockMvc.perform(get("/api/v1/trips/user/user-count/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void countByUserIdAndStatus_ReturnsCorrectCount() throws Exception {
        // Create trips
        TripDTO trip1 = TripDTO.builder()
                .userId("user-count-status")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .build();

        TripDTO trip2 = TripDTO.builder()
                .userId("user-count-status")
                .originLongitude(-122.5000)
                .originLatitude(37.8000)
                .destinationLongitude(-122.4500)
                .destinationLatitude(37.8500)
                .build();

        MvcResult result1 = mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip1)))
                .andExpect(status().isCreated())
                .andReturn();

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip2)))
                .andExpect(status().isCreated());

        // Update first trip to COMPLETED
        String trip1Id = objectMapper.readValue(result1.getResponse().getContentAsString(), TripDTO.class).getId();
        TripDTO updateDTO = TripDTO.builder()
                .userId("user-count-status")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .status(TripStatus.COMPLETED)
                .build();

        mockMvc.perform(put("/api/v1/trips/" + trip1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());

        // Count COMPLETED trips should be 1
        mockMvc.perform(get("/api/v1/trips/user/user-count-status/status/COMPLETED/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        // Count PLANNING trips should be 1
        mockMvc.perform(get("/api/v1/trips/user/user-count-status/status/PLANNING/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void existsByUserIdAndStatus_ReturnsCorrectResult() throws Exception {
        // Create a trip
        TripDTO trip = TripDTO.builder()
                .userId("user-exists")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .build();

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip)))
                .andExpect(status().isCreated());

        // User has PLANNING trip - should return true
        mockMvc.perform(get("/api/v1/trips/user/user-exists/status/PLANNING/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // User has no ACTIVE trip - should return false
        mockMvc.perform(get("/api/v1/trips/user/user-exists/status/ACTIVE/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        // Non-existing user - should return false
        mockMvc.perform(get("/api/v1/trips/user/non-existing-user/status/PLANNING/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void tripStatusTransitions_Integration() throws Exception {
        // Create a trip (starts in PLANNING)
        TripDTO createDTO = TripDTO.builder()
                .userId("user-status")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .status(TripStatus.PLANNING)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PLANNING"))
                .andReturn();

        String tripId = objectMapper.readValue(createResult.getResponse().getContentAsString(), TripDTO.class).getId();

        // Transition to ACTIVE
        TripDTO activeDTO = TripDTO.builder()
                .userId("user-status")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .status(TripStatus.ACTIVE)
                .build();

        mockMvc.perform(put("/api/v1/trips/" + tripId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Transition to COMPLETED
        TripDTO completedDTO = TripDTO.builder()
                .userId("user-status")
                .originLongitude(-122.4194)
                .originLatitude(37.7749)
                .destinationLongitude(-122.4094)
                .destinationLatitude(37.7849)
                .status(TripStatus.COMPLETED)
                .build();

        mockMvc.perform(put("/api/v1/trips/" + tripId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void delete_WithNonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/trips/non-existing-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createMultipleTripsForSameUser_Success() throws Exception {
        String[] addresses = {"Home to Work", "Work to Gym", "Gym to Home", "Home to School"};
        double baseLongitude = -122.4194;
        double baseLatitude = 37.7749;

        for (int i = 0; i < addresses.length; i++) {
            TripDTO trip = TripDTO.builder()
                    .userId("user-multi-trips")
                    .originLongitude(baseLongitude + (i * 0.01))
                    .originLatitude(baseLatitude + (i * 0.01))
                    .originAddress(addresses[i] + " - Origin")
                    .destinationLongitude(baseLongitude + (i * 0.02))
                    .destinationLatitude(baseLatitude + (i * 0.02))
                    .destinationAddress(addresses[i] + " - Destination")
                    .build();

            mockMvc.perform(post("/api/v1/trips")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(trip)))
                    .andExpect(status().isCreated());
        }

        // Verify all 4 trips were created
        mockMvc.perform(get("/api/v1/trips/user/user-multi-trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }
}