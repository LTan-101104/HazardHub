package hazardhub.com.hub.repository;

import hazardhub.com.hub.model.entity.EmergencyContact;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmergencyContactRepository extends MongoRepository<EmergencyContact, String> {

    List<EmergencyContact> findByUserIdOrderByPriorityAsc(String userId);

    Optional<EmergencyContact> findByIdAndUserId(String id, String userId);
}
