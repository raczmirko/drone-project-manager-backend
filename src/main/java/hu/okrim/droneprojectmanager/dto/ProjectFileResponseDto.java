package hu.okrim.droneprojectmanager.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectFileResponseDto (
    UUID id,
    String filename,
    LocalDate uploadDate,
    long sizeBytes
) {
}
