package hazardhub.com.hub.service;

import java.util.Map;

public interface GoogleDirectionsService {

    Map<String, Object> getDirections(String origin, String destination, String waypoints, String mode);
}
