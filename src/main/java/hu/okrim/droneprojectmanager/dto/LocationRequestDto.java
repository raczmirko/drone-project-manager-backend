package hu.okrim.droneprojectmanager.dto;

/**
 * A Data Transfer Object (DTO) that represents a request to create a Location.
 *
 * The LocationRequestDto record is used to encapsulate information about a location,
 * including its name, geographical longitude, and latitude.
 */
public record LocationRequestDto(
        String name,
        Double longitude,
        Double latitude
) {
}
