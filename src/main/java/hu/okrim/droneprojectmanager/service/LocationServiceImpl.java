package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.model.Location;
import hu.okrim.droneprojectmanager.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public void save(Location location) {
        locationRepository.save(location);
    }

    @Override
    public Location getLocationById(UUID id) {
        return locationRepository.getReferenceById(id);
    }

    @Override
    public void delete(UUID id) {
        locationRepository.deleteById(id);
    }

    @Override
    public Page<Location> getAllLocations(Pageable pageable) {
        return locationRepository.findAll(pageable);
    }
}
