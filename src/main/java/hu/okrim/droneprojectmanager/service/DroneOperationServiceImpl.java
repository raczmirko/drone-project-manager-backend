package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.model.DroneOperation;
import hu.okrim.droneprojectmanager.repository.DroneOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DroneOperationServiceImpl implements DroneOperationService {

    private final DroneOperationRepository droneOperationRepository;

    @Override
    public void save(DroneOperation droneOperation) {
        droneOperationRepository.save(droneOperation);
    }

    @Override
    public DroneOperation getById(UUID id) {
        return droneOperationRepository.getReferenceById(id);
    }

    @Override
    public DroneOperation getByCode(String code) {
        return droneOperationRepository.findByCode(code);
    }

    @Override
    public Page<DroneOperation> getAll(UUID projectId, Pageable pageable) {
        return droneOperationRepository.findAllByProjectId(projectId, pageable);
    }

    @Override
    public void delete(DroneOperation droneOperation) {
        droneOperationRepository.delete(droneOperation);
    }
}
