package hazardhub.com.hub.repository;

import hazardhub.com.hub.model.entity.Trip;
import hazardhub.com.hub.model.enums.TripStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends MongoRepository<Trip, String> {

    List<Trip> findByUserId(String userId);

    List<Trip> findByStatus(TripStatus status);

    List<Trip> findByUserIdAndStatus(String userId, TripStatus status);

    Optional<Trip> findBySelectedRouteId(String selectedRouteId);

    long countByUserId(String userId);

    long countByUserIdAndStatus(String userId, TripStatus status);

    boolean existsByUserIdAndStatus(String userId, TripStatus status);
}