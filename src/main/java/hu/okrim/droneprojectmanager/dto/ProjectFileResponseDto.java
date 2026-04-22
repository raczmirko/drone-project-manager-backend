package hu.okrim.droneprojectmanager.dto;

import java.util.UUID;

public record ProjectFileResponseDto (
    UUID id,
    String filename
) {
}
