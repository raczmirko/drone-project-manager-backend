package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.model.DroneOperation;
import hu.okrim.droneprojectmanager.model.DroneOperationFile;
import hu.okrim.droneprojectmanager.repository.DroneOperationFileRepository;
import hu.okrim.droneprojectmanager.repository.DroneOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DroneOperationFileServiceImpl implements DroneOperationFileService {

    private final DroneOperationFileRepository droneOperationFileRepository;


    @Override
    public Page<DroneOperationFile> findAllByOperationId(UUID operationId, Pageable pageable) {
        return droneOperationFileRepository.findAllByDroneOperationId(operationId, pageable);
    }

    @Override
    public DroneOperationFile getDroneOperationFileById(UUID id) {
        return droneOperationFileRepository.getReferenceById(id);
    }

    @Override
    public void saveDroneOperationFile(DroneOperationFile operationFile) {
        droneOperationFileRepository.save(operationFile);
    }

    @Override
    public void deleteDroneOperationFile(DroneOperationFile operationFile) {
        droneOperationFileRepository.delete(operationFile);
    }
}
