package hazardhub.com.hub.repository;

import hazardhub.com.hub.model.entity.Route;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends MongoRepository<Route, String> {
    List<Route> findByTripId(String tripId);
    Optional<Route> findByTripIdAndIsSelectedTrue(String tripId);
}
