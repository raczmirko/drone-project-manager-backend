package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.model.DroneOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DroneOperationService {

    /**
     * Saves a drone operation.
     * @param droneOperation The drone operation to save.
     */
    void save(DroneOperation droneOperation);

    /**
     * Get drone operation by id
     * @param id The id of the drone operation
     * @return The drone operation
     */
    DroneOperation getById(UUID id);

    /**
     * Get drone operation by code
     * @param code The code of the drone operation
     * @return The drone operation
     */
    DroneOperation getByCode(String code);

    /**
     * Get all drone operations for a project.
     * @param projectId The project ID.
     * @param pageable The pagination information.
     * @return A page of drone operations.
     */
    Page<DroneOperation> getAll(UUID projectId, Pageable pageable);

    /**
     * Delete a drone operation.
     * @param droneOperation The drone operation to delete.
     */
    void delete(DroneOperation droneOperation);

}
