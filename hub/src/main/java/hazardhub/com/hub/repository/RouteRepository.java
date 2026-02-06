package hazardhub.com.hub.repository;

import hazardhub.com.hub.model.entity.Route;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends MongoRepository<Route, String> {

    /**
     * Returns all routes belonging to the given trip.
     *
     * @param tripId the trip identifier
     * @return all routes for the trip, or an empty list if none exist
     */
    List<Route> findByTripId(String tripId);

    /**
     * Returns the currently selected route for a trip. At most one route per
     * trip should have {@code isSelected = true}; this constraint is enforced
     * by {@link hazardhub.com.hub.service.RouteService#selectRoute(String)}.
     *
     * @param tripId the trip identifier
     * @return the selected route, or empty if no route is selected
     */
    Optional<Route> findByTripIdAndIsSelectedTrue(String tripId);
}
