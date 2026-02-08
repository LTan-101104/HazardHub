package hazardhub.com.hub.model.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestedRouteDTO {

    private String name;

    private String routeType; // "safest", "fastest", "balanced"

    @Positive(message = "Distance must be positive")
    private Double distanceMeters;

    @Positive(message = "Duration must be positive")
    private Integer durationSeconds;

    @DecimalMin(value = "0.0", inclusive = true, message = "Safety score must be >= 0.0")
    @DecimalMax(value = "1.0", inclusive = true, message = "Safety score must be <= 1.0")
    private Double safetyScore;

    private String description;

    private String polyline; // encoded polyline from Google Directions API

    private List<String> tags;

    @Builder.Default
    private Boolean recommended = false;
}
