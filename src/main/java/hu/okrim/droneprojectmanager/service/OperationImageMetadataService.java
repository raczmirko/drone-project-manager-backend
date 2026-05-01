package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing image metadata.
 */
public interface OperationImageMetadataService {

    /**
     * Extracts image metadata from the given files and persists them in the database.
     * @param operationCode The code of the operation to extract metadata for.
     * @param files The list of files to extract metadata from.
     * @return The response containing the extracted metadata.
     */
    OperationImageMetadataExtractionResponse extractAndPersist(String operationCode, List<MultipartFile> files);

    /**
     * Get the page of image metadata for the operation.
     * @param operationCode The code of the operation.
     * @param pageable The pagination information.
     * @return The page of image metadata.
     */
    Page<OperationImageMetadataListItemResponse> getPage(String operationCode, Pageable pageable);

    /**
     * Analyzes the flight of a drone operation and updates the operation's metadata.
     * @param operationCode The code of the drone operation.
     * @return The updated operation's flight analysis.
     */
    OperationFlightAnalysisResponse analyzeAndUpdateOperation(String operationCode);

    /**
     * Get the flight path of the operation by extracting the GPS coordinates from all images.
     * @param operationCode The code of the operation.
     * @return The flight path of the operation.
     */
    List<OperationFlightPathPointResponseDto> getFlightPath(String operationCode);

    /**
     * Get the dashboard data for the operation.
     * @param operationCode The code of the operation.
     * @return The dashboard data for the operation.
     */
    OperationImageMetadataDashboardResponse getDashboard(String operationCode);

    /**
     * Delete all metadata for a specific operation.
     * @param operationCode The code of the operation.
     */
    void deleteAllOperationMetadata(String operationCode);
}