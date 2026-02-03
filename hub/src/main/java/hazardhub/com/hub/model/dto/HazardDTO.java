package hazardhub.com.hub.model.dto;

import hazardhub.com.hub.model.enums.HazardSeverity;
import hazardhub.com.hub.model.enums.HazardStatus;
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
public class HazardDTO {

    @NotBlank(message = "Reporter ID is required")
    private String reporterId;

    private Instant expiresAt;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    private Double locationAccuracyMeters;

    private String address;

    @NotNull(message = "Severity is required")
    private HazardSeverity severity;

    @NotBlank(message = "Description is required")
    private String description;

    private String imageUrl;

    private String thumbnailUrl;

    private HazardStatus status;

    private Integer verificationCount;

    private Integer disputeCount;

    private Instant disabledAt;

    private Double affectedRadiusMeters;
}
