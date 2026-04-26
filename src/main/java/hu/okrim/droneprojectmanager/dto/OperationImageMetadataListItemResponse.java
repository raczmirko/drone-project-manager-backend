package hu.okrim.droneprojectmanager.dto;

import hu.okrim.droneprojectmanager.model.OperationImageMetadataStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for image metadata list item.
 */
@Value
@Builder
public class OperationImageMetadataListItemResponse {
    UUID id;
    String originalFilename;
    String mimeType;
    Long fileSizeBytes;
    Integer imageWidth;
    Integer imageHeight;
    LocalDateTime capturedAt;
    Double gpsLatitude;
    Double gpsLongitude;
    Double gpsAltitude;
    String cameraMake;
    String cameraModel;
    OperationImageMetadataStatus metadataStatus;
    String metadataError;
    Instant createdAt;
}