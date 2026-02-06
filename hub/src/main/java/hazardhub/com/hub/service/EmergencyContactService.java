package hazardhub.com.hub.service;

import hazardhub.com.hub.exception.BadRequestException;
import hazardhub.com.hub.exception.ResourceNotFoundException;
import hazardhub.com.hub.mapper.EmergencyContactMapper;
import hazardhub.com.hub.model.dto.EmergencyContactDTO;
import hazardhub.com.hub.model.dto.UpdateEmergencyContactDTO;
import hazardhub.com.hub.model.entity.EmergencyContact;
import hazardhub.com.hub.repository.EmergencyContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class EmergencyContactService {

    private static final String USER_ID_NAMESPACE = "firebase-user:";
    private final EmergencyContactRepository emergencyContactRepository;

    public EmergencyContactDTO createEmergencyContact(String uid, EmergencyContactDTO request) {
        UUID userId = toUserUuid(uid);
        EmergencyContact contact = EmergencyContactMapper.toEntity(request, userId);
        EmergencyContact saved = emergencyContactRepository.save(contact);
        log.info("Created emergency contact {} for user {}", saved.getId(), uid);
        return EmergencyContactMapper.toDTO(saved);
    }

    public List<EmergencyContactDTO> getContactsByUserId(String uid) {
        UUID userId = toUserUuid(uid);
        return emergencyContactRepository.findByUserIdOrderByPriorityAsc(userId).stream()
                .map(EmergencyContactMapper::toDTO)
                .toList();
    }

    public EmergencyContactDTO getContactById(String uid, UUID contactId) {
        EmergencyContact contact = getOwnedContact(uid, contactId);
        return EmergencyContactMapper.toDTO(contact);
    }

    public EmergencyContactDTO updateEmergencyContact(String uid, UUID contactId, UpdateEmergencyContactDTO updates) {
        EmergencyContact contact = getOwnedContact(uid, contactId);
        EmergencyContactMapper.updateEntityFromDTO(updates, contact);

        EmergencyContact saved = emergencyContactRepository.save(contact);
        log.info("Updated emergency contact {} for user {}", contactId, uid);
        return EmergencyContactMapper.toDTO(saved);
    }

    public void deleteEmergencyContact(String uid, UUID contactId) {
        EmergencyContact contact = getOwnedContact(uid, contactId);
        emergencyContactRepository.delete(contact);
        log.info("Deleted emergency contact {} for user {}", contactId, uid);
    }

    private EmergencyContact getOwnedContact(String uid, UUID contactId) {
        UUID userId = toUserUuid(uid);
        return emergencyContactRepository.findByIdAndUserId(contactId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Emergency contact not found with id: " + contactId));
    }

    private UUID toUserUuid(String uid) {
        if (uid == null || uid.isBlank()) {
            throw new BadRequestException("Authenticated user id is missing");
        }
        return UUID.nameUUIDFromBytes((USER_ID_NAMESPACE + uid).getBytes(StandardCharsets.UTF_8));
    }
}
