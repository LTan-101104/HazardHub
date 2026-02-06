package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.HazardVerificationDTO;
import hazardhub.com.hub.model.enums.VerificationType;
import hazardhub.com.hub.repository.HazardVerificationRepository;
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
class HazardVerificationControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HazardVerificationRepository hazardVerificationRepository;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @BeforeEach
    void setUp() {
        hazardVerificationRepository.deleteAll();
    }

    @Test
    void fullCrudFlow_Integration() throws Exception {
        // ==================== CREATE ====================
        HazardVerificationDTO createDTO = HazardVerificationDTO.builder()
                .hazardId("hazard-123")
                .userId("user-123")
                .verificationType(VerificationType.CONFIRM)
                .comment("I can confirm this hazard exists")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.hazardId").value("hazard-123"))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.verificationType").value("CONFIRM"))
                .andExpect(jsonPath("$.comment").value("I can confirm this hazard exists"))
                .andReturn();

        // Extract the created ID for subsequent operations
        String responseJson = createResult.getResponse().getContentAsString();
        HazardVerificationDTO createdVerification = objectMapper.readValue(responseJson, HazardVerificationDTO.class);
        String verificationId = createdVerification.getId();

        // ==================== READ BY ID ====================
        mockMvc.perform(get("/api/v1/hazard-verifications/" + verificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(verificationId))
                .andExpect(jsonPath("$.hazardId").value("hazard-123"))
                .andExpect(jsonPath("$.userId").value("user-123"));

        // ==================== READ ALL ====================
        mockMvc.perform(get("/api/v1/hazard-verifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(verificationId));

        // ==================== UPDATE ====================
        HazardVerificationDTO updateDTO = HazardVerificationDTO.builder()
                .hazardId("hazard-123")
                .userId("user-123")
                .verificationType(VerificationType.DISPUTE)
                .comment("Actually, I'm disputing this now")
                .build();

        mockMvc.perform(put("/api/v1/hazard-verifications/" + verificationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(verificationId))
                .andExpect(jsonPath("$.verificationType").value("DISPUTE"))
                .andExpect(jsonPath("$.comment").value("Actually, I'm disputing this now"));

        // Verify the update persisted
        mockMvc.perform(get("/api/v1/hazard-verifications/" + verificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationType").value("DISPUTE"));

        // ==================== DELETE ====================
        mockMvc.perform(delete("/api/v1/hazard-verifications/" + verificationId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/v1/hazard-verifications/" + verificationId))
                .andExpect(status().isNotFound());

        // Verify list is empty
        mockMvc.perform(get("/api/v1/hazard-verifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void create_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Missing required fields
        HazardVerificationDTO invalidDTO = HazardVerificationDTO.builder()
                .hazardId(null)
                .userId("user-123")
                .verificationType(VerificationType.CONFIRM)
                .build();

        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_WithNonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/hazard-verifications/non-existing-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByHazardId_ReturnsVerificationsForHazard() throws Exception {
        // Create two verifications for the same hazard
        HazardVerificationDTO verification1 = HazardVerificationDTO.builder()
                .hazardId("hazard-456")
                .userId("user-111")
                .verificationType(VerificationType.CONFIRM)
                .build();

        HazardVerificationDTO verification2 = HazardVerificationDTO.builder()
                .hazardId("hazard-456")
                .userId("user-222")
                .verificationType(VerificationType.CONFIRM)
                .build();

        // Create a verification for a different hazard
        HazardVerificationDTO verification3 = HazardVerificationDTO.builder()
                .hazardId("hazard-789")
                .userId("user-333")
                .verificationType(VerificationType.DISPUTE)
                .build();

        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verification1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verification2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verification3)))
                .andExpect(status().isCreated());

        // Find by hazard ID should return only 2
        mockMvc.perform(get("/api/v1/hazard-verifications/hazard/hazard-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].hazardId").value("hazard-456"))
                .andExpect(jsonPath("$[1].hazardId").value("hazard-456"));
    }

    @Test
    void findByUserId_ReturnsVerificationsByUser() throws Exception {
        // Create verifications by the same user for different hazards
        HazardVerificationDTO verification1 = HazardVerificationDTO.builder()
                .hazardId("hazard-aaa")
                .userId("user-same")
                .verificationType(VerificationType.CONFIRM)
                .build();

        HazardVerificationDTO verification2 = HazardVerificationDTO.builder()
                .hazardId("hazard-bbb")
                .userId("user-same")
                .verificationType(VerificationType.DISPUTE)
                .build();

        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verification1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verification2)))
                .andExpect(status().isCreated());

        // Find by user ID
        mockMvc.perform(get("/api/v1/hazard-verifications/user/user-same"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("user-same"))
                .andExpect(jsonPath("$[1].userId").value("user-same"));
    }

    @Test
    void findByVerificationType_ReturnsVerificationsByType() throws Exception {
        // Create CONFIRM verifications
        HazardVerificationDTO confirm1 = HazardVerificationDTO.builder()
                .hazardId("hazard-c1")
                .userId("user-c1")
                .verificationType(VerificationType.CONFIRM)
                .build();

        HazardVerificationDTO confirm2 = HazardVerificationDTO.builder()
                .hazardId("hazard-c2")
                .userId("user-c2")
                .verificationType(VerificationType.CONFIRM)
                .build();

        // Create DISPUTE verification
        HazardVerificationDTO dispute1 = HazardVerificationDTO.builder()
                .hazardId("hazard-d1")
                .userId("user-d1")
                .verificationType(VerificationType.DISPUTE)
                .build();

        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirm1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirm2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dispute1)))
                .andExpect(status().isCreated());

        // Find by CONFIRM type
        mockMvc.perform(get("/api/v1/hazard-verifications/type/CONFIRM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // Find by DISPUTE type
        mockMvc.perform(get("/api/v1/hazard-verifications/type/DISPUTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findByHazardIdAndUserId_ReturnsSpecificVerification() throws Exception {
        HazardVerificationDTO verification = HazardVerificationDTO.builder()
                .hazardId("hazard-specific")
                .userId("user-specific")
                .verificationType(VerificationType.CONFIRM)
                .build();

        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verification)))
                .andExpect(status().isCreated());

        // Find by hazard ID and user ID
        mockMvc.perform(get("/api/v1/hazard-verifications/hazard/hazard-specific/user/user-specific"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hazardId").value("hazard-specific"))
                .andExpect(jsonPath("$.userId").value("user-specific"));

        // Non-existing combination
        mockMvc.perform(get("/api/v1/hazard-verifications/hazard/hazard-specific/user/user-other"))
                .andExpect(status().isNotFound());
    }

    @Test
    void countByHazardIdAndVerificationType_ReturnsCorrectCount() throws Exception {
        // Create multiple CONFIRM verifications for the same hazard
        for (int i = 1; i <= 3; i++) {
            HazardVerificationDTO verification = HazardVerificationDTO.builder()
                    .hazardId("hazard-count")
                    .userId("user-" + i)
                    .verificationType(VerificationType.CONFIRM)
                    .build();

            mockMvc.perform(post("/api/v1/hazard-verifications")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(verification)))
                    .andExpect(status().isCreated());
        }

        // Create one DISPUTE for the same hazard
        HazardVerificationDTO dispute = HazardVerificationDTO.builder()
                .hazardId("hazard-count")
                .userId("user-disputer")
                .verificationType(VerificationType.DISPUTE)
                .build();

        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dispute)))
                .andExpect(status().isCreated());

        // Count CONFIRM
        mockMvc.perform(get("/api/v1/hazard-verifications/hazard/hazard-count/type/CONFIRM/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));

        // Count DISPUTE
        mockMvc.perform(get("/api/v1/hazard-verifications/hazard/hazard-count/type/DISPUTE/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void existsByHazardIdAndUserId_ReturnsCorrectBoolean() throws Exception {
        HazardVerificationDTO verification = HazardVerificationDTO.builder()
                .hazardId("hazard-exists")
                .userId("user-exists")
                .verificationType(VerificationType.CONFIRM)
                .build();

        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verification)))
                .andExpect(status().isCreated());

        // Exists - should return true
        mockMvc.perform(get("/api/v1/hazard-verifications/hazard/hazard-exists/user/user-exists/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // Does not exist - should return false
        mockMvc.perform(get("/api/v1/hazard-verifications/hazard/hazard-exists/user/user-other/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
