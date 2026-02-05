package hazardhub.com.hub.repository;

import hazardhub.com.hub.model.entity.SavedLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedLocationRepository extends MongoRepository<SavedLocation, String> {

    List<SavedLocation> findByUserId(String userId);

}
