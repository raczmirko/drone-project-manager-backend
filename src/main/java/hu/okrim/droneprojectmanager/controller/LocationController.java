package hu.okrim.droneprojectmanager.controller;

import hu.okrim.droneprojectmanager.dto.LocationRequestDto;
import hu.okrim.droneprojectmanager.model.Location;
import hu.okrim.droneprojectmanager.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public Page<Location> getAllLocations(@PageableDefault(size = 20) Pageable pageable) {
        return locationService.getAllLocations(pageable);
    }

    @GetMapping("/{id}")
    public Location getLocation(@PathVariable UUID id) {
        return locationService.getLocationById(id);
    }

    @PostMapping
    public void createLocation(@RequestBody LocationRequestDto requestDto) {
        Location location = new Location(requestDto.name(), requestDto.longitude(), requestDto.latitude());
        locationService.save(location);
    }

    @DeleteMapping("/{id}")
    public void deleteLocation(@PathVariable UUID id) {
        locationService.delete(id);
    }

}
