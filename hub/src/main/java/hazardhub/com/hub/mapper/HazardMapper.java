package hazardhub.com.hub.mapper;

import hazardhub.com.hub.model.dto.HazardDTO;
import hazardhub.com.hub.model.entity.Hazard;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;

public final class HazardMapper {

    public static Hazard toEntity(HazardDTO dto) {
        if (dto == null) {
            return null;
        }

        Hazard.HazardBuilder<?, ?> builder = Hazard.builder()
                .reporterId(dto.getReporterId())
                .expiresAt(dto.getExpiresAt())
                .locationAccuracyMeters(dto.getLocationAccuracyMeters())
                .address(dto.getAddress())
                .severity(dto.getSeverity())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .thumbnailUrl(dto.getThumbnailUrl())
                .status(dto.getStatus())
                .verificationCount(dto.getVerificationCount())
                .disputeCount(dto.getDisputeCount())
                .disabledAt(dto.getDisabledAt())
                .affectedRadiusMeters(dto.getAffectedRadiusMeters())
                .location(new GeoJsonPoint(dto.getLongitude(), dto.getLatitude()));

        return builder.build();
    }

    public static HazardDTO toDTO(Hazard entity) {
        if (entity == null) {
            return null;
        }

        HazardDTO.HazardDTOBuilder builder = HazardDTO.builder()
                .reporterId(entity.getReporterId())
                .expiresAt(entity.getExpiresAt())
                .locationAccuracyMeters(entity.getLocationAccuracyMeters())
                .address(entity.getAddress())
                .severity(entity.getSeverity())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .thumbnailUrl(entity.getThumbnailUrl())
                .status(entity.getStatus())
                .verificationCount(entity.getVerificationCount())
                .disputeCount(entity.getDisputeCount())
                .disabledAt(entity.getDisabledAt())
                .affectedRadiusMeters(entity.getAffectedRadiusMeters())
                .longitude(entity.getLocation().getX())
                .latitude(entity.getLocation().getY());

        return builder.build();
    }

    public static void updateEntityFromDTO(HazardDTO dto, Hazard entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setReporterId(dto.getReporterId());
        if (dto.getExpiresAt() != null) {
            entity.setExpiresAt(dto.getExpiresAt());
        }
        entity.setLocation(new GeoJsonPoint(dto.getLongitude(), dto.getLatitude()));
        if (dto.getLocationAccuracyMeters() != null) {
            entity.setLocationAccuracyMeters(dto.getLocationAccuracyMeters());
        }
        if (dto.getAddress() != null) {
            entity.setAddress(dto.getAddress());
        }
        entity.setSeverity(dto.getSeverity());
        entity.setDescription(dto.getDescription());
        if (dto.getImageUrl() != null) {
            entity.setImageUrl(dto.getImageUrl());
        }
        if (dto.getThumbnailUrl() != null) {
            entity.setThumbnailUrl(dto.getThumbnailUrl());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getVerificationCount() != null) {
            entity.setVerificationCount(dto.getVerificationCount());
        }
        if (dto.getDisputeCount() != null) {
            entity.setDisputeCount(dto.getDisputeCount());
        }
        if (dto.getDisabledAt() != null) {
            entity.setDisabledAt(dto.getDisabledAt());
        }
        if (dto.getAffectedRadiusMeters() != null) {
            entity.setAffectedRadiusMeters(dto.getAffectedRadiusMeters());
        }
    }
}
