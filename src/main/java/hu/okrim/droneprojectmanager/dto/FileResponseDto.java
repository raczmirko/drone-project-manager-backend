package hu.okrim.droneprojectmanager.dto;

import java.time.LocalDate;
import java.util.UUID;

public record FileResponseDto(
    UUID id,
    String filename,
    LocalDate uploadDate,
    long sizeBytes
) {
}
