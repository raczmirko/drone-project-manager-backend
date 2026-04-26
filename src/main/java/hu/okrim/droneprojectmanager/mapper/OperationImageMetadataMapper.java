package hu.okrim.droneprojectmanager.mapper;

import hu.okrim.droneprojectmanager.dto.OperationFlightAnalysisResponse;
import hu.okrim.droneprojectmanager.dto.OperationImageMetadataListItemResponse;
import hu.okrim.droneprojectmanager.dto.OperationImageMetadataPageResponse;
import hu.okrim.droneprojectmanager.model.DroneOperation;
import hu.okrim.droneprojectmanager.model.DroneOperationImageMetadata;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

/**
 * Mapper class for mapping between entity and response objects.
 */
@UtilityClass
public class OperationImageMetadataMapper {

    /**
     * Maps a DroneOperationImageMetadata entity to an OperationImageMetadataListItemResponse DTO.
     *
     * @param entity the DroneOperationImageMetadata entity to be transformed
     * @return an OperationImageMetadataListItemResponse object containing the mapped data
     */
    public OperationImageMetadataListItemResponse toListItemResponse(DroneOperationImageMetadata entity) {
        return OperationImageMetadataListItemResponse.builder()
                .id(entity.getId())
                .originalFilename(entity.getOriginalFilename())
                .mimeType(entity.getMimeType())
                .fileSizeBytes(entity.getFileSizeBytes())
                .imageWidth(entity.getImageWidth())
                .imageHeight(entity.getImageHeight())
                .capturedAt(entity.getCapturedAt())
                .gpsLatitude(entity.getGpsLatitude())
                .gpsLongitude(entity.getGpsLongitude())
                .gpsAltitude(entity.getGpsAltitude())
                .cameraMake(entity.getCameraMake())
                .cameraModel(entity.getCameraModel())
                .metadataStatus(entity.getMetadataStatus())
                .metadataError(entity.getMetadataError())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Maps a Page<DroneOperationImageMetadata> to an OperationImageMetadataPageResponse.
     * @param page The page of DroneOperationImageMetadata to be mapped.
     * @return The OperationImageMetadataPageResponse object containing the mapped data.
     */
    public OperationImageMetadataPageResponse toPageResponse(Page<DroneOperationImageMetadata> page) {
        return OperationImageMetadataPageResponse.builder()
                .content(page.getContent().stream().map(OperationImageMetadataMapper::toListItemResponse).toList())
                .totalElements(page.getTotalElements())
                .page(page.getNumber())
                .size(page.getSize())
                .build();
    }

    /**
     * Maps a DroneOperation to an OperationFlightAnalysisResponse.
     * @param operation The drone operation to be mapped.
     * @return The OperationFlightAnalysisResponse object containing the mapped data.
     */
    public OperationFlightAnalysisResponse toFlightAnalysisResponse(DroneOperation operation) {
        return OperationFlightAnalysisResponse.builder()
                .flightDurationSeconds(operation.getFlightDurationSeconds())
                .avgRecordingAltitude(operation.getAvgRecordingAltitude())
                .recordingLength(operation.getRecordingLength())
                .recordingStart(operation.getRecordingStart())
                .recordingEnd(operation.getRecordingEnd())
                .numberOfRecordings(operation.getNumberOfRecordings())
                .build();
    }
}