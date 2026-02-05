package hazardhub.com.hub.mapper;

import hazardhub.com.hub.model.dto.TripDTO;
import hazardhub.com.hub.model.entity.Trip;
import hazardhub.com.hub.model.enums.TripStatus;

public final class TripMapper {

    public static Trip toEntity(TripDTO dto) {
        if (dto == null) return null;
        Trip.TripBuilder<?, ?> b = Trip.builder()
                .userId(dto.getUserId())
                .startedAt(dto.getStartedAt())
                .completedAt(dto.getCompletedAt())
                .originLatitude(dto.getOriginLatitude())
                .originLongitude(dto.getOriginLongitude())
                .originAddress(dto.getOriginAddress())
                .destinationLatitude(dto.getDestinationLatitude())
                .destinationLongitude(dto.getDestinationLongitude())
                .destinationAddress(dto.getDestinationAddress())
                .selectedRouteId(dto.getSelectedRouteId())
                .status(dto.getStatus());
        return b.build();
    }

    public static TripDTO toDTO(Trip entity) {
        if (entity == null) return null;
        return TripDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .createdAt(entity.getCreatedAt())
                .startedAt(entity.getStartedAt())
                .completedAt(entity.getCompletedAt())
                .originLatitude(entity.getOriginLatitude())
                .originLongitude(entity.getOriginLongitude())
                .originAddress(entity.getOriginAddress())
                .destinationLatitude(entity.getDestinationLatitude())
                .destinationLongitude(entity.getDestinationLongitude())
                .destinationAddress(entity.getDestinationAddress())
                .selectedRouteId(entity.getSelectedRouteId())
                .status(entity.getStatus())
                .build();
    }

    public static void updateEntityFromDTO(TripDTO dto, Trip entity) {
        if (dto == null || entity == null) return;
        if (dto.getUserId() != null) entity.setUserId(dto.getUserId());
        if (dto.getStartedAt() != null) entity.setStartedAt(dto.getStartedAt());
        if (dto.getCompletedAt() != null) entity.setCompletedAt(dto.getCompletedAt());
        if (dto.getOriginLatitude() != null) entity.setOriginLatitude(dto.getOriginLatitude());
        if (dto.getOriginLongitude() != null) entity.setOriginLongitude(dto.getOriginLongitude());
        if (dto.getOriginAddress() != null) entity.setOriginAddress(dto.getOriginAddress());
        if (dto.getDestinationLatitude() != null) entity.setDestinationLatitude(dto.getDestinationLatitude());
        if (dto.getDestinationLongitude() != null) entity.setDestinationLongitude(dto.getDestinationLongitude());
        if (dto.getDestinationAddress() != null) entity.setDestinationAddress(dto.getDestinationAddress());
        if (dto.getSelectedRouteId() != null) entity.setSelectedRouteId(dto.getSelectedRouteId());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
    }
}
