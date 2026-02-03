package hazardhub.com.hub.model.dto;

import hazardhub.com.hub.model.enums.VerificationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HazardVerificationDTO {

    private String id;

    @NotNull(message = "Hazard ID is required")
    private String hazardId;

    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "Verification type is required")
    private VerificationType verificationType;

    private String comment;

    private String imageUrl;

}
