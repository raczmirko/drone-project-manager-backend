package hu.okrim.droneprojectmanager.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record DroneOperationRequestDto (
        String code,
        String name,
        String objective,
        LocalDate date,
        String description,
        UUID locationId,
        String drone,
        String flightMode,
        String weatherDescription,
        Double kpIndex,
        LocalDateTime takeoffTime,
        LocalDateTime landingTime,
        Integer flightDurationSeconds
){
}
