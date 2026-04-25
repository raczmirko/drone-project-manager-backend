package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.model.DroneOperationFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DroneOperationFileService {
    Page<DroneOperationFile> findAllByOperationId(UUID operationId, Pageable pageable);
    DroneOperationFile getDroneOperationFileById(UUID id);
    void saveDroneOperationFile(DroneOperationFile operationFile);
    void deleteDroneOperationFile(DroneOperationFile operationFile);
}
