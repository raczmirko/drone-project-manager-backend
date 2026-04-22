package hu.okrim.droneprojectmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectResponseDto(
        UUID id,
        @NotBlank
        @Size(max = 50)
        String code,
        @NotBlank
        @Size(max = 255)
        String name,
        @Size(max = 50)
        String status,
        String description,
        String objective,
        LocalDate startDate,
        LocalDate endDate,
        Instant createdAt,
        Instant updatedAt
) {
}