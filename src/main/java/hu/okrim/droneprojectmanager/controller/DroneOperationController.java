package hu.okrim.droneprojectmanager.controller;

import hu.okrim.droneprojectmanager.dto.DroneOperationRequestDto;
import hu.okrim.droneprojectmanager.model.DroneOperation;
import hu.okrim.droneprojectmanager.model.Location;
import hu.okrim.droneprojectmanager.model.Project;
import hu.okrim.droneprojectmanager.service.DroneOperationService;
import hu.okrim.droneprojectmanager.service.LocationService;
import hu.okrim.droneprojectmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling drone operations.
 */
@RestController
@RequestMapping("/projects/{projectCode}/operations")
@RequiredArgsConstructor
public class DroneOperationController {

    private final DroneOperationService droneOperationService;
    private final ProjectService projectService;
    private final LocationService locationService;

    /**
     * Get all drone operations for a project.
     * @param projectCode The project code.
     * @param pageable The pagination information.
     * @return A page of drone operations.
     */
    @GetMapping
    public Page<DroneOperation> getAll(
            @PathVariable String projectCode,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Project project = projectService.getProjectByCode(projectCode);
        return droneOperationService.getAll(project.getId(), pageable);
    }

    /**
     * Get a drone operation by its code.
     * @param projectCode The project code.
     * @param operationCode The operation code.
     * @return The drone operation wrapped in a ResponseEntity.
     */
    @GetMapping("/{operationCode}")
    public ResponseEntity<DroneOperation> getByCode(
            @PathVariable String projectCode,
            @PathVariable String operationCode
    ) {
        Project project = projectService.getProjectByCode(projectCode);
        DroneOperation droneOperation = droneOperationService.getByCode(operationCode);

        validateOperationBelongsToProject(droneOperation, project);

        return ResponseEntity.ok(droneOperation);
    }

    /**
     * Create a new drone operation.
     * @param projectCode The project code.
     * @param requestDto The request DTO containing the operation data.
     * @return The created drone operation wrapped in a ResponseEntity.
     */
    @PostMapping
    public ResponseEntity<DroneOperation> create(
            @PathVariable String projectCode,
            @RequestBody DroneOperationRequestDto requestDto
    ) {
        Project project = projectService.getProjectByCode(projectCode);

        DroneOperation droneOperation = new DroneOperation();
        droneOperation.setProject(project);
        droneOperation.setCode(requestDto.code());
        droneOperation.setName(requestDto.name());
        droneOperation.setDate(requestDto.date());
        droneOperation.setObjective(requestDto.objective());
        droneOperation.setDescription(requestDto.description());
        droneOperation.setDrone(requestDto.drone());
        droneOperation.setFlightMode(requestDto.flightMode());
        droneOperation.setWeatherDescription(requestDto.weatherDescription());
        droneOperation.setKpIndex(requestDto.kpIndex());
        droneOperation.setTakeoffTime(requestDto.takeoffTime());
        droneOperation.setLandingTime(requestDto.landingTime());
        droneOperation.setFlightDurationSeconds(requestDto.flightDurationSeconds());

        Location location = locationService.getLocationById(requestDto.locationId());
        droneOperation.setLocation(location);

        droneOperationService.save(droneOperation);

        return ResponseEntity.status(HttpStatus.CREATED).body(droneOperation);
    }

    /**
     * Update an existing drone operation.
     * @param projectCode The project code.
     * @param operationCode The operation code.
     * @param requestDto The request DTO containing the updated operation data.
     * @return The updated drone operation wrapped in a ResponseEntity.
     */
    @PutMapping("/{operationCode}")
    public ResponseEntity<DroneOperation> update(
            @PathVariable String projectCode,
            @PathVariable String operationCode,
            @RequestBody DroneOperationRequestDto requestDto
    ) {
        Project project = projectService.getProjectByCode(projectCode);
        DroneOperation existing = droneOperationService.getByCode(operationCode);

        validateOperationBelongsToProject(existing, project);

        existing.setName(requestDto.name());
        existing.setObjective(requestDto.objective());
        existing.setDate(requestDto.date());
        existing.setDescription(requestDto.description());
        existing.setLocation(requestDto.locationId() != null ? locationService.getLocationById(requestDto.locationId()) : null);
        existing.setDrone(requestDto.drone());
        existing.setFlightMode(requestDto.flightMode());
        existing.setWeatherDescription(requestDto.weatherDescription());
        existing.setKpIndex(requestDto.kpIndex());
        existing.setTakeoffTime(requestDto.takeoffTime());
        existing.setLandingTime(requestDto.landingTime());
        existing.setFlightDurationSeconds(requestDto.flightDurationSeconds());

        droneOperationService.save(existing);

        return ResponseEntity.ok(existing);
    }

    /**
     * Delete a drone operation.
     * @param projectCode The project code.
     * @param operationCode The operation code.
     * @return A ResponseEntity indicating success.
     */
    @DeleteMapping("/{operationCode}")
    public ResponseEntity<Void> delete(
            @PathVariable String projectCode,
            @PathVariable String operationCode
    ) {
        Project project = projectService.getProjectByCode(projectCode);
        DroneOperation droneOperation = droneOperationService.getByCode(operationCode);

        validateOperationBelongsToProject(droneOperation, project);

        droneOperationService.delete(droneOperation);

        return ResponseEntity.noContent().build();
    }

    /**
     * Validate that the given drone operation belongs to the specified project.
     * @param droneOperation The drone operation to validate.
     * @param project The project to check against.
     */
    private void validateOperationBelongsToProject(DroneOperation droneOperation, Project project) {
        if (droneOperation.getProject() == null ||
                !droneOperation.getProject().getId().equals(project.getId())) {
            throw new IllegalArgumentException("Drone operation does not belong to the specified project.");
        }
    }
}