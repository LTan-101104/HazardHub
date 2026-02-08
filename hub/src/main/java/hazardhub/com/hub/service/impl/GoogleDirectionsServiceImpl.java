package hazardhub.com.hub.service.impl;

import hazardhub.com.hub.config.GoogleMapsConfig;
import hazardhub.com.hub.service.GoogleDirectionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
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
        log.info("Calling Google Directions API: origin={}, destination={}, waypoints={}, mode={}",
                origin, destination, waypoints, mode);

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

    /**
     * Extract the encoded polyline from the first route in a Directions API
     * response.
     */
    @SuppressWarnings("unchecked")
    public static String extractPolyline(Map<String, Object> directionsResponse) {
        List<Map<String, Object>> routes = (List<Map<String, Object>>) directionsResponse.get("routes");
        if (routes == null || routes.isEmpty()) {
            return null;
        }
        Map<String, Object> overviewPolyline = (Map<String, Object>) routes.get(0).get("overview_polyline");
        return overviewPolyline != null ? (String) overviewPolyline.get("points") : null;
    }

    /**
     * Extract total distance in meters from the first route's first leg.
     */
    @SuppressWarnings("unchecked")
    public static Double extractDistanceMeters(Map<String, Object> directionsResponse) {
        Map<String, Object> leg = extractFirstLeg(directionsResponse);
        if (leg == null)
            return null;
        Map<String, Object> distance = (Map<String, Object>) leg.get("distance");
        return distance != null ? ((Number) distance.get("value")).doubleValue() : null;
    }

    /**
     * Extract total duration in seconds from the first route's first leg.
     */
    @SuppressWarnings("unchecked")
    public static Integer extractDurationSeconds(Map<String, Object> directionsResponse) {
        Map<String, Object> leg = extractFirstLeg(directionsResponse);
        if (leg == null)
            return null;
        Map<String, Object> duration = (Map<String, Object>) leg.get("duration");
        return duration != null ? ((Number) duration.get("value")).intValue() : null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> extractFirstLeg(Map<String, Object> directionsResponse) {
        List<Map<String, Object>> routes = (List<Map<String, Object>>) directionsResponse.get("routes");
        if (routes == null || routes.isEmpty())
            return null;
        List<Map<String, Object>> legs = (List<Map<String, Object>>) routes.get(0).get("legs");
        if (legs == null || legs.isEmpty())
            return null;
        return legs.get(0);
    }

    private String buildUri(String origin, String destination, String waypoints, String mode) {
        StringBuilder sb = new StringBuilder("/maps/api/directions/json?");
        sb.append("origin=").append(origin);
        sb.append("&destination=").append(destination);
        sb.append("&mode=").append(mode);

        if (waypoints != null && !waypoints.isBlank()) {
            sb.append("&waypoints=").append(waypoints);
        }

        sb.append("&key=").append(googleMapsConfig.getApiKey());

        return sb.toString();
    }
}
