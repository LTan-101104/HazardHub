package hazardhub.com.hub.model.entity;

import hazardhub.com.hub.model.BaseEntity;
import hazardhub.com.hub.model.enums.TripStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "trips")
public class Trip extends BaseEntity {

    @Id
    private String id; 

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("started_at")
    private Instant startedAt;

    @Field("completed_at")
    private Instant completedAt;

    @Field("origin_latitude")
    private Double originLatitude;

    @Field("origin_longitude")
    private Double originLongitude;

    @Field("origin_address")
    private String originAddress;

    @Field("destination_latitude")
    private Double destinationLatitude;

    @Field("destination_longitude")
    private Double destinationLongitude;

    @Field("destination_address")
    private String destinationAddress;

    @Field("selected_route_id")
    private String selectedRouteId;

    private TripStatus status;
}
