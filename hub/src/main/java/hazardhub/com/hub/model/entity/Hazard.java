package hazardhub.com.hub.model.entity;

import hazardhub.com.hub.model.BaseEntity;
import hazardhub.com.hub.model.enums.HazardSeverity;
import hazardhub.com.hub.model.enums.HazardStatus;
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

//TODO: need some sort of mechanism to differentiate hazard other than id, what if user not approves the hazard and just create the new same hazard?
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "hazards")
public class Hazard extends BaseEntity {

    @Id
    private String id;

    @Field("reporter_id")
    private String reporterId;

    @Field("expires_at")
    private Instant expiresAt;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    @Field("location_accuracy_meters")
    private Double locationAccuracyMeters;

    private String address;

    private HazardSeverity severity;

    private String description;

    @Field("image_url")
    private String imageUrl;

    @Field("thumbnail_url")
    private String thumbnailUrl;

    private HazardStatus status;

    @Field("verification_count")
    private Integer verificationCount;

    @Field("dispute_count")
    private Integer disputeCount;

    @Field("disabled_at")
    private Instant disabledAt;

    @Field("affected_radius_meters")
    private Double affectedRadiusMeters;
}
