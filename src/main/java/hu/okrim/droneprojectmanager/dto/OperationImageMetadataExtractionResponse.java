package hu.okrim.droneprojectmanager.dto;

import lombok.Builder;
import lombok.Value;

/**
 * Response DTO for image metadata extraction.
 */
@Value
@Builder
public class OperationImageMetadataExtractionResponse {
    int processedCount;
    int extractedCount;
    int errorCount;
}