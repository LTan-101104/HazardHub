package hazardhub.com.hub.mapper;

import hazardhub.com.hub.model.dto.RouteDTO;
import hazardhub.com.hub.model.entity.Route;

public final class RouteMapper {

    /*
     * Map RouteDTO to Entity, ignore id of dto so id will be automatically created
     * when saved
     */
    public static Route toEntity(RouteDTO dto) {
        if (dto == null) {
            return null;
        }

        return Route.builder()
                .tripId(dto.getTripId())
                .polyline(dto.getPolyline())
                .waypoints(dto.getWaypoints())
                .distanceMeters(dto.getDistanceMeters())
                .durationSeconds(dto.getDurationSeconds())
                .safetyScore(dto.getSafetyScore())
                .safetyAnalysis(dto.getSafetyAnalysis())
                .hazardsConsidered(dto.getHazardsConsidered())
                .isSelected(dto.getIsSelected() != null ? dto.getIsSelected() : false)
                .build();
    }

    /*
     * Map entity to DTO
     */
    public static RouteDTO toDTO(Route entity) {
        if (entity == null) {
            return null;
        }

        return RouteDTO.builder()
                .id(entity.getId())
                .tripId(entity.getTripId())
                .polyline(entity.getPolyline())
                .waypoints(entity.getWaypoints())
                .distanceMeters(entity.getDistanceMeters())
                .durationSeconds(entity.getDurationSeconds())
                .safetyScore(entity.getSafetyScore())
                .safetyAnalysis(entity.getSafetyAnalysis())
                .hazardsConsidered(entity.getHazardsConsidered())
                .isSelected(entity.getIsSelected())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /*
     * Update entity based on DTO, no override of entity's id is allowed
     */
    public static void updateEntityFromDTO(RouteDTO dto, Route entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getTripId() != null) {
            entity.setTripId(dto.getTripId());
        }
        if (dto.getPolyline() != null) {
            entity.setPolyline(dto.getPolyline());
        }
        if (dto.getWaypoints() != null) {
            entity.setWaypoints(dto.getWaypoints());
        }
        if (dto.getDistanceMeters() != null) {
            entity.setDistanceMeters(dto.getDistanceMeters());
        }
        if (dto.getDurationSeconds() != null) {
            entity.setDurationSeconds(dto.getDurationSeconds());
        }
        if (dto.getSafetyScore() != null) {
            entity.setSafetyScore(dto.getSafetyScore());
        }
        if (dto.getSafetyAnalysis() != null) {
            entity.setSafetyAnalysis(dto.getSafetyAnalysis());
        }
        if (dto.getHazardsConsidered() != null) {
            entity.setHazardsConsidered(dto.getHazardsConsidered());
        }
        if (dto.getIsSelected() != null) {
            entity.setIsSelected(dto.getIsSelected());
        }
    }
}
