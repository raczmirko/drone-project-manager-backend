package hu.okrim.droneprojectmanager.repository;

import hu.okrim.droneprojectmanager.model.DroneOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for DroneOperation.
 */
public interface DroneOperationRepository extends JpaRepository<DroneOperation, UUID> {

    /**
     * Find a drone operation by code.
     */
    Optional<DroneOperation> findByCode(String code);

    /**
     * Find all drone operations by project id.
     */
    Page<DroneOperation> findAllByProjectId(UUID projectId, Pageable pageable);
}
