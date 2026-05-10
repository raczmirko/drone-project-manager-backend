package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for managing Location.
 */
public interface LocationService {

    /**
     * Save a location.
     * @param location The location to save.
     */
    void save(Location location);

    /**
     * Get location by id
     * @param id The id of the location
     * @return The location
     */
    Location getLocationById(UUID id);

    /**
     * Delete location by id
     * @param id The id of the location
     */
    void delete(UUID id);

    /**
     * Get all locations
     * @param pageable The pagination information.
     * @return A page of locations.
     */
    Page<Location> getAllLocations(Pageable pageable);
}
