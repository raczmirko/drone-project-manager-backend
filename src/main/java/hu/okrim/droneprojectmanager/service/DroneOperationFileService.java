package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.model.DroneOperationFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for managing DroneOperationFile.
 */
public interface DroneOperationFileService {

    /**
     * Find all drone operation files by drone operation id. Return a page of results.
     * @param operationId The drone operation id.
     * @param pageable The pagination information.
     * @return A page of drone operation files.
     */
    Page<DroneOperationFile> findAllByOperationId(UUID operationId, Pageable pageable);

    /**
     * Get drone operation file by id
     * @param id The id of the drone operation file
     * @return The drone operation file
     */
    DroneOperationFile getDroneOperationFileById(UUID id);

    /**
     * Save or update a drone operation file.
     * @param operationFile The drone operation file to save or update.
     */
    void saveDroneOperationFile(DroneOperationFile operationFile);

    /**
     * Delete a drone operation file.
     * @param operationFile The drone operation file to delete.
     */
    void deleteDroneOperationFile(DroneOperationFile operationFile);
}
