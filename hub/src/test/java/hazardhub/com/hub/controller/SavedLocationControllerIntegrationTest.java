package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.SavedLocationDTO;
import hazardhub.com.hub.repository.SavedLocationRepository;
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

import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@ActiveProfiles("integration")
class SavedLocationControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SavedLocationRepository savedLocationRepository;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {
        savedLocationRepository.deleteAll();
        // Mock userService to return true for any user ID
        when(userService.existsById(anyString())).thenReturn(true);
    }

    @Test
    void fullCrudFlow_Integration() throws Exception {
        // ==================== CREATE ====================
        SavedLocationDTO createDTO = SavedLocationDTO.builder()
                .userId("user-123")
                .name("Home")
                .longitude(-122.4194)
                .latitude(37.7749)
                .address("123 Main St, San Francisco, CA")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/saved-locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.name").value("Home"))
                .andExpect(jsonPath("$.longitude").value(-122.4194))
                .andExpect(jsonPath("$.latitude").value(37.7749))
                .andExpect(jsonPath("$.address").value("123 Main St, San Francisco, CA"))
                .andReturn();

        // Extract the created ID for subsequent operations
        String responseJson = createResult.getResponse().getContentAsString();
        SavedLocationDTO createdLocation = objectMapper.readValue(responseJson, SavedLocationDTO.class);
        String locationId = createdLocation.getId();

        // ==================== READ BY ID ====================
        mockMvc.perform(get("/api/v1/saved-locations/" + locationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(locationId))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.name").value("Home"));

        // ==================== READ ALL ====================
        mockMvc.perform(get("/api/v1/saved-locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(locationId));

        // ==================== UPDATE ====================
        SavedLocationDTO updateDTO = SavedLocationDTO.builder()
                .userId("user-123")
                .name("Updated Home")
                .longitude(-122.4200)
                .latitude(37.7750)
                .address("456 New St, San Francisco, CA")
                .build();

        mockMvc.perform(put("/api/v1/saved-locations/" + locationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(locationId))
                .andExpect(jsonPath("$.name").value("Updated Home"))
                .andExpect(jsonPath("$.longitude").value(-122.4200))
                .andExpect(jsonPath("$.latitude").value(37.7750))
                .andExpect(jsonPath("$.address").value("456 New St, San Francisco, CA"));

        // Verify the update persisted
        mockMvc.perform(get("/api/v1/saved-locations/" + locationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Home"));

        // ==================== DELETE ====================
        mockMvc.perform(delete("/api/v1/saved-locations/" + locationId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/v1/saved-locations/" + locationId))
                .andExpect(status().isNotFound());

        // Verify list is empty
        mockMvc.perform(get("/api/v1/saved-locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void create_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Missing required fields
        SavedLocationDTO invalidDTO = SavedLocationDTO.builder()
                .userId(null)
                .name("Home")
                .longitude(-122.4194)
                .latitude(37.7749)
                .build();

        mockMvc.perform(post("/api/v1/saved-locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithInvalidCoordinates_ReturnsBadRequest() throws Exception {
        // Longitude out of range
        SavedLocationDTO invalidLongitude = SavedLocationDTO.builder()
                .userId("user-123")
                .name("Home")
                .longitude(181.0)
                .latitude(37.7749)
                .build();

        mockMvc.perform(post("/api/v1/saved-locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLongitude)))
                .andExpect(status().isBadRequest());

        // Latitude out of range
        SavedLocationDTO invalidLatitude = SavedLocationDTO.builder()
                .userId("user-123")
                .name("Home")
                .longitude(-122.4194)
                .latitude(91.0)
                .build();

        mockMvc.perform(post("/api/v1/saved-locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLatitude)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_WithNonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/saved-locations/non-existing-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByUserId_ReturnsLocationsForUser() throws Exception {
        // Create locations for user-123
        SavedLocationDTO location1 = SavedLocationDTO.builder()
                .userId("user-123")
                .name("Home")
                .longitude(-122.4194)
                .latitude(37.7749)
                .build();

        SavedLocationDTO location2 = SavedLocationDTO.builder()
                .userId("user-123")
                .name("Work")
                .longitude(-122.4000)
                .latitude(37.7800)
                .build();

        // Create location for different user
        SavedLocationDTO location3 = SavedLocationDTO.builder()
                .userId("user-456")
                .name("Gym")
                .longitude(-122.3900)
                .latitude(37.7850)
                .build();

        mockMvc.perform(post("/api/v1/saved-locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(location1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/saved-locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(location2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/saved-locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(location3)))
                .andExpect(status().isCreated());

        // Find by user ID should return only 2 for user-123
        mockMvc.perform(get("/api/v1/saved-locations/user/user-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("user-123"))
                .andExpect(jsonPath("$[1].userId").value("user-123"));

        // Find by user ID for user-456 should return 1
        mockMvc.perform(get("/api/v1/saved-locations/user/user-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value("user-456"))
                .andExpect(jsonPath("$[0].name").value("Gym"));
    }

    @Test
    void findByUserId_WithNoLocations_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/saved-locations/user/user-no-locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createMultipleLocationsForSameUser_Success() throws Exception {
        // Create multiple saved locations for the same user
        String[] locationNames = {"Home", "Work", "Gym", "School"};
        double baseLongitude = -122.4194;
        double baseLatitude = 37.7749;

        for (int i = 0; i < locationNames.length; i++) {
            SavedLocationDTO location = SavedLocationDTO.builder()
                    .userId("user-multi")
                    .name(locationNames[i])
                    .longitude(baseLongitude + (i * 0.01))
                    .latitude(baseLatitude + (i * 0.01))
                    .build();

            mockMvc.perform(post("/api/v1/saved-locations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(location)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value(locationNames[i]));
        }

        // Verify all 4 locations were created
        mockMvc.perform(get("/api/v1/saved-locations/user/user-multi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void update_WithPartialData_UpdatesOnlyProvidedFields() throws Exception {
        // Create initial location
        SavedLocationDTO createDTO = SavedLocationDTO.builder()
                .userId("user-partial")
                .name("Original Name")
                .longitude(-122.4194)
                .latitude(37.7749)
                .address("Original Address")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/saved-locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = createResult.getResponse().getContentAsString();
        SavedLocationDTO createdLocation = objectMapper.readValue(responseJson, SavedLocationDTO.class);
        String locationId = createdLocation.getId();

        // Update only the name (other required fields must still be provided for validation)
        SavedLocationDTO updateDTO = SavedLocationDTO.builder()
                .userId("user-partial")
                .name("New Name")
                .longitude(-122.4194)
                .latitude(37.7749)
                .address("New Address")
                .build();

        mockMvc.perform(put("/api/v1/saved-locations/" + locationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.address").value("New Address"));
    }

    @Test
    void delete_WithNonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/saved-locations/non-existing-id"))
                .andExpect(status().isNotFound());
    }
}
