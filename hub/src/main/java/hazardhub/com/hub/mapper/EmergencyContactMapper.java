package hazardhub.com.hub.mapper;

import hazardhub.com.hub.model.dto.EmergencyContactDTO;
import hazardhub.com.hub.model.entity.EmergencyContact;

import java.util.UUID;

public final class EmergencyContactMapper {

    private EmergencyContactMapper() {
    }

    public static EmergencyContact toEntity(EmergencyContactDTO dto, UUID userId) {
        if (dto == null) {
            return null;
        }

        return EmergencyContact.builder()
                .userId(userId)
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .relationship(dto.getRelationship())
                .priority(dto.getPriority() == null ? 1 : dto.getPriority())
                .build();
    }

    public static EmergencyContactDTO toDTO(EmergencyContact entity) {
        if (entity == null) {
            return null;
        }

        return EmergencyContactDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .relationship(entity.getRelationship())
                .priority(entity.getPriority())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static void updateEntityFromDTO(EmergencyContactDTO dto, EmergencyContact entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getPhone() != null) {
            entity.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getRelationship() != null) {
            entity.setRelationship(dto.getRelationship());
        }
        if (dto.getPriority() != null) {
            entity.setPriority(dto.getPriority());
        }
    }
}
