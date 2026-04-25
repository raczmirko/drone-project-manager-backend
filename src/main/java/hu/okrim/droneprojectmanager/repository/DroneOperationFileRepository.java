package hu.okrim.droneprojectmanager.repository;

import hu.okrim.droneprojectmanager.model.DroneOperationFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DroneOperationFileRepository extends JpaRepository<DroneOperationFile, UUID> {
    Page<DroneOperationFile> findAllByDroneOperationId(UUID operationId, Pageable pageable);
}
