package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.SavedLocationDTO;
import hazardhub.com.hub.service.SavedLocationService;
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
class SavedLocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @MockitoBean
    private SavedLocationService savedLocationService;

    @MockitoBean
    private UserService userService;

    // ==================== CREATE TESTS ====================

    @Test
    void create_WithValidSavedLocationDTO_ReturnsCreatedLocation() throws Exception {
        // Arrange
        SavedLocationDTO inputDTO = SavedLocationDTO.builder()
                .userId("user-123")
                .name("Home")
                .longitude(-122.4194)
                .latitude(37.7749)
                .address("123 Main St, San Francisco, CA")
                .build();

        SavedLocationDTO savedDTO = SavedLocationDTO.builder()
                .id("location-001")
                .userId("user-123")
                .name("Home")
                .longitude(-122.4194)
                .latitude(37.7749)
                .address("123 Main St, San Francisco, CA")
                .build();

        when(savedLocationService.create(any(SavedLocationDTO.class))).thenReturn(savedDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/saved-locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("location-001"))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.name").value("Home"))
                .andExpect(jsonPath("$.longitude").value(-122.4194))
                .andExpect(jsonPath("$.latitude").value(37.7749))
                .andExpect(jsonPath("$.address").value("123 Main St, San Francisco, CA"));
    }

    @Test
    void create_WithNullUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        SavedLocationDTO inputDTO = SavedLocationDTO.builder()
                .userId(null)
                .name("Home")
                .longitude(-122.4194)
                .latitude(37.7749)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/saved-locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithNullName_ReturnsBadRequest() throws Exception {
        // Arrange
        SavedLocationDTO inputDTO = SavedLocationDTO.builder()
                .userId("user-123")
                .name(null)
                .longitude(-122.4194)
                .latitude(37.7749)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/saved-locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithNullCoordinates_ReturnsBadRequest() throws Exception {
        // Arrange - null longitude
        SavedLocationDTO inputWithNullLongitude = SavedLocationDTO.builder()
                .userId("user-123")
                .name("Home")
                .longitude(null)
                .latitude(37.7749)
                .build();

        // Act & Assert - null longitude
        mockMvc.perform(post("/api/v1/saved-locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWithNullLongitude)))
                .andExpect(status().isBadRequest());

        // Arrange - null latitude
        SavedLocationDTO inputWithNullLatitude = SavedLocationDTO.builder()
                .userId("user-123")
                .name("Home")
                .longitude(-122.4194)
                .latitude(null)
                .build();

        // Act & Assert - null latitude
        mockMvc.perform(post("/api/v1/saved-locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWithNullLatitude)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithInvalidLongitude_ReturnsBadRequest() throws Exception {
        // Arrange - longitude > 180
        SavedLocationDTO inputDTO = SavedLocationDTO.builder()
                .userId("user-123")
                .name("Home")
                .longitude(181.0)
                .latitude(37.7749)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/saved-locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_WithInvalidLatitude_ReturnsBadRequest() throws Exception {
        // Arrange - latitude > 90
        SavedLocationDTO inputDTO = SavedLocationDTO.builder()
                .userId("user-123")
                .name("Home")
                .longitude(-122.4194)
                .latitude(91.0)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/saved-locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    // ==================== GET BY ID TESTS ====================

    @Test
    void findById_WithExistingId_ReturnsLocation() throws Exception {
        // Arrange
        SavedLocationDTO locationDTO = SavedLocationDTO.builder()
                .id("location-001")
                .userId("user-123")
                .name("Home")
                .longitude(-122.4194)
                .latitude(37.7749)
                .address("123 Main St, San Francisco, CA")
                .build();

        when(savedLocationService.findById("location-001")).thenReturn(Optional.of(locationDTO));

        // Act & Assert
        mockMvc.perform(get("/api/v1/saved-locations/location-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("location-001"))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.name").value("Home"));
    }

    @Test
    void findById_WithNonExistingId_ReturnsNotFound() throws Exception {
        // Arrange
        when(savedLocationService.findById("non-existing-id")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/saved-locations/non-existing-id"))
                .andExpect(status().isNotFound());
    }

    // ==================== GET ALL TESTS ====================

    @Test
    void findAll_ReturnsListOfLocations() throws Exception {
        // Arrange
        SavedLocationDTO location1 = SavedLocationDTO.builder()
                .id("location-001")
                .userId("user-123")
                .name("Home")
                .longitude(-122.4194)
                .latitude(37.7749)
                .address("123 Main St, San Francisco, CA")
                .build();

        SavedLocationDTO location2 = SavedLocationDTO.builder()
                .id("location-002")
                .userId("user-123")
                .name("Work")
                .longitude(-122.4000)
                .latitude(37.7800)
                .address("456 Office Blvd, San Francisco, CA")
                .build();

        when(savedLocationService.findAll()).thenReturn(List.of(location1, location2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/saved-locations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("location-001"))
                .andExpect(jsonPath("$[0].name").value("Home"))
                .andExpect(jsonPath("$[1].id").value("location-002"))
                .andExpect(jsonPath("$[1].name").value("Work"));
    }

    @Test
    void findAll_WhenEmpty_ReturnsEmptyList() throws Exception {
        // Arrange
        when(savedLocationService.findAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/saved-locations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ==================== GET BY USER ID TESTS ====================

    @Test
    void findByUserId_WithExistingUser_ReturnsLocations() throws Exception {
        // Arrange
        SavedLocationDTO location1 = SavedLocationDTO.builder()
                .id("location-001")
                .userId("user-123")
                .name("Home")
                .longitude(-122.4194)
                .latitude(37.7749)
                .build();

        SavedLocationDTO location2 = SavedLocationDTO.builder()
                .id("location-002")
                .userId("user-123")
                .name("Work")
                .longitude(-122.4000)
                .latitude(37.7800)
                .build();

        when(savedLocationService.findByUserId("user-123")).thenReturn(List.of(location1, location2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/saved-locations/user/user-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("user-123"))
                .andExpect(jsonPath("$[1].userId").value("user-123"));
    }

    @Test
    void findByUserId_WithNoLocations_ReturnsEmptyList() throws Exception {
        // Arrange
        when(savedLocationService.findByUserId("user-456")).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/saved-locations/user/user-456"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ==================== UPDATE TESTS ====================

    @Test
    void update_WithValidData_ReturnsUpdatedLocation() throws Exception {
        // Arrange
        SavedLocationDTO inputDTO = SavedLocationDTO.builder()
                .userId("user-123")
                .name("Updated Home")
                .longitude(-122.4200)
                .latitude(37.7750)
                .address("456 New St, San Francisco, CA")
                .build();

        SavedLocationDTO updatedDTO = SavedLocationDTO.builder()
                .id("location-001")
                .userId("user-123")
                .name("Updated Home")
                .longitude(-122.4200)
                .latitude(37.7750)
                .address("456 New St, San Francisco, CA")
                .build();

        when(savedLocationService.update(eq("location-001"), any(SavedLocationDTO.class))).thenReturn(updatedDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/saved-locations/location-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("location-001"))
                .andExpect(jsonPath("$.name").value("Updated Home"))
                .andExpect(jsonPath("$.longitude").value(-122.4200))
                .andExpect(jsonPath("$.latitude").value(37.7750))
                .andExpect(jsonPath("$.address").value("456 New St, San Francisco, CA"));
    }

    @Test
    void update_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Arrange - missing required fields
        SavedLocationDTO inputDTO = SavedLocationDTO.builder()
                .userId(null)
                .name("Home")
                .longitude(-122.4194)
                .latitude(37.7749)
                .build();

        // Act & Assert
        mockMvc.perform(put("/api/v1/saved-locations/location-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isBadRequest());
    }

    // ==================== DELETE TESTS ====================

    @Test
    void delete_WithExistingId_ReturnsNoContent() throws Exception {
        // Arrange
        doNothing().when(savedLocationService).delete("location-001");

        // Act & Assert
        mockMvc.perform(delete("/api/v1/saved-locations/location-001"))
                .andExpect(status().isNoContent());
    }
}
