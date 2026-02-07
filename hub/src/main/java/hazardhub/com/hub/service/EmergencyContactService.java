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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class EmergencyContactService {

    private final EmergencyContactRepository emergencyContactRepository;

    public EmergencyContactDTO createEmergencyContact(String uid, EmergencyContactDTO request) {
        String userId = validateUserId(uid);
        EmergencyContact contact = EmergencyContactMapper.toEntity(request, userId);
        EmergencyContact saved = emergencyContactRepository.save(contact);
        log.info("Created emergency contact {} for user {}", saved.getId(), uid);
        return EmergencyContactMapper.toDTO(saved);
    }

    public List<EmergencyContactDTO> getContactsByUserId(String uid) {
        String userId = validateUserId(uid);
        return emergencyContactRepository.findByUserIdOrderByPriorityAsc(userId).stream()
                .map(EmergencyContactMapper::toDTO)
                .toList();
    }

    public EmergencyContactDTO getContactById(String uid, String contactId) {
        EmergencyContact contact = getOwnedContact(uid, contactId);
        return EmergencyContactMapper.toDTO(contact);
    }

    public EmergencyContactDTO updateEmergencyContact(String uid, String contactId, UpdateEmergencyContactDTO updates) {
        EmergencyContact contact = getOwnedContact(uid, contactId);
        EmergencyContactMapper.updateEntityFromDTO(updates, contact);

        EmergencyContact saved = emergencyContactRepository.save(contact);
        log.info("Updated emergency contact {} for user {}", contactId, uid);
        return EmergencyContactMapper.toDTO(saved);
    }

    public void deleteEmergencyContact(String uid, String contactId) {
        EmergencyContact contact = getOwnedContact(uid, contactId);
        emergencyContactRepository.delete(contact);
        log.info("Deleted emergency contact {} for user {}", contactId, uid);
    }

    private EmergencyContact getOwnedContact(String uid, String contactId) {
        String userId = validateUserId(uid);
        return emergencyContactRepository.findByIdAndUserId(contactId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Emergency contact not found or access denied"));
    }

    private String validateUserId(String uid) {
        if (uid == null || uid.isBlank()) {
            throw new BadRequestException("Authenticated user id is missing");
        }
        return uid;
    }
}
