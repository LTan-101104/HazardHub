package hazardhub.com.hub.model.dto;

import hazardhub.com.hub.model.enums.HazardSeverity;
import hazardhub.com.hub.model.enums.HazardStatus;
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

    private String reporterId;

    private Instant expiresAt;

    private Double longitude;

    private Double latitude;

    private Double locationAccuracyMeters;

    private String address;

    private HazardSeverity severity;

    private String description;

    private String imageUrl;

    private String thumbnailUrl;

    private HazardStatus status;

    private Integer verificationCount;

    private Integer disputeCount;

    private Instant disabledAt;

    private Double affectedRadiusMeters;
}
