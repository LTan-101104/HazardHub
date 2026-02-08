package hazardhub.com.hub.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectionsParamsDTO {
    private String origin;
    private String destination;
    private String waypoints; // "via:lat,lng|via:lat,lng"
    private String mode; // "driving", "bicycling", "walking"
}
