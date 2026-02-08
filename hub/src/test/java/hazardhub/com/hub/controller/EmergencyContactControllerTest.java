package hazardhub.com.hub.controller;

import hazardhub.com.hub.exception.GlobalExceptionHandler;
import hazardhub.com.hub.exception.ResourceNotFoundException;
import hazardhub.com.hub.model.dto.EmergencyContactDTO;
import hazardhub.com.hub.model.dto.UpdateEmergencyContactDTO;
import hazardhub.com.hub.service.EmergencyContactService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmergencyContactController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        GlobalExceptionHandler.class,
        EmergencyContactControllerTest.AuthenticationPrincipalTestConfig.class
})
class EmergencyContactControllerTest {

    private static final String BASE_URL = "/api/v1/emergency-contacts";
    private static final String UID = "user-123";

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @MockitoBean
    private EmergencyContactService emergencyContactService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void listContacts_ReturnsContactsForAuthenticatedUser() throws Exception {
        EmergencyContactDTO first = EmergencyContactDTO.builder()
                .id("contact-1")
                .userId(UID)
                .name("Alice")
                .phone("0123456789")
                .email("alice@example.com")
                .relationship("Sister")
                .priority(1)
                .build();

        EmergencyContactDTO second = EmergencyContactDTO.builder()
                .id("contact-2")
                .userId(UID)
                .name("Bob")
                .phone("0987654321")
                .relationship("Friend")
                .priority(2)
                .build();

        when(emergencyContactService.getContactsByUserId(UID)).thenReturn(List.of(first, second));

        mockMvc.perform(get(BASE_URL).with(authenticatedUid(UID)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("contact-1"))
                .andExpect(jsonPath("$[1].id").value("contact-2"));

        verify(emergencyContactService).getContactsByUserId(UID);
    }

    @Test
    void getContact_WhenOwned_ReturnsContact() throws Exception {
        EmergencyContactDTO contact = EmergencyContactDTO.builder()
                .id("contact-1")
                .userId(UID)
                .name("Alice")
                .phone("0123456789")
                .email("alice@example.com")
                .relationship("Sister")
                .priority(1)
                .build();

        when(emergencyContactService.getContactById(UID, "contact-1")).thenReturn(contact);

        mockMvc.perform(get(BASE_URL + "/contact-1").with(authenticatedUid(UID)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("contact-1"))
                .andExpect(jsonPath("$.name").value("Alice"));

        verify(emergencyContactService).getContactById(UID, "contact-1");
    }

    @Test
    void getContact_WhenNotOwned_ReturnsNotFound() throws Exception {
        when(emergencyContactService.getContactById(UID, "contact-404"))
                .thenThrow(new ResourceNotFoundException("Emergency contact not found or access denied"));

        mockMvc.perform(get(BASE_URL + "/contact-404").with(authenticatedUid(UID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Emergency contact not found or access denied"));
    }

    @Test
    void createContact_WithValidRequest_ReturnsCreated() throws Exception {
        EmergencyContactDTO request = EmergencyContactDTO.builder()
                .name("Alice")
                .phone("0123456789")
                .email("alice@example.com")
                .relationship("Sister")
                .priority(1)
                .build();

        EmergencyContactDTO created = EmergencyContactDTO.builder()
                .id("contact-1")
                .userId(UID)
                .name("Alice")
                .phone("0123456789")
                .email("alice@example.com")
                .relationship("Sister")
                .priority(1)
                .build();

        when(emergencyContactService.createEmergencyContact(eq(UID), any(EmergencyContactDTO.class)))
                .thenReturn(created);

        mockMvc.perform(post(BASE_URL)
                        .with(authenticatedUid(UID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("contact-1"))
                .andExpect(jsonPath("$.userId").value(UID))
                .andExpect(jsonPath("$.priority").value(1));

        verify(emergencyContactService).createEmergencyContact(eq(UID), any(EmergencyContactDTO.class));
    }

    @Test
    void createContact_WithInvalidRequest_ReturnsBadRequest() throws Exception {
        EmergencyContactDTO request = EmergencyContactDTO.builder()
                .name("")
                .phone("0123456789")
                .priority(1)
                .build();

        mockMvc.perform(post(BASE_URL)
                        .with(authenticatedUid(UID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Error"))
                .andExpect(jsonPath("$.error.name").value("Name is required"));

        verify(emergencyContactService, never()).createEmergencyContact(eq(UID), any(EmergencyContactDTO.class));
    }

    @Test
    void updateContact_WithValidRequest_ReturnsUpdatedContact() throws Exception {
        UpdateEmergencyContactDTO updates = UpdateEmergencyContactDTO.builder()
                .name("Alice Updated")
                .phone("0999999999")
                .email("updated@example.com")
                .relationship("Mother")
                .priority(2)
                .build();

        EmergencyContactDTO updated = EmergencyContactDTO.builder()
                .id("contact-1")
                .userId(UID)
                .name("Alice Updated")
                .phone("0999999999")
                .email("updated@example.com")
                .relationship("Mother")
                .priority(2)
                .build();

        when(emergencyContactService.updateEmergencyContact(eq(UID), eq("contact-1"), any(UpdateEmergencyContactDTO.class)))
                .thenReturn(updated);

        mockMvc.perform(put(BASE_URL + "/contact-1")
                        .with(authenticatedUid(UID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("contact-1"))
                .andExpect(jsonPath("$.priority").value(2));

        verify(emergencyContactService).updateEmergencyContact(eq(UID), eq("contact-1"), any(UpdateEmergencyContactDTO.class));
    }

    @Test
    void updateContact_WithInvalidRequest_ReturnsBadRequest() throws Exception {
        UpdateEmergencyContactDTO updates = UpdateEmergencyContactDTO.builder()
                .name("Alice")
                .phone("0123456789")
                .priority(0)
                .build();

        mockMvc.perform(put(BASE_URL + "/contact-1")
                        .with(authenticatedUid(UID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Error"))
                .andExpect(jsonPath("$.error.priority").value("Priority must be at least 1"));

        verify(emergencyContactService, never()).updateEmergencyContact(eq(UID), eq("contact-1"), any(UpdateEmergencyContactDTO.class));
    }

    @Test
    void deleteContact_WhenOwned_ReturnsNoContent() throws Exception {
        doNothing().when(emergencyContactService).deleteEmergencyContact(UID, "contact-1");

        mockMvc.perform(delete(BASE_URL + "/contact-1")
                        .with(authenticatedUid(UID)))
                .andExpect(status().isNoContent());

        verify(emergencyContactService).deleteEmergencyContact(UID, "contact-1");
    }

    @Test
    void deleteContact_WhenNotOwned_ReturnsNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Emergency contact not found or access denied"))
                .when(emergencyContactService)
                .deleteEmergencyContact(UID, "contact-404");

        mockMvc.perform(delete(BASE_URL + "/contact-404")
                        .with(authenticatedUid(UID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Emergency contact not found or access denied"));

        verify(emergencyContactService).deleteEmergencyContact(UID, "contact-404");
    }

    @Test
    void updateContact_WhenNotOwned_ReturnsNotFound() throws Exception {
        UpdateEmergencyContactDTO updates = UpdateEmergencyContactDTO.builder()
                .name("Alice")
                .phone("0123456789")
                .priority(1)
                .build();

        when(emergencyContactService.updateEmergencyContact(eq(UID), eq("contact-404"), any(UpdateEmergencyContactDTO.class)))
                .thenThrow(new ResourceNotFoundException("Emergency contact not found or access denied"));

        mockMvc.perform(put(BASE_URL + "/contact-404")
                        .with(authenticatedUid(UID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Emergency contact not found or access denied"));

        verify(emergencyContactService).updateEmergencyContact(eq(UID), eq("contact-404"), any(UpdateEmergencyContactDTO.class));
    }

    private RequestPostProcessor authenticatedUid(String uid) {
        return request -> {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    uid,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            request.setUserPrincipal(auth);
            request.getSession(true).setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    context);
            return request;
        };
    }

    @TestConfiguration
    static class AuthenticationPrincipalTestConfig implements WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new AuthenticationPrincipalArgumentResolver());
        }
    }
}
