package hazardhub.com.hub.mapper;

import hazardhub.com.hub.model.dto.HazardVerificationDTO;
import hazardhub.com.hub.model.entity.HazardVerification;

public final class HazardVerificationMapper {

    private HazardVerificationMapper() {}

    /*
     * Map HazardVerificationDTO to Entity, ignore id of dto so id will be automatically created when saved
     */
    public static HazardVerification toEntity(HazardVerificationDTO dto) {
        if (dto == null) {
            return null;
        }

        return HazardVerification.builder()
                .hazardId(dto.getHazardId())
                .userId(dto.getUserId())
                .verificationType(dto.getVerificationType())
                .comment(dto.getComment())
                .imageUrl(dto.getImageUrl())
                .build();
    }

    /*
     * Map entity to dto
     */
    public static HazardVerificationDTO toDTO(HazardVerification entity) {
        if (entity == null) {
            return null;
        }

        return HazardVerificationDTO.builder()
                .id(entity.getId())
                .hazardId(entity.getHazardId())
                .userId(entity.getUserId())
                .verificationType(entity.getVerificationType())
                .comment(entity.getComment())
                .imageUrl(entity.getImageUrl())
                .build();
    }

    /*
     * Update entity based on dto, no override of entity's id and foreign key is allowed
     */
    public static void updateEntityFromDTO(HazardVerificationDTO dto, HazardVerification entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getVerificationType() != null) {
            entity.setVerificationType(dto.getVerificationType());
        }
        if (dto.getComment() != null) {
            entity.setComment(dto.getComment());
        }
        if (dto.getImageUrl() != null) {
            entity.setImageUrl(dto.getImageUrl());
        }
    }
}
