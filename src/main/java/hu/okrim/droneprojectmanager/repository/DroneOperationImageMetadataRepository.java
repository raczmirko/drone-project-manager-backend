package hu.okrim.droneprojectmanager.repository;

import hu.okrim.droneprojectmanager.model.DroneOperationImageMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for DroneOperationImageMetadata.
 */
public interface DroneOperationImageMetadataRepository extends JpaRepository<DroneOperationImageMetadata, UUID> {

    /**
     * Find all drone operation image metadata by operation code. Return a page of results.
     */
    Page<DroneOperationImageMetadata> findByOperationCode(String operationCode, Pageable pageable);

    /**
     * Find all drone operation image metadata by operation code. Return an ordered list of results.
     */
    List<DroneOperationImageMetadata> findAllByOperationCodeOrderByCapturedAtAscCreatedAtAsc(String operationCode);

    long countByOperationCode(String operationCode);

    void deleteByOperationCode(String operationCode);
}