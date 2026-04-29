package hu.okrim.droneprojectmanager.repository;

import hu.okrim.droneprojectmanager.model.OperationImageMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for DroneOperationImageMetadata.
 */
public interface OperationImageMetadataRepository extends JpaRepository<OperationImageMetadata, UUID> {

    /**
     * Find all drone operation image metadata by operation code. Return a page of results.
     */
    Page<OperationImageMetadata> findByOperationCode(String operationCode, Pageable pageable);

    /**
     * Find all drone operation image metadata by operation code. Return an ordered list of results.
     */
    List<OperationImageMetadata> findAllByOperationCodeOrderByCapturedAtAscCreatedAtAsc(String operationCode);

    long countByOperationCode(String operationCode);

    void deleteByOperationCode(String operationCode);
}