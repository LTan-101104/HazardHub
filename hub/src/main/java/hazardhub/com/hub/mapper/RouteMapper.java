package hazardhub.com.hub.mapper;

import hazardhub.com.hub.model.dto.RouteDTO;
import hazardhub.com.hub.model.entity.Route;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RouteMapper {

    public Route toEntity(RouteDTO dto) {
        if (dto == null) return null;

        return Route.builder()
                .id(dto.getId())
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

    public RouteDTO toDTO(Route entity) {
        if (entity == null) return null;

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

    public void updateEntity(RouteDTO dto, Route entity) {
        if (dto == null || entity == null) return;

        if (dto.getTripId() != null) entity.setTripId(dto.getTripId());
        if (dto.getPolyline() != null) entity.setPolyline(dto.getPolyline());
        if (dto.getWaypoints() != null) entity.setWaypoints(dto.getWaypoints());
        if (dto.getDistanceMeters() != null) entity.setDistanceMeters(dto.getDistanceMeters());
        if (dto.getDurationSeconds() != null) entity.setDurationSeconds(dto.getDurationSeconds());
        if (dto.getSafetyScore() != null) entity.setSafetyScore(dto.getSafetyScore());
        if (dto.getSafetyAnalysis() != null) entity.setSafetyAnalysis(dto.getSafetyAnalysis());
        if (dto.getHazardsConsidered() != null) entity.setHazardsConsidered(dto.getHazardsConsidered());
        if (dto.getIsSelected() != null) entity.setIsSelected(dto.getIsSelected());
    }
}
