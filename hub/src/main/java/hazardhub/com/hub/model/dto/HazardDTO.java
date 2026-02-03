package hazardhub.com.hub.model.dto;

import hazardhub.com.hub.model.enums.HazardSeverity;
import hazardhub.com.hub.model.enums.HazardStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HazardDTO {

    private String id;

    @NotBlank(message = "Reporter ID is required")
    private String reporterId;

    private Instant expiresAt;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double longitude;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double latitude;

    @NotNull(message = "Location accuracy is required")
    @Positive(message = "Location accuracy must be positive")
    private Double locationAccuracyMeters;

    private String address;

    @NotNull(message = "Severity is required")
    private HazardSeverity severity;

    @NotBlank(message = "Description is required")
    private String description;

    private String imageUrl;

    private String thumbnailUrl;

    private HazardStatus status;

    @PositiveOrZero(message = "Verification count cannot be negative")
    private Integer verificationCount;

    @PositiveOrZero(message = "Dispute count cannot be negative")
    private Integer disputeCount;

    private Instant disabledAt;

    @NotNull(message = "Affected radius is required")
    @Positive(message = "Affected radius must be positive")
    private Double affectedRadiusMeters;
}
