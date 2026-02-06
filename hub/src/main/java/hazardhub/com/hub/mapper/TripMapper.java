package hazardhub.com.hub.mapper;

import hazardhub.com.hub.model.dto.TripDTO;
import hazardhub.com.hub.model.entity.Trip;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

public final class TripMapper {

    /*
     * Map TripDTO to Entity, ignore id of dto so id from trip will be automatically created when saved
     */
    public static Trip toEntity(TripDTO dto) {
        if (dto == null) {
            return null;
        }

        Trip.TripBuilder<?, ?> builder = Trip.builder()
                .userId(dto.getUserId())
                .startedAt(dto.getStartedAt())
                .completedAt(dto.getCompletedAt())
                .origin(new GeoJsonPoint(dto.getOriginLongitude(), dto.getOriginLatitude()))
                .originAddress(dto.getOriginAddress())
                .destination(new GeoJsonPoint(dto.getDestinationLongitude(), dto.getDestinationLatitude()))
                .destinationAddress(dto.getDestinationAddress())
                .selectedRouteId(dto.getSelectedRouteId())
                .status(dto.getStatus());

        return builder.build();
    }

    /*
     * Map entity to dto
     */
    public static TripDTO toDTO(Trip entity) {
        if (entity == null) {
            return null;
        }

        TripDTO.TripDTOBuilder builder = TripDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .createdAt(entity.getCreatedAt())
                .startedAt(entity.getStartedAt())
                .completedAt(entity.getCompletedAt())
                .originAddress(entity.getOriginAddress())
                .destinationAddress(entity.getDestinationAddress())
                .selectedRouteId(entity.getSelectedRouteId())
                .status(entity.getStatus());

        if (entity.getOrigin() != null) {
            builder.originLongitude(entity.getOrigin().getX())
                    .originLatitude(entity.getOrigin().getY());
        }

        if (entity.getDestination() != null) {
            builder.destinationLongitude(entity.getDestination().getX())
                    .destinationLatitude(entity.getDestination().getY());
        }

        return builder.build();
    }

    /*
     * Update entity based on dto, no override of entity's id and foreign key is allowed
     */
    public static void updateEntityFromDTO(TripDTO dto, Trip entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getStartedAt() != null) {
            entity.setStartedAt(dto.getStartedAt());
        }
        if (dto.getCompletedAt() != null) {
            entity.setCompletedAt(dto.getCompletedAt());
        }
        if (dto.getOriginLongitude() != null && dto.getOriginLatitude() != null) {
            entity.setOrigin(new GeoJsonPoint(dto.getOriginLongitude(), dto.getOriginLatitude()));
        }
        if (dto.getOriginAddress() != null) {
            entity.setOriginAddress(dto.getOriginAddress());
        }
        if (dto.getDestinationLongitude() != null && dto.getDestinationLatitude() != null) {
            entity.setDestination(new GeoJsonPoint(dto.getDestinationLongitude(), dto.getDestinationLatitude()));
        }
        if (dto.getDestinationAddress() != null) {
            entity.setDestinationAddress(dto.getDestinationAddress());
        }
        if (dto.getSelectedRouteId() != null) {
            entity.setSelectedRouteId(dto.getSelectedRouteId());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
    }
}
