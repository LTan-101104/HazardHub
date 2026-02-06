package hazardhub.com.hub.mapper;

import hazardhub.com.hub.model.dto.SavedLocationDTO;
import hazardhub.com.hub.model.entity.SavedLocation;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

public final class SavedLocationMapper {

    private SavedLocationMapper() {}

    public static SavedLocation toEntity(SavedLocationDTO dto) {
        if (dto == null) {
            return null;
        }

        return SavedLocation.builder()
                .userId(dto.getUserId())
                .name(dto.getName())
                .location(new GeoJsonPoint(dto.getLongitude(), dto.getLatitude()))
                .address(dto.getAddress())
                .build();
    }

    public static SavedLocationDTO toDTO(SavedLocation entity) {
        if (entity == null) {
            return null;
        }

        return SavedLocationDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .name(entity.getName())
                .longitude(entity.getLocation().getX())
                .latitude(entity.getLocation().getY())
                .address(entity.getAddress())
                .build();
    }

    public static void updateEntityFromDTO(SavedLocationDTO dto, SavedLocation entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getLongitude() != null && dto.getLatitude() != null) {
            entity.setLocation(new GeoJsonPoint(dto.getLongitude(), dto.getLatitude()));
        }
        if (dto.getAddress() != null) {
            entity.setAddress(dto.getAddress());
        }
    }
}
