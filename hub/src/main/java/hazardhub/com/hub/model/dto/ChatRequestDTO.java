package hazardhub.com.hub.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hazardhub.com.hub.model.enums.VehicleType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO {

    @NotBlank(message = "Message is required")
    private String message;

    @DecimalMin(value = "-180.0", message = "Origin longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Origin longitude must be <= 180")
    private Double originLongitude;

    @DecimalMin(value = "-90.0", message = "Origin latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Origin latitude must be <= 90")
    private Double originLatitude;

    private String originAddress;

    @DecimalMin(value = "-180.0", message = "Destination longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Destination longitude must be <= 180")
    private Double destinationLongitude;

    @DecimalMin(value = "-90.0", message = "Destination latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Destination latitude must be <= 90")
    private Double destinationLatitude;

    private String destinationAddress;

    private VehicleType vehicleType;

    @JsonIgnore
    public boolean hasRouteContext() {
        return originLongitude != null
                && originLatitude != null
                && destinationLongitude != null
                && destinationLatitude != null;
    }
}
