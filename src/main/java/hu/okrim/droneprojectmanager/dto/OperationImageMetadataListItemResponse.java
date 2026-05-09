package hu.okrim.droneprojectmanager.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for image metadata list item.
 */
public record OperationImageMetadataListItemResponse(
        UUID id,
        String originalFilename,
        String mimeType,
        Long fileSizeBytes,
        Integer imageWidth,
        Integer imageHeight,
        LocalDateTime capturedAt,
        Double gpsLatitude,
        Double gpsLongitude,
        Double gpsAltitude,
        String cameraMake,
        String cameraModel,
        Integer orientation,
        Double focalLength,
        Integer isoValue,
        Double aperture,
        String exposureTime,
        Instant createdAt
) {
}