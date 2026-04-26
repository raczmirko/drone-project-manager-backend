package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.dto.OperationFlightAnalysisResponse;
import hu.okrim.droneprojectmanager.dto.OperationImageMetadataExtractionResponse;
import hu.okrim.droneprojectmanager.dto.OperationImageMetadataPageResponse;
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
     * @param operationCode The code of the operation to get metadata for.
     * @param page The page number to retrieve.
     * @param size The number of items per page.
     * @return The page of image metadata.
     */
    OperationImageMetadataPageResponse getPage(String operationCode, int page, int size);

    /**
     * Analyzes the flight of a drone operation and updates the operation's metadata.
     * @param operationCode The code of the drone operation.
     * @return The updated operation's flight analysis.
     */
    OperationFlightAnalysisResponse analyzeAndUpdateOperation(String operationCode);
}