package hazardhub.com.hub.service;

import hazardhub.com.hub.model.dto.ImageAnalysisResponseDTO;

public interface GeminiService {

    ImageAnalysisResponseDTO analyzeHazardImage(String imageUrl);
}
