package hu.okrim.droneprojectmanager.dto;

import java.time.LocalDate;

public record DroneOperationLiteResponseDto (
        String code,
        String name,
        LocalDate operationDate,
        String locationName,
        String flightMode
) {
}
