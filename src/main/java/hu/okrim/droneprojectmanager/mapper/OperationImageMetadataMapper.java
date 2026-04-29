package hu.okrim.droneprojectmanager.mapper;

import hu.okrim.droneprojectmanager.dto.OperationFlightAnalysisResponse;
import hu.okrim.droneprojectmanager.dto.OperationImageMetadataListItemResponse;
import hu.okrim.droneprojectmanager.model.DroneOperation;
import hu.okrim.droneprojectmanager.model.OperationImageMetadata;
import lombok.experimental.UtilityClass;

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
    public OperationImageMetadataListItemResponse toListItemResponse(OperationImageMetadata entity) {
        return new OperationImageMetadataListItemResponse(
                entity.getId(),
                entity.getOriginalFilename(),
                entity.getMimeType(),
                entity.getFileSizeBytes(),
                entity.getImageWidth(),
                entity.getImageHeight(),
                entity.getCapturedAt(),
                entity.getGpsLatitude(),
                entity.getGpsLongitude(),
                entity.getGpsAltitude(),
                entity.getCameraMake(),
                entity.getCameraModel(),
                entity.getOrientation(),
                entity.getFocalLength(),
                entity.getIsoValue(),
                entity.getAperture(),
                entity.getExposureTime(),
                entity.getCreatedAt()
        );
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