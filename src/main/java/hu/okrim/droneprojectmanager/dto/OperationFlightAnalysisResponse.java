package hu.okrim.droneprojectmanager.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Response DTO for flight analysis.
 */
@Value
@Builder
public class OperationFlightAnalysisResponse {
    Integer flightDurationSeconds;
    Double avgRecordingAltitude;
    Double recordingLength;
    LocalDateTime recordingStart;
    LocalDateTime recordingEnd;
    Integer numberOfRecordings;
}