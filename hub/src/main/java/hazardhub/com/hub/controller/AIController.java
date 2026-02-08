package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.HazardDTO;
import hazardhub.com.hub.model.dto.ImageAnalysisRequestDTO;
import hazardhub.com.hub.model.dto.ImageAnalysisResponseDTO;
import hazardhub.com.hub.model.dto.RouteSuggestionRequestDTO;
import hazardhub.com.hub.model.dto.RouteSuggestionResponseDTO;
import hazardhub.com.hub.service.GeminiService;
import hazardhub.com.hub.service.HazardService;
import hazardhub.com.hub.service.RouteSuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Validated
@Tag(name = "AI", description = "AI-powered hazard analysis and route suggestions")
public class AIController {

    private final GeminiService geminiService;
    private final RouteSuggestionService routeSuggestionService;
    private final HazardService hazardService;

    @PostMapping("/analyze-hazard-image")
    @Operation(summary = "Analyze a hazard image using Gemini AI", description = "Takes a Firebase Storage image URL and returns an AI-generated hazard description")
    public ResponseEntity<ImageAnalysisResponseDTO> analyzeHazardImage(
            @Valid @RequestBody ImageAnalysisRequestDTO request) {
        ImageAnalysisResponseDTO response = geminiService.analyzeHazardImage(request.getImageUrl());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/suggest-routes")
    @Operation(summary = "AI-powered route suggestions avoiding hazards", description = "Uses Gemini AI to analyze hazards and suggest safe routes with Google Directions API polylines")
    public ResponseEntity<RouteSuggestionResponseDTO> suggestRoutes(
            @Valid @RequestBody RouteSuggestionRequestDTO request) {

        // Compute search area — midpoint of origin/destination
        double midLat = (request.getOriginLatitude() + request.getDestinationLatitude()) / 2;
        double midLng = (request.getOriginLongitude() + request.getDestinationLongitude()) / 2;

        // Search radius = distance between origin/dest * 1.5, minimum 5000m
        double distanceBetween = haversineMeters(
                request.getOriginLatitude(), request.getOriginLongitude(),
                request.getDestinationLatitude(), request.getDestinationLongitude());
        double searchRadius = Math.max(distanceBetween * 1.5, 5000);

        // Fetch nearby active hazards
        List<HazardDTO> hazards = hazardService.findNearbyActive(midLng, midLat, searchRadius);

        // Gemini + Directions API
        RouteSuggestionResponseDTO response = routeSuggestionService.suggestRoutes(request, hazards);

        return ResponseEntity.ok(response);
    }

    /**
     * Haversine formula to calculate distance in meters between two lat/lng points.
     */
    private double haversineMeters(double lat1, double lng1, double lat2, double lng2) {
        double R = 6_371_000; // Earth radius in meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
