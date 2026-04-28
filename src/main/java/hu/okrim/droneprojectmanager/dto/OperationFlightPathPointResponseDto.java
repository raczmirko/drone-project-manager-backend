package hu.okrim.droneprojectmanager.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record OperationFlightPathPointResponseDto(
        UUID id,
        LocalDateTime capturedAt,
        Double gpsLatitude,
        Double gpsLongitude
) {}
