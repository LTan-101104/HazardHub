package hazardhub.com.hub.model.entity;

import hazardhub.com.hub.model.BaseEntity;
import hazardhub.com.hub.model.enums.SOSEventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "sos_events")
public class SOSEvent extends BaseEntity {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("trip_id")
    private String tripId;

    @Field("triggered_at")
    private Instant triggeredAt;

    @Field("resolved_at")
    private Instant resolvedAt;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    @Field("location_accuracy_meters")
    private Double locationAccuracyMeters;

    private SOSEventStatus status;

    @Field("dispatch_notified")
    private Boolean dispatchNotified;

    @Field("dispatch_reference")
    private String dispatchReference;
}
