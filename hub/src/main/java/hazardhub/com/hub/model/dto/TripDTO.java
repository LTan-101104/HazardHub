package hazardhub.com.hub.model.dto;

import hazardhub.com.hub.model.enums.TripStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDTO {

    private String id;

    @NotBlank(message = "User ID is required")
    private String userId;

    private Instant createdAt;

    private Instant startedAt;

    private Instant completedAt;

    @NotNull(message = "Origin longitude is required")
    @DecimalMin(value = "-180.0", message = "Origin longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Origin longitude must be <= 180")
    private Double originLongitude;

    @NotNull(message = "Origin latitude is required")
    @DecimalMin(value = "-90.0", message = "Origin latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Origin latitude must be <= 90")
    private Double originLatitude;

    private String originAddress;

    @NotNull(message = "Destination longitude is required")
    @DecimalMin(value = "-180.0", message = "Destination longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Destination longitude must be <= 180")
    private Double destinationLongitude;

    @NotNull(message = "Destination latitude is required")
    @DecimalMin(value = "-90.0", message = "Destination latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Destination latitude must be <= 90")
    private Double destinationLatitude;

    private String destinationAddress;

    private String selectedRouteId;

    private TripStatus status;
}
