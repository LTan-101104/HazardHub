package hazardhub.com.hub.service.impl;

import hazardhub.com.hub.config.GoogleMapsConfig;
import hazardhub.com.hub.service.GoogleDirectionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleDirectionsServiceImpl implements GoogleDirectionsService {

    private final RestClient googleMapsRestClient;
    private final GoogleMapsConfig googleMapsConfig;

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getDirections(String origin, String destination, String waypoints, String mode) {
        log.info("Calling Google Directions API: origin={}, destination={}, mode={}", origin, destination, mode);

        String uri = buildUri(origin, destination, waypoints, mode);

        Map<String, Object> response = googleMapsRestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            log.error("Google Directions API returned null response");
            throw new RuntimeException("Google Directions API returned null response");
        }

        String status = (String) response.get("status");
        if (!"OK".equals(status)) {
            log.error("Google Directions API error: status={}", status);
            throw new RuntimeException("Google Directions API error: " + status);
        }

        List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
        log.info("Google Directions API returned {} route(s)", routes != null ? routes.size() : 0);

        return response;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String extractPolyline(Map<String, Object> directionsResponse) {
        List<Map<String, Object>> routes = (List<Map<String, Object>>) directionsResponse.get("routes");
        if (routes == null || routes.isEmpty()) {
            return null;
        }
        Map<String, Object> overviewPolyline = (Map<String, Object>) routes.get(0).get("overview_polyline");
        return overviewPolyline != null ? (String) overviewPolyline.get("points") : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public double extractDistanceMeters(Map<String, Object> directionsResponse) {
        List<Map<String, Object>> legs = extractAllLegs(directionsResponse);
        if (legs == null) return 0;

        double total = 0;
        for (Map<String, Object> leg : legs) {
            Map<String, Object> distance = (Map<String, Object>) leg.get("distance");
            if (distance != null) {
                total += ((Number) distance.get("value")).doubleValue();
            }
        }
        return total;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int extractDurationSeconds(Map<String, Object> directionsResponse) {
        List<Map<String, Object>> legs = extractAllLegs(directionsResponse);
        if (legs == null) return 0;

        int total = 0;
        for (Map<String, Object> leg : legs) {
            Map<String, Object> duration = (Map<String, Object>) leg.get("duration");
            if (duration != null) {
                total += ((Number) duration.get("value")).intValue();
            }
        }
        return total;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractAllLegs(Map<String, Object> directionsResponse) {
        List<Map<String, Object>> routes = (List<Map<String, Object>>) directionsResponse.get("routes");
        if (routes == null || routes.isEmpty()) return null;
        List<Map<String, Object>> legs = (List<Map<String, Object>>) routes.get(0).get("legs");
        if (legs == null || legs.isEmpty()) return null;
        return legs;
    }

    private String buildUri(String origin, String destination, String waypoints, String mode) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/maps/api/directions/json")
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .queryParam("mode", mode)
                .queryParam("key", googleMapsConfig.getApiKey());

        if (waypoints != null && !waypoints.isBlank()) {
            builder.queryParam("waypoints", waypoints);
        }

        return builder.build().toUriString();
    }
}
