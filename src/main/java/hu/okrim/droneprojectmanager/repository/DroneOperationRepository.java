package hu.okrim.droneprojectmanager.repository;

import hu.okrim.droneprojectmanager.model.DroneOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DroneOperationRepository extends JpaRepository<DroneOperation, UUID> {
    DroneOperation findByCode(String code);

    Page<DroneOperation> findAllByProjectId(UUID projectId, Pageable pageable);
}
