package hazardhub.com.hub.service;

import hazardhub.com.hub.model.dto.RouteDTO;

import java.util.List;
import java.util.Optional;

public interface RouteService {

    RouteDTO create(RouteDTO routeDTO);

    Optional<RouteDTO> findById(String id);

    List<RouteDTO> findByTripId(String tripId);

    Optional<RouteDTO> findSelectedByTripId(String tripId);

    RouteDTO update(String id, RouteDTO routeDTO);

    void delete(String id);

    RouteDTO selectRoute(String id);
}
