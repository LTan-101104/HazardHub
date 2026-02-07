package hazardhub.com.hub.repository;

import hazardhub.com.hub.model.entity.SOSEvent;
import hazardhub.com.hub.model.enums.SOSEventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SOSEventRepository extends MongoRepository<SOSEvent, String> {

    List<SOSEvent> findByUserId(String userId);

    List<SOSEvent> findByTripId(String tripId);

    List<SOSEvent> findByStatus(SOSEventStatus status);

    Page<SOSEvent> findByStatus(SOSEventStatus status, Pageable pageable);

    @Query("{ 'location': { $nearSphere: { $geometry: ?0, $maxDistance: ?1 } } }")
    List<SOSEvent> findByLocationNear(GeoJsonPoint point, double maxDistanceMeters);

    @Query("{ 'location': { $nearSphere: { $geometry: ?0, $maxDistance: ?1 } }, 'status': ?2 }")
    List<SOSEvent> findByLocationNearAndStatus(GeoJsonPoint point, double maxDistanceMeters, SOSEventStatus status);
}