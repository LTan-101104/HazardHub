package hazardhub.com.hub.service;

import hazardhub.com.hub.model.dto.ChatRequestDTO;
import hazardhub.com.hub.model.dto.ChatResponseDTO;
import hazardhub.com.hub.model.dto.ImageAnalysisResponseDTO;
import hazardhub.com.hub.model.dto.RouteSuggestionResponseDTO;

public interface GeminiService {

    ImageAnalysisResponseDTO analyzeHazardImage(String imageUrl);

    ChatResponseDTO chat(ChatRequestDTO request, RouteSuggestionResponseDTO routeSuggestion);
}
