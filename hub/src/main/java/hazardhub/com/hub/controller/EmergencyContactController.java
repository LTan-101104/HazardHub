package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.EmergencyContactDTO;
import hazardhub.com.hub.model.dto.UpdateEmergencyContactDTO;
import hazardhub.com.hub.service.EmergencyContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/emergency-contacts")
@RequiredArgsConstructor
@Tag(name = "Emergency Contacts", description = "Manage emergency contacts for authenticated users")
@Profile("!test")
public class EmergencyContactController {

    private final EmergencyContactService emergencyContactService;

    @GetMapping
    @Operation(summary = "List emergency contacts for current user")
    public ResponseEntity<List<EmergencyContactDTO>> listContacts(@AuthenticationPrincipal String uid) {
        List<EmergencyContactDTO> contacts = emergencyContactService.getContactsByUserId(uid);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{contactId}")
    @Operation(summary = "Get one emergency contact for current user")
    public ResponseEntity<EmergencyContactDTO> getContact(
            @AuthenticationPrincipal String uid,
            @PathVariable String contactId) {
        EmergencyContactDTO contact = emergencyContactService.getContactById(uid, contactId);
        return ResponseEntity.ok(contact);
    }

    @PostMapping
    @Operation(summary = "Create emergency contact for current user")
    public ResponseEntity<EmergencyContactDTO> createContact(
            @AuthenticationPrincipal String uid,
            @Valid @RequestBody EmergencyContactDTO request) {
        EmergencyContactDTO created = emergencyContactService.createEmergencyContact(uid, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{contactId}")
    @Operation(summary = "Update emergency contact for current user")
    public ResponseEntity<EmergencyContactDTO> updateContact(
            @AuthenticationPrincipal String uid,
            @PathVariable String contactId,
            @Valid @RequestBody UpdateEmergencyContactDTO updates) {
        EmergencyContactDTO updated = emergencyContactService.updateEmergencyContact(uid, contactId, updates);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{contactId}")
    @Operation(summary = "Delete emergency contact for current user")
    public ResponseEntity<Void> deleteContact(
            @AuthenticationPrincipal String uid,
            @PathVariable String contactId) {
        emergencyContactService.deleteEmergencyContact(uid, contactId);
        return ResponseEntity.noContent().build();
    }
}
