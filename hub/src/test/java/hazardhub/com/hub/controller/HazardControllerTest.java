// package hazardhub.com.hub.controller;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import hazardhub.com.hub.model.dto.HazardDTO;
// import hazardhub.com.hub.model.entity.Hazard;
// import hazardhub.com.hub.model.enums.HazardSeverity;
// import hazardhub.com.hub.model.enums.HazardStatus;
// import hazardhub.com.hub.service.HazardService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
// import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;

// import java.time.Instant;
// import java.util.Optional;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.*;
// import static
// org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static
// org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(HazardController.class)
// @ActiveProfiles("test")
// class HazardControllerTest {

// @Autowired
// private MockMvc mockMvc;

// private final ObjectMapper objectMapper = new ObjectMapper();

// @MockitoBean
// private HazardService hazardService;

// private Hazard testHazard;
// private HazardDTO testHazardDTO;

// @BeforeEach
// void setUp() {
// testHazard = Hazard.builder()
// .id("hazard-123")
// .reporterId("user-456")
// .location(new GeoJsonPoint(-122.4194, 37.7749))
// .address("123 Test Street, San Francisco, CA")
// .severity(HazardSeverity.HIGH)
// .description("Pothole on the road")
// .status(HazardStatus.ACTIVE)
// .verificationCount(0)
// .disputeCount(0)
// .expiresAt(Instant.now().plusSeconds(86400))
// .build();

// testHazardDTO = HazardDTO.builder()
// .reporterId("user-456")
// .longitude(-122.4194)
// .latitude(37.7749)
// .address("123 Test Street, San Francisco, CA")
// .severity(HazardSeverity.HIGH)
// .description("Pothole on the road")
// .status(HazardStatus.ACTIVE)
// .build();
// }

// @Nested
// @DisplayName("Create Hazard")
// class CreateHazard {

// @Test
// @WithMockUser(username = "testUser")
// @DisplayName("should create hazard and return 201 CREATED")
// void shouldCreateHazard() throws Exception {
// when(hazardService.create(any(HazardDTO.class))).thenReturn(testHazard);

// mockMvc.perform(post("/api/v1/hazards")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(testHazardDTO)))
// .andExpect(status().isCreated())
// .andExpect(jsonPath("$.id").value("hazard-123"))
// .andExpect(jsonPath("$.reporterId").value("user-456"))
// .andExpect(jsonPath("$.description").value("Pothole on the road"));

// verify(hazardService).create(any(HazardDTO.class));
// }

// @Test
// @DisplayName("should return 401 when not authenticated")
// void shouldReturn401WhenNotAuthenticated() throws Exception {
// mockMvc.perform(post("/api/v1/hazards")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(testHazardDTO)))
// .andExpect(status().isUnauthorized());

// verify(hazardService, never()).create(any());
// }
// }

// @Nested
// @DisplayName("Get Hazard by ID")
// class GetHazardById {

// @Test
// @WithMockUser(username = "testUser")
// @DisplayName("should return hazard when found")
// void shouldReturnHazardWhenFound() throws Exception {
// when(hazardService.findById("hazard-123")).thenReturn(Optional.of(testHazard));

// mockMvc.perform(get("/api/v1/hazards/hazard-123"))
// .andExpect(status().isOk())
// .andExpect(jsonPath("$.id").value("hazard-123"))
// .andExpect(jsonPath("$.description").value("Pothole on the road"));

// verify(hazardService).findById("hazard-123");
// }

// @Test
// @WithMockUser(username = "testUser")
// @DisplayName("should return 404 when hazard not found")
// void shouldReturn404WhenNotFound() throws Exception {
// when(hazardService.findById("non-existent")).thenReturn(Optional.empty());

// mockMvc.perform(get("/api/v1/hazards/non-existent"))
// .andExpect(status().isNotFound());

// verify(hazardService).findById("non-existent");
// }

// @Test
// @DisplayName("should return 401 when not authenticated")
// void shouldReturn401WhenNotAuthenticated() throws Exception {
// mockMvc.perform(get("/api/v1/hazards/hazard-123"))
// .andExpect(status().isUnauthorized());

// verify(hazardService, never()).findById(any());
// }
// }

// @Nested
// @DisplayName("Update Hazard")
// class UpdateHazard {

// @Test
// @WithMockUser(username = "testUser")
// @DisplayName("should update hazard and return 200 OK")
// void shouldUpdateHazard() throws Exception {
// Hazard updatedHazard = Hazard.builder()
// .id("hazard-123")
// .reporterId("user-456")
// .description("Updated description")
// .status(HazardStatus.INACTIVE)
// .build();

// when(hazardService.update(eq("hazard-123"),
// any(HazardDTO.class))).thenReturn(updatedHazard);

// HazardDTO updateDTO = HazardDTO.builder()
// .description("Updated description")
// .status(HazardStatus.INACTIVE)
// .build();

// mockMvc.perform(put("/api/v1/hazards/hazard-123")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(updateDTO)))
// .andExpect(status().isOk())
// .andExpect(jsonPath("$.id").value("hazard-123"))
// .andExpect(jsonPath("$.description").value("Updated description"));

// verify(hazardService).update(eq("hazard-123"), any(HazardDTO.class));
// }

// @Test
// @DisplayName("should return 401 when not authenticated")
// void shouldReturn401WhenNotAuthenticated() throws Exception {
// mockMvc.perform(put("/api/v1/hazards/hazard-123")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(testHazardDTO)))
// .andExpect(status().isUnauthorized());

// verify(hazardService, never()).update(any(), any());
// }
// }

// @Nested
// @DisplayName("Delete Hazard")
// class DeleteHazard {

// @Test
// @WithMockUser(username = "testUser")
// @DisplayName("should delete hazard and return 204 NO CONTENT")
// void shouldDeleteHazard() throws Exception {
// doNothing().when(hazardService).delete("hazard-123");

// mockMvc.perform(delete("/api/v1/hazards/hazard-123"))
// .andExpect(status().isNoContent());

// verify(hazardService).delete("hazard-123");
// }

// @Test
// @DisplayName("should return 401 when not authenticated")
// void shouldReturn401WhenNotAuthenticated() throws Exception {
// mockMvc.perform(delete("/api/v1/hazards/hazard-123"))
// .andExpect(status().isUnauthorized());

// verify(hazardService, never()).delete(any());
// }
// }
// }
