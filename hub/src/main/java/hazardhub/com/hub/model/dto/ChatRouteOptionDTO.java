package hazardhub.com.hub.model.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRouteOptionDTO {

    private String name;

    private String recommendationTier;

    @DecimalMin(value = "0.0", inclusive = true, message = "Safety score must be >= 0.0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Safety score must be <= 100.0")
    private Double safetyScore;

    private Integer hazardCount;

    private String summary;

    @Positive(message = "Distance must be positive")
    private Double distanceMeters;

    @Positive(message = "Duration must be positive")
    private Integer durationSeconds;

    private String polyline;
}
