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

    /**
     * Selects the specified route and deselects all other routes belonging to
     * the same trip. Only one route per trip can be selected at a time.
     *
     * <p>
     * The operation is performed with atomic MongoDB updates to prevent
     * concurrent requests from leaving multiple routes selected.
     *
     * @param id the ID of the route to select
     * @return the updated route with {@code isSelected = true}
     * @throws hazardhub.com.hub.exception.ResourceNotFoundException if no route
     *                                                               exists with the
     *                                                               given ID
     */
    RouteDTO selectRoute(String id);
}
