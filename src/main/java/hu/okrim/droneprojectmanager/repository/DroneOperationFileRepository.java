package hu.okrim.droneprojectmanager.repository;

import hu.okrim.droneprojectmanager.model.DroneOperationFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for DroneOperationFile.
 */
public interface DroneOperationFileRepository extends JpaRepository<DroneOperationFile, UUID> {

    /**
     * Find all drone operation files by drone operation id. Return a page of results.
     */
    Page<DroneOperationFile> findAllByDroneOperationId(UUID operationId, Pageable pageable);
}
