package hazardhub.com.hub.repository;

import hazardhub.com.hub.model.entity.Hazard;
import hazardhub.com.hub.model.enums.HazardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HazardRepository extends MongoRepository<Hazard, String> {

    List<Hazard> findByReporterId(String reporterId);

    List<Hazard> findByStatus(HazardStatus status);

    Page<Hazard> findByStatus(HazardStatus status, Pageable pageable);

    @Query("{ 'location': { $nearSphere: { $geometry: ?0, $maxDistance: ?1 } } }")
    List<Hazard> findByLocationNear(GeoJsonPoint point, double maxDistanceMeters);

    @Query("{ 'location': { $nearSphere: { $geometry: ?0, $maxDistance: ?1 } }, 'status': ?2 }")
    List<Hazard> findByLocationNearAndStatus(GeoJsonPoint point, double maxDistanceMeters, HazardStatus status);
}
