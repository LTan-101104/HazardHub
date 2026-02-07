package hazardhub.com.hub.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageAnalysisRequestDTO {

    @NotBlank(message = "Image URL is required")
    private String imageUrl;
}
