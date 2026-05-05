package hu.okrim.droneprojectmanager.controller;

import hu.okrim.droneprojectmanager.dto.LocationRequestDto;
import hu.okrim.droneprojectmanager.dto.LocationResponseDto;
import hu.okrim.droneprojectmanager.model.Location;
import hu.okrim.droneprojectmanager.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    /**
     * Get all locations.
     * @param pageable The pagination information.
     * @return A page of locations.
     */
    @GetMapping
    public Page<Location> getAllLocations(@PageableDefault(size = 20) Pageable pageable) {
        return locationService.getAllLocations(pageable);
    }

    /**
     * Get a location by its id.
     * @param id The id of the location.
     * @return The location.
     */
    @GetMapping("/{id}")
    public Location getLocation(@PathVariable UUID id) {
        return locationService.getLocationById(id);
    }

    /**
     * Create a new location.
     * @param requestDto The location data.
     * @return The created location.
     */
    @PostMapping
    public ResponseEntity<LocationResponseDto> createLocation(
            @RequestBody LocationRequestDto requestDto
    ) {
        Location location = new Location(
                requestDto.name(),
                requestDto.longitude(),
                requestDto.latitude()
        );

        locationService.save(location);

        LocationResponseDto response = new LocationResponseDto(
                location.getId(),
                location.getName(),
                location.getLongitude(),
                location.getLatitude()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Delete a location by its id.
     * @param id The id of the location.
     */
    @DeleteMapping("/{id}")
    public void deleteLocation(@PathVariable UUID id) {
        locationService.delete(id);
    }

}
