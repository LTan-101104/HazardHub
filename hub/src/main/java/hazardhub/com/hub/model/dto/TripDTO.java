package hazardhub.com.hub.model.dto;

import hazardhub.com.hub.model.enums.TripStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDTO {

    private UUID id;

    @NotNull(message = "User ID is required")
    private UUID userId;

    private Instant createdAt;
    private Instant startedAt;
    private Instant completedAt;

    @NotNull(message = "Origin latitude is required")
    @Digits(integer = 2, fraction = 8, message = "Origin latitude must fit DECIMAL(10,8)")
    @DecimalMin(value = "-90.0", message = "Origin latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Origin latitude must be <= 90")
    private BigDecimal originLatitude;

    @NotNull(message = "Origin longitude is required")
    @Digits(integer = 3, fraction = 8, message = "Origin longitude must fit DECIMAL(11,8)")
    @DecimalMin(value = "-180.0", message = "Origin longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Origin longitude must be <= 180")
    private BigDecimal originLongitude;

    private String originAddress;

    @NotNull(message = "Destination latitude is required")
    @Digits(integer = 2, fraction = 8, message = "Destination latitude must fit DECIMAL(10,8)")
    @DecimalMin(value = "-90.0", message = "Destination latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Destination latitude must be <= 90")
    private BigDecimal destinationLatitude;

    @NotNull(message = "Destination longitude is required")
    @Digits(integer = 3, fraction = 8, message = "Destination longitude must fit DECIMAL(11,8)")
    @DecimalMin(value = "-180.0", message = "Destination longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Destination longitude must be <= 180")
    private BigDecimal destinationLongitude;

    private String destinationAddress;

    private UUID selectedRouteId;

    @NotNull(message = "Status is required")
    @Builder.Default
    private TripStatus status = TripStatus.PLANNING;
}
