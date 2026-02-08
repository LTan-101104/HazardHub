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
public class SuggestedRouteDTO {

    private String name;

    private String recommendationTier; // "RECOMMENDED", "ALTERNATIVE", "RISKY"

    @DecimalMin(value = "0.0", inclusive = true, message = "Safety score must be >= 0.0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Safety score must be <= 100.0")
    private Double safetyScore;

    @DecimalMin(value = "0.0", inclusive = true, message = "Efficiency score must be >= 0.0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Efficiency score must be <= 100.0")
    private Double efficiencyScore;

    @DecimalMin(value = "0.0", inclusive = true, message = "Rank score must be >= 0.0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Rank score must be <= 100.0")
    private Double rankScore;

    private String aiSummary;

    private Integer hazardCount;

    // --- Fields populated by backend from Google Directions API ---

    @Positive(message = "Distance must be positive")
    private Double distanceMeters;

    @Positive(message = "Duration must be positive")
    private Integer durationSeconds;

    private String polyline; // encoded polyline from Google Directions API

    // --- Directions API params (from Gemini, used by backend to call Directions
    // API) ---

    private DirectionsParamsDTO directionsParams;
}
