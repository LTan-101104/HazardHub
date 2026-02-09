package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.HazardDTO;
import hazardhub.com.hub.model.enums.HazardSeverity;
import hazardhub.com.hub.repository.HazardRepository;
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
class HazardControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HazardRepository hazardRepository;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @BeforeEach
    void setUp() {
        hazardRepository.deleteAll();
    }

    @Test
    void fullCrudFlow_Integration() throws Exception {
        // ==================== CREATE ====================
        HazardDTO createDTO = HazardDTO.builder()
                .reporterId("user-123")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .severity(HazardSeverity.HIGH)
                .description("Pothole on main street")
                .affectedRadiusMeters(50.0)
                .build();
        MvcResult createResult = mockMvc.perform(post("/api/v1/hazards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.reporterId").value("user-123"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        // Extract the created ID for subsequent operations
        String responseJson = createResult.getResponse().getContentAsString();
        HazardDTO createdHazard = objectMapper.readValue(responseJson, HazardDTO.class);
        String hazardId = createdHazard.getId();

        // ==================== READ BY ID ====================
        mockMvc.perform(get("/api/v1/hazards/" + hazardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(hazardId))
                .andExpect(jsonPath("$.reporterId").value("user-123"))
                .andExpect(jsonPath("$.description").value("Pothole on main street"));

        // ==================== READ ALL ====================
        mockMvc.perform(get("/api/v1/hazards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(hazardId));

        // ==================== UPDATE ====================
        HazardDTO updateDTO = HazardDTO.builder()
                .reporterId("user-123")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .severity(HazardSeverity.MEDIUM)
                .description("Updated: Small pothole on main street")
                .affectedRadiusMeters(30.0)
                .build();

        mockMvc.perform(put("/api/v1/hazards/" + hazardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(hazardId))
                .andExpect(jsonPath("$.severity").value("MEDIUM"))
                .andExpect(jsonPath("$.description").value("Updated: Small pothole on main street"))
                .andExpect(jsonPath("$.affectedRadiusMeters").value(30.0));

        // Verify the update persisted
        mockMvc.perform(get("/api/v1/hazards/" + hazardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.severity").value("MEDIUM"));

        // ==================== DELETE ====================
        mockMvc.perform(delete("/api/v1/hazards/" + hazardId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/v1/hazards/" + hazardId))
                .andExpect(status().isNotFound());

        // Verify list is empty
        mockMvc.perform(get("/api/v1/hazards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void create_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Missing required fields
        HazardDTO invalidDTO = HazardDTO.builder()
                .reporterId(null)
                .longitude(-122.4194)
                .latitude(37.7749)
                .build();

        mockMvc.perform(post("/api/v1/hazards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_WithNonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/hazards/non-existing-id"))
                .andExpect(status().isNotFound());
    }
}
