package hazardhub.com.hub.model.dto;

import hazardhub.com.hub.model.enums.TripStatus;
import jakarta.validation.constraints.*;

import java.time.Instant;

import lombok.*;

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

    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double originLatitude;

    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double originLongitude;

    private String originAddress;

    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double destinationLatitude;

    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double destinationLongitude;

    private String destinationAddress;

    private String selectedRouteId;

    private TripStatus status;
}
