package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.HazardDTO;
import hazardhub.com.hub.model.enums.HazardSeverity;
import hazardhub.com.hub.model.enums.HazardStatus;
import hazardhub.com.hub.service.HazardService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}

