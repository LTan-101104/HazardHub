package hazardhub.com.hub.mapper;

import hazardhub.com.hub.model.dto.SOSEventDTO;
import hazardhub.com.hub.model.entity.SOSEvent;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

public final class SOSEventMapper {

    /**
     * Map SOSEventDTO to Entity, ignore id of dto so id from entity will be automatically created when saved
     */
    public static SOSEvent toEntity(SOSEventDTO dto) {
        if (dto == null) {
            return null;
        }

        SOSEvent.SOSEventBuilder<?, ?> builder = SOSEvent.builder()
                .userId(dto.getUserId())
                .tripId(dto.getTripId())
                .triggeredAt(dto.getTriggeredAt())
                .resolvedAt(dto.getResolvedAt())
                .locationAccuracyMeters(dto.getLocationAccuracyMeters())
                .status(dto.getStatus())
                .dispatchNotified(dto.getDispatchNotified())
                .dispatchReference(dto.getDispatchReference())
                .location(new GeoJsonPoint(dto.getLongitude(), dto.getLatitude()));

        return builder.build();
    }

    /**
     * Map entity to DTO
     */
    public static SOSEventDTO toDTO(SOSEvent entity) {
        if (entity == null) {
            return null;
        }

        SOSEventDTO.SOSEventDTOBuilder builder = SOSEventDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .tripId(entity.getTripId())
                .triggeredAt(entity.getTriggeredAt())
                .resolvedAt(entity.getResolvedAt())
                .locationAccuracyMeters(entity.getLocationAccuracyMeters())
                .status(entity.getStatus())
                .dispatchNotified(entity.getDispatchNotified())
                .dispatchReference(entity.getDispatchReference())
                .longitude(entity.getLocation().getX())
                .latitude(entity.getLocation().getY());

        return builder.build();
    }

    /**
     * Update entity based on dto, no override of entity's id is allowed
     */
    public static void updateEntityFromDTO(SOSEventDTO dto, SOSEvent entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getUserId() != null) {
            entity.setUserId(dto.getUserId());
        }
        if (dto.getTripId() != null) {
            entity.setTripId(dto.getTripId());
        }
        if (dto.getTriggeredAt() != null) {
            entity.setTriggeredAt(dto.getTriggeredAt());
        }
        if (dto.getResolvedAt() != null) {
            entity.setResolvedAt(dto.getResolvedAt());
        }
        if (dto.getLongitude() != null && dto.getLatitude() != null) {
            entity.setLocation(new GeoJsonPoint(dto.getLongitude(), dto.getLatitude()));
        }
        if (dto.getLocationAccuracyMeters() != null) {
            entity.setLocationAccuracyMeters(dto.getLocationAccuracyMeters());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getDispatchNotified() != null) {
            entity.setDispatchNotified(dto.getDispatchNotified());
        }
        if (dto.getDispatchReference() != null) {
            entity.setDispatchReference(dto.getDispatchReference());
        }
    }
}