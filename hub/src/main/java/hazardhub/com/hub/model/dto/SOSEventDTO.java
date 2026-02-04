package hazardhub.com.hub.model.dto;

import hazardhub.com.hub.model.enums.SOSEventStatus;
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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SOSEventDTO {

    private String id;

    @NotBlank(message = "User ID is required")
    private String userId;

    private String tripId;

    private Instant triggeredAt;

    private Instant resolvedAt;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double longitude;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double latitude;

    @NotNull(message = "Location accuracy must not be null")
    @Positive(message = "Location accuracy must be positive")
    private Double locationAccuracyMeters;

    private SOSEventStatus status;

    private Boolean dispatchNotified;

    private String dispatchReference;
}