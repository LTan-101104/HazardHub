package hazardhub.com.hub.model.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {

    private String id;

    @NotBlank(message = "Trip ID is required")
    private String tripId;

    @NotBlank(message = "Polyline is required")
    private String polyline;

    @NotNull(message = "Waypoints are required")
    private Map<String, Object> waypoints;

    @NotNull(message = "Distance is required")
    @Positive(message = "Distance must be positive")
    private Integer distanceMeters;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer durationSeconds;

    @NotNull(message = "Safety score is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Safety score must be >= 0.0")
    @DecimalMax(value = "1.0", inclusive = true, message = "Safety score must be <= 1.0")
    private Double safetyScore;

    private Map<String, Object> safetyAnalysis;

    private List<String> hazardsConsidered;

    @Builder.Default
    private Boolean isSelected = false;

    private Instant createdAt;
    private Instant updatedAt;
}
