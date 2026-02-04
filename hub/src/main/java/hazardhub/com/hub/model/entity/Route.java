package hazardhub.com.hub.model.entity;

import hazardhub.com.hub.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "routes")
public class Route extends BaseEntity {

    @Id
    private String id;

    @Indexed
    @Field("trip_id")
    private String tripId;

    @Field("polyline")
    private String polyline;

    @Field("waypoints")
    private Map<String, Object> waypoints;

    @Field("distance_meters")
    private Integer distanceMeters;

    @Field("duration_seconds")
    private Integer durationSeconds;

    @Field("safety_score")
    private Double safetyScore;

    @Field("safety_analysis")
    private Map<String, Object> safetyAnalysis;

    @Field("hazards_considered")
    private List<String> hazardsConsidered;

    @Field("is_selected")
    @Builder.Default
    private Boolean isSelected = false;
}
