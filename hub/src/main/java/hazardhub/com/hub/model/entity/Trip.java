package hazardhub.com.hub.model.entity;

import hazardhub.com.hub.model.enums.TripStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "trips")
public class Trip {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotNull(message = "User ID is required")
    @Indexed
    @Field("user_id")
    private UUID userId;

    @NotNull(message = "Created at is required")
    @CreatedDate
    @Builder.Default
    @Field("created_at")
    private Instant createdAt = Instant.now();

    // When navigation started.
    @Field("started_at")
    private Instant startedAt;

    @Field("completed_at")
    private Instant completedAt;

    @NotNull(message = "Origin latitude is required")
    @Digits(integer = 2, fraction = 8, message = "Origin latitude must fit DECIMAL(10,8)")
    @DecimalMin(value = "-90.0", message = "Origin latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Origin latitude must be <= 90")
    @Field(name = "origin_latitude", targetType = FieldType.DECIMAL128)
    private BigDecimal originLatitude;

    @NotNull(message = "Origin longitude is required")
    @Digits(integer = 3, fraction = 8, message = "Origin longitude must fit DECIMAL(11,8)")
    @DecimalMin(value = "-180.0", message = "Origin longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Origin longitude must be <= 180")
    @Field(name = "origin_longitude", targetType = FieldType.DECIMAL128)
    private BigDecimal originLongitude;

    @Field("origin_address")
    private String originAddress;

    @NotNull(message = "Destination latitude is required")
    @Digits(integer = 2, fraction = 8, message = "Destination latitude must fit DECIMAL(10,8)")
    @DecimalMin(value = "-90.0", message = "Destination latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Destination latitude must be <= 90")
    @Field(name = "destination_latitude", targetType = FieldType.DECIMAL128)
    private BigDecimal destinationLatitude;

    @NotNull(message = "Destination longitude is required")
    @Digits(integer = 3, fraction = 8, message = "Destination longitude must fit DECIMAL(11,8)")
    @DecimalMin(value = "-180.0", message = "Destination longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Destination longitude must be <= 180")
    @Field(name = "destination_longitude", targetType = FieldType.DECIMAL128)
    private BigDecimal destinationLongitude;

    @Field("destination_address")
    private String destinationAddress;

    @Field("selected_route_id")
    private UUID selectedRouteId;

    @NotNull(message = "Status is required")
    @Builder.Default
    private TripStatus status = TripStatus.PLANNING;
}
