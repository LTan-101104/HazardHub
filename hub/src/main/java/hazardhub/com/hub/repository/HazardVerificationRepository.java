package hazardhub.com.hub.repository;

import hazardhub.com.hub.model.entity.HazardVerification;
import hazardhub.com.hub.model.enums.VerificationType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HazardVerificationRepository extends MongoRepository<HazardVerification, String> {

    List<HazardVerification> findByHazardId(String hazardId);

    List<HazardVerification> findByUserId(String userId);

    List<HazardVerification> findByVerificationType(VerificationType verificationType);

    Optional<HazardVerification> findByHazardIdAndUserId(String hazardId, String userId);

    List<HazardVerification> findByHazardIdAndVerificationType(String hazardId, VerificationType verificationType);

    long countByHazardIdAndVerificationType(String hazardId, VerificationType verificationType);

    boolean existsByHazardIdAndUserId(String hazardId, String userId);
}
