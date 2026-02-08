package hazardhub.com.hub.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteSuggestionResponseDTO {

    private String message;

    private List<SuggestedRouteDTO> routes;
}
