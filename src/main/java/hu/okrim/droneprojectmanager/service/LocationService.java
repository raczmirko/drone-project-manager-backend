package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface LocationService {
    void save(Location location);
    Location getLocationById(UUID id);
    void delete(UUID id);
    Page<Location> getAllLocations(Pageable pageable);
}
