package hazardhub.com.hub.service;

import hazardhub.com.hub.model.dto.SavedLocationDTO;

import java.util.List;
import java.util.Optional;

public interface SavedLocationService {

    SavedLocationDTO create(SavedLocationDTO savedLocationDTO);

    Optional<SavedLocationDTO> findById(String id);

    List<SavedLocationDTO> findAll();

    List<SavedLocationDTO> findByUserId(String userId);

    SavedLocationDTO update(String id, SavedLocationDTO savedLocationDTO);

    void delete(String id);
}
