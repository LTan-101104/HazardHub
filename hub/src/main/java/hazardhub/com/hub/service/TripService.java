package hazardhub.com.hub.service;

import hazardhub.com.hub.model.dto.TripDTO;
import hazardhub.com.hub.model.enums.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TripService {

    TripDTO create(TripDTO dto);

    Optional<TripDTO> findById(String id);

    List<TripDTO> findAll();

    Page<TripDTO> findAll(Pageable pageable);

    TripDTO update(String id, TripDTO dto);

    void delete(String id);

    List<TripDTO> findByUserId(String userId);

    List<TripDTO> findByStatus(TripStatus status);

    List<TripDTO> findByUserIdAndStatus(String userId, TripStatus status);

    Optional<TripDTO> findBySelectedRouteId(String selectedRouteId);

    long countByUserId(String userId);

    long countByUserIdAndStatus(String userId, TripStatus status);

    boolean existsByUserIdAndStatus(String userId, TripStatus status);
}