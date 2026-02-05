package hazardhub.com.hub.repository;

import hazardhub.com.hub.model.entity.EmergencyContact;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmergencyContactRepository extends MongoRepository<EmergencyContact, UUID> {

    List<EmergencyContact> findByUserIdOrderByPriorityAsc(UUID userId);

    Optional<EmergencyContact> findByIdAndUserId(UUID id, UUID userId);
}
