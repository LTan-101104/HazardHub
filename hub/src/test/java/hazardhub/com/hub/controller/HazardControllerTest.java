package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.HazardDTO;
import hazardhub.com.hub.model.enums.HazardSeverity;
import hazardhub.com.hub.model.enums.HazardStatus;
import hazardhub.com.hub.service.HazardService;
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
class HazardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @MockitoBean
    private HazardService hazardService;

    @MockitoBean
    private UserService userService;

    @Test
    void create_WithValidHazardDTO_ReturnsCreatedHazard() throws Exception {
        // Arrange
        HazardDTO inputDTO = HazardDTO.builder()
                .reporterId("user-123")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .severity(HazardSeverity.HIGH)
                .description("Pothole on main street")
                .affectedRadiusMeters(50.0)
                .build();

        HazardDTO savedDTO = HazardDTO.builder()
                .id("hazard-001")
                .reporterId("user-123")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .severity(HazardSeverity.HIGH)
                .description("Pothole on main street")
                .affectedRadiusMeters(50.0)
                .status(HazardStatus.PENDING)
                .verificationCount(0)
                .disputeCount(0)
                .build();

        when(hazardService.create(any(HazardDTO.class))).thenReturn(savedDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/hazards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("hazard-001"))
                .andExpect(jsonPath("$.reporterId").value("user-123"))
                .andExpect(jsonPath("$.longitude").value(-122.4194))
                .andExpect(jsonPath("$.latitude").value(37.7749))
                .andExpect(jsonPath("$.severity").value("HIGH"))
                .andExpect(jsonPath("$.description").value("Pothole on main street"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.verificationCount").value(0))
                .andExpect(jsonPath("$.disputeCount").value(0));
    }

    @Test
    void create_WithNullReporterId_ReturnsBadRequest() throws Exception {
        // Arrange
        HazardDTO inputDTO = HazardDTO.builder()
                .reporterId(null)
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .severity(HazardSeverity.HIGH)
                .description("Pothole on main street")
                .affectedRadiusMeters(50.0)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/hazards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithNullCoordinates_ReturnsBadRequest() throws Exception {
        // Arrange - null longitude
        HazardDTO inputWithNullLongitude = HazardDTO.builder()
                .reporterId("user-123")
                .longitude(null)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .severity(HazardSeverity.HIGH)
                .description("Pothole on main street")
                .affectedRadiusMeters(50.0)
                .build();

        // Act & Assert - null longitude
        mockMvc.perform(post("/api/v1/hazards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWithNullLongitude)))
                .andExpect(status().isBadRequest());

        // Arrange - null latitude
        HazardDTO inputWithNullLatitude = HazardDTO.builder()
                .reporterId("user-123")
                .longitude(-122.4194)
                .latitude(null)
                .locationAccuracyMeters(10.0)
                .severity(HazardSeverity.HIGH)
                .description("Pothole on main street")
                .affectedRadiusMeters(50.0)
                .build();

        // Act & Assert - null latitude
        mockMvc.perform(post("/api/v1/hazards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWithNullLatitude)))
                .andExpect(status().isBadRequest());
    }

    // ==================== GET BY ID TESTS ====================

    @Test
    void findById_WithExistingId_ReturnsHazard() throws Exception {
        // Arrange
        HazardDTO hazardDTO = HazardDTO.builder()
                .id("hazard-001")
                .reporterId("user-123")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .severity(HazardSeverity.HIGH)
                .description("Pothole on main street")
                .affectedRadiusMeters(50.0)
                .status(HazardStatus.PENDING)
                .verificationCount(0)
                .disputeCount(0)
                .build();

        when(hazardService.findById("hazard-001")).thenReturn(Optional.of(hazardDTO));

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazards/hazard-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("hazard-001"))
                .andExpect(jsonPath("$.reporterId").value("user-123"))
                .andExpect(jsonPath("$.severity").value("HIGH"));
    }

    @Test
    void findById_WithNonExistingId_ReturnsNotFound() throws Exception {
        // Arrange
        when(hazardService.findById("non-existing-id")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazards/non-existing-id"))
                .andExpect(status().isNotFound());
    }

    // ==================== GET ALL TESTS ====================

    @Test
    void findAll_ReturnsListOfHazards() throws Exception {
        // Arrange
        HazardDTO hazard1 = HazardDTO.builder()
                .id("hazard-001")
                .reporterId("user-123")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .severity(HazardSeverity.HIGH)
                .description("Pothole on main street")
                .affectedRadiusMeters(50.0)
                .status(HazardStatus.PENDING)
                .build();

        HazardDTO hazard2 = HazardDTO.builder()
                .id("hazard-002")
                .reporterId("user-456")
                .longitude(-122.4000)
                .latitude(37.7800)
                .locationAccuracyMeters(5.0)
                .severity(HazardSeverity.LOW)
                .description("Broken sidewalk")
                .affectedRadiusMeters(20.0)
                .status(HazardStatus.ACTIVE)
                .build();

        when(hazardService.findAll()).thenReturn(List.of(hazard1, hazard2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazards"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("hazard-001"))
                .andExpect(jsonPath("$[1].id").value("hazard-002"));
    }

    @Test
    void findAll_WhenEmpty_ReturnsEmptyList() throws Exception {
        // Arrange
        when(hazardService.findAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/hazards"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ==================== UPDATE TESTS ====================

    @Test
    void update_WithValidData_ReturnsUpdatedHazard() throws Exception {
        // Arrange
        HazardDTO inputDTO = HazardDTO.builder()
                .reporterId("user-123")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .severity(HazardSeverity.MEDIUM)
                .description("Updated description")
                .affectedRadiusMeters(75.0)
                .build();

        HazardDTO updatedDTO = HazardDTO.builder()
                .id("hazard-001")
                .reporterId("user-123")
                .longitude(-122.4194)
                .latitude(37.7749)
                .locationAccuracyMeters(10.0)
                .severity(HazardSeverity.MEDIUM)
                .description("Updated description")
                .affectedRadiusMeters(75.0)
                .status(HazardStatus.PENDING)
                .verificationCount(0)
                .disputeCount(0)
                .build();

        when(hazardService.update(eq("hazard-001"), any(HazardDTO.class))).thenReturn(updatedDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/hazards/hazard-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("hazard-001"))
                .andExpect(jsonPath("$.severity").value("MEDIUM"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.affectedRadiusMeters").value(75.0));
    }

    @Test
    void update_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Arrange - missing required fields
        HazardDTO inputDTO = HazardDTO.builder()
                .reporterId(null)
                .longitude(-122.4194)
                .latitude(37.7749)
                .build();

        // Act & Assert
        mockMvc.perform(put("/api/v1/hazards/hazard-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    // ==================== DELETE TESTS ====================

    @Test
    void delete_WithExistingId_ReturnsNoContent() throws Exception {
        // Arrange
        doNothing().when(hazardService).delete("hazard-001");

        // Act & Assert
        mockMvc.perform(delete("/api/v1/hazards/hazard-001"))
                .andExpect(status().isNoContent());
    }
}

