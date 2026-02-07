package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.HazardVerificationDTO;
import hazardhub.com.hub.model.enums.VerificationType;
import hazardhub.com.hub.service.HazardVerificationService;
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
class HazardVerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @MockitoBean
    private HazardVerificationService hazardVerificationService;

    @MockitoBean
    private UserService userService;

    // ==================== CREATE TESTS ====================

    @Test
    void create_WithValidDTO_ReturnsCreatedVerification() throws Exception {
        // Arrange
        HazardVerificationDTO inputDTO = HazardVerificationDTO.builder()
                .hazardId("hazard-123")
                .userId("user-123")
                .verificationType(VerificationType.CONFIRM)
                .comment("Verified this hazard")
                .build();

        HazardVerificationDTO savedDTO = HazardVerificationDTO.builder()
                .id("verification-001")
                .hazardId("hazard-123")
                .userId("user-123")
                .verificationType(VerificationType.CONFIRM)
                .comment("Verified this hazard")
                .build();

        when(hazardVerificationService.create(any(HazardVerificationDTO.class))).thenReturn(savedDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("verification-001"))
                .andExpect(jsonPath("$.hazardId").value("hazard-123"))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.verificationType").value("CONFIRM"))
                .andExpect(jsonPath("$.comment").value("Verified this hazard"));
    }

    @Test
    void create_WithNullHazardId_ReturnsBadRequest() throws Exception {
        // Arrange
        HazardVerificationDTO inputDTO = HazardVerificationDTO.builder()
                .hazardId(null)
                .userId("user-123")
                .verificationType(VerificationType.CONFIRM)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithNullUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        HazardVerificationDTO inputDTO = HazardVerificationDTO.builder()
                .hazardId("hazard-123")
                .userId(null)
                .verificationType(VerificationType.CONFIRM)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithNullVerificationType_ReturnsBadRequest() throws Exception {
        // Arrange
        HazardVerificationDTO inputDTO = HazardVerificationDTO.builder()
                .hazardId("hazard-123")
                .userId("user-123")
                .verificationType(null)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/hazard-verifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    // ==================== GET BY ID TESTS ====================

    @Test
    void findById_WithExistingId_ReturnsVerification() throws Exception {
        // Arrange
        HazardVerificationDTO dto = HazardVerificationDTO.builder()
                .id("verification-001")
                .hazardId("hazard-123")
                .userId("user-123")
                .verificationType(VerificationType.CONFIRM)
                .comment("Verified this hazard")
                .build();

        when(hazardVerificationService.findById("verification-001")).thenReturn(Optional.of(dto));

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazard-verifications/verification-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("verification-001"))
                .andExpect(jsonPath("$.hazardId").value("hazard-123"))
                .andExpect(jsonPath("$.verificationType").value("CONFIRM"));
    }

    @Test
    void findById_WithNonExistingId_ReturnsNotFound() throws Exception {
        // Arrange
        when(hazardVerificationService.findById("non-existing-id")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazard-verifications/non-existing-id"))
                .andExpect(status().isNotFound());
    }

    // ==================== GET ALL TESTS ====================

    @Test
    void findAll_ReturnsListOfVerifications() throws Exception {
        // Arrange
        HazardVerificationDTO verification1 = HazardVerificationDTO.builder()
                .id("verification-001")
                .hazardId("hazard-123")
                .userId("user-123")
                .verificationType(VerificationType.CONFIRM)
                .build();

        HazardVerificationDTO verification2 = HazardVerificationDTO.builder()
                .id("verification-002")
                .hazardId("hazard-123")
                .userId("user-456")
                .verificationType(VerificationType.DISPUTE)
                .build();

        when(hazardVerificationService.findAll()).thenReturn(List.of(verification1, verification2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazard-verifications"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("verification-001"))
                .andExpect(jsonPath("$[1].id").value("verification-002"));
    }

    @Test
    void findAll_WhenEmpty_ReturnsEmptyList() throws Exception {
        // Arrange
        when(hazardVerificationService.findAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazard-verifications"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ==================== UPDATE TESTS ====================

    @Test
    void update_WithValidData_ReturnsUpdatedVerification() throws Exception {
        // Arrange
        HazardVerificationDTO inputDTO = HazardVerificationDTO.builder()
                .hazardId("hazard-123")
                .userId("user-123")
                .verificationType(VerificationType.DISPUTE)
                .comment("Changed my mind, disputing this")
                .build();

        HazardVerificationDTO updatedDTO = HazardVerificationDTO.builder()
                .id("mock-id")
                .hazardId("hazard-123")
                .userId("user-123")
                .verificationType(VerificationType.DISPUTE)
                .comment("Changed my mind, disputing this")
                .build();

        when(hazardVerificationService.update(eq("mock-id"), any(HazardVerificationDTO.class)))
                .thenReturn(updatedDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/hazard-verifications/mock-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("mock-id"))
                .andExpect(jsonPath("$.verificationType").value("DISPUTE"))
                .andExpect(jsonPath("$.comment").value("Changed my mind, disputing this"));
    }

    @Test
    void update_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Arrange - missing required fields
        HazardVerificationDTO inputDTO = HazardVerificationDTO.builder()
                .hazardId(null)
                .userId("user-123")
                .verificationType(VerificationType.CONFIRM)
                .build();

        // Act & Assert
        mockMvc.perform(put("/api/v1/hazard-verifications/verification-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    // ==================== DELETE TESTS ====================

    @Test
    void delete_WithExistingId_ReturnsNoContent() throws Exception {
        // Arrange
        doNothing().when(hazardVerificationService).delete("verification-001");

        // Act & Assert
        mockMvc.perform(delete("/api/v1/hazard-verifications/verification-001"))
                .andExpect(status().isNoContent());
    }

    // ==================== FIND BY HAZARD ID TESTS ====================

    @Test
    void findByHazardId_ReturnsListOfVerifications() throws Exception {
        // Arrange
        HazardVerificationDTO verification1 = HazardVerificationDTO.builder()
                .id("verification-001")
                .hazardId("hazard-123")
                .userId("user-123")
                .verificationType(VerificationType.CONFIRM)
                .build();

        HazardVerificationDTO verification2 = HazardVerificationDTO.builder()
                .id("verification-002")
                .hazardId("hazard-123")
                .userId("user-456")
                .verificationType(VerificationType.CONFIRM)
                .build();

        when(hazardVerificationService.findByHazardId("hazard-123"))
                .thenReturn(List.of(verification1, verification2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazard-verifications/hazard/hazard-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].hazardId").value("hazard-123"))
                .andExpect(jsonPath("$[1].hazardId").value("hazard-123"));
    }

    // ==================== FIND BY USER ID TESTS ====================

    @Test
    void findByUserId_ReturnsListOfVerifications() throws Exception {
        // Arrange
        HazardVerificationDTO verification1 = HazardVerificationDTO.builder()
                .id("verification-001")
                .hazardId("hazard-123")
                .userId("user-123")
                .verificationType(VerificationType.CONFIRM)
                .build();

        HazardVerificationDTO verification2 = HazardVerificationDTO.builder()
                .id("verification-002")
                .hazardId("hazard-456")
                .userId("user-123")
                .verificationType(VerificationType.DISPUTE)
                .build();

        when(hazardVerificationService.findByUserId("user-123"))
                .thenReturn(List.of(verification1, verification2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazard-verifications/user/user-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("user-123"))
                .andExpect(jsonPath("$[1].userId").value("user-123"));
    }

    // ==================== FIND BY VERIFICATION TYPE TESTS ====================

    @Test
    void findByVerificationType_ReturnsListOfVerifications() throws Exception {
        // Arrange
        HazardVerificationDTO verification1 = HazardVerificationDTO.builder()
                .id("verification-001")
                .hazardId("hazard-123")
                .userId("user-123")
                .verificationType(VerificationType.CONFIRM)
                .build();

        when(hazardVerificationService.findByVerificationType(VerificationType.CONFIRM))
                .thenReturn(List.of(verification1));

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazard-verifications/type/CONFIRM"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].verificationType").value("CONFIRM"));
    }

    // ==================== FIND BY HAZARD ID AND USER ID TESTS ====================

    @Test
    void findByHazardIdAndUserId_WithExisting_ReturnsVerification() throws Exception {
        // Arrange
        HazardVerificationDTO dto = HazardVerificationDTO.builder()
                .id("verification-001")
                .hazardId("hazard-123")
                .userId("user-123")
                .verificationType(VerificationType.CONFIRM)
                .build();

        when(hazardVerificationService.findByHazardIdAndUserId("hazard-123", "user-123"))
                .thenReturn(Optional.of(dto));

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazard-verifications/hazard/hazard-123/user/user-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("verification-001"))
                .andExpect(jsonPath("$.hazardId").value("hazard-123"))
                .andExpect(jsonPath("$.userId").value("user-123"));
    }

    @Test
    void findByHazardIdAndUserId_WithNonExisting_ReturnsNotFound() throws Exception {
        // Arrange
        when(hazardVerificationService.findByHazardIdAndUserId("hazard-123", "user-999"))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazard-verifications/hazard/hazard-123/user/user-999"))
                .andExpect(status().isNotFound());
    }

    // ==================== FIND BY HAZARD ID AND VERIFICATION TYPE TESTS ====================

    @Test
    void findByHazardIdAndVerificationType_ReturnsListOfVerifications() throws Exception {
        // Arrange
        HazardVerificationDTO verification1 = HazardVerificationDTO.builder()
                .id("verification-001")
                .hazardId("hazard-123")
                .userId("user-123")
                .verificationType(VerificationType.CONFIRM)
                .build();

        HazardVerificationDTO verification2 = HazardVerificationDTO.builder()
                .id("verification-002")
                .hazardId("hazard-123")
                .userId("user-456")
                .verificationType(VerificationType.CONFIRM)
                .build();

        when(hazardVerificationService.findByHazardIdAndVerificationType("hazard-123", VerificationType.CONFIRM))
                .thenReturn(List.of(verification1, verification2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazard-verifications/hazard/hazard-123/type/CONFIRM"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].verificationType").value("CONFIRM"))
                .andExpect(jsonPath("$[1].verificationType").value("CONFIRM"));
    }

    // ==================== COUNT BY HAZARD ID AND VERIFICATION TYPE TESTS ====================

    @Test
    void countByHazardIdAndVerificationType_ReturnsCount() throws Exception {
        // Arrange
        when(hazardVerificationService.countByHazardIdAndVerificationType("hazard-123", VerificationType.CONFIRM))
                .thenReturn(5L);

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazard-verifications/hazard/hazard-123/type/CONFIRM/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    // ==================== EXISTS BY HAZARD ID AND USER ID TESTS ====================

    @Test
    void existsByHazardIdAndUserId_WhenExists_ReturnsTrue() throws Exception {
        // Arrange
        when(hazardVerificationService.existsByHazardIdAndUserId("hazard-123", "user-123"))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazard-verifications/hazard/hazard-123/user/user-123/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void existsByHazardIdAndUserId_WhenNotExists_ReturnsFalse() throws Exception {
        // Arrange
        when(hazardVerificationService.existsByHazardIdAndUserId("hazard-123", "user-999"))
                .thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazard-verifications/hazard/hazard-123/user/user-999/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
