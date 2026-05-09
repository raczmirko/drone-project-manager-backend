package hu.okrim.droneprojectmanager.dto;

/**
 * Response DTO for image metadata extraction.
 */
public record OperationImageMetadataExtractionResponse (
    int processedCount,
    int extractedCount,
    int errorCount
) {

}