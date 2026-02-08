package hazardhub.com.hub.service;

import hazardhub.com.hub.model.dto.HazardDTO;
import hazardhub.com.hub.model.dto.RouteSuggestionRequestDTO;
import hazardhub.com.hub.model.dto.RouteSuggestionResponseDTO;

import java.util.List;

public interface RouteSuggestionService {

    RouteSuggestionResponseDTO suggestRoutes(RouteSuggestionRequestDTO request, List<HazardDTO> hazards);
}
