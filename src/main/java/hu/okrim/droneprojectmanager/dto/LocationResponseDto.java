package hu.okrim.droneprojectmanager.dto;

import jakarta.persistence.Column;

import java.util.UUID;

/**
 * LocationResponseDto class represents a response DTO for a location.
 */
public record LocationResponseDto (
        UUID id,
        String name,
        Double longitude,
        Double latitude
) {
}
