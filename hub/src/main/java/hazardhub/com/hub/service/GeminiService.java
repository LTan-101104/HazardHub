package hazardhub.com.hub.service;

import hazardhub.com.hub.model.dto.ChatRequestDTO;
import hazardhub.com.hub.model.dto.ChatResponseDTO;
import hazardhub.com.hub.model.dto.HazardDTO;
import hazardhub.com.hub.model.dto.ImageAnalysisResponseDTO;
import hazardhub.com.hub.model.dto.RouteSuggestionResponseDTO;

import java.util.List;

public interface GeminiService {

    ImageAnalysisResponseDTO analyzeHazardImage(String imageUrl);

    ChatResponseDTO chat(ChatRequestDTO request, RouteSuggestionResponseDTO routeSuggestion, List<HazardDTO> hazards);
}
