package hazardhub.com.hub.service;

import hazardhub.com.hub.model.dto.HazardVerificationDTO;
import hazardhub.com.hub.model.enums.VerificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface HazardVerificationService {

    HazardVerificationDTO create(HazardVerificationDTO dto);

    Optional<HazardVerificationDTO> findById(String id);

    List<HazardVerificationDTO> findAll();

    Page<HazardVerificationDTO> findAll(Pageable pageable);

    HazardVerificationDTO update(String id, HazardVerificationDTO dto);

    void delete(String id);

    List<HazardVerificationDTO> findByHazardId(String hazardId);

    List<HazardVerificationDTO> findByUserId(String userId);

    List<HazardVerificationDTO> findByVerificationType(VerificationType verificationType);

    Optional<HazardVerificationDTO> findByHazardIdAndUserId(String hazardId, String userId);

    List<HazardVerificationDTO> findByHazardIdAndVerificationType(String hazardId, VerificationType verificationType);

    long countByHazardIdAndVerificationType(String hazardId, VerificationType verificationType);

    boolean existsByHazardIdAndUserId(String hazardId, String userId);
}
