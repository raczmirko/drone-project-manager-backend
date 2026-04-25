package hu.okrim.droneprojectmanager.controller;

import hu.okrim.droneprojectmanager.dto.DroneOperationRequestDto;
import hu.okrim.droneprojectmanager.model.DroneOperation;
import hu.okrim.droneprojectmanager.model.Location;
import hu.okrim.droneprojectmanager.model.Project;
import hu.okrim.droneprojectmanager.service.DroneOperationFileService;
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

@RestController
@RequestMapping("/projects/{projectCode}/operations")
@RequiredArgsConstructor
public class DroneOperationController {

    private final DroneOperationService droneOperationService;
    private final DroneOperationFileService droneOperationFileService;
    private final ProjectService projectService;
    private final LocationService locationService;

    @GetMapping
    public Page<DroneOperation> getAll(
            @PathVariable String projectCode,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Project project = projectService.getProjectByCode(projectCode);
        return droneOperationService.getAll(project.getId(), pageable);
    }

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
        droneOperation.setObjective(requestDto.objective());
        droneOperation.setOperationDate(requestDto.operationDate());
        droneOperation.setDescription(requestDto.description());
        droneOperation.setDrone(requestDto.drone());
        droneOperation.setFlightMode(requestDto.flightMode());
        droneOperation.setWeatherDescription(requestDto.weatherDescription());
        droneOperation.setKpIndex(requestDto.kpIndex());
        droneOperation.setTakeoffTime(requestDto.takeoffTime());
        droneOperation.setLandingTime(requestDto.landingTime());
        droneOperation.setFlightLength(requestDto.flightLength());
        droneOperation.setFlightDuration(requestDto.flightDuration());

        Location location = locationService.getLocationById(requestDto.locationId());
        droneOperation.setLocation(location);

        droneOperationService.save(droneOperation);

        return ResponseEntity.status(HttpStatus.CREATED).body(droneOperation);
    }

    @PutMapping("/{operationCode}")
    public ResponseEntity<DroneOperation> update(
            @PathVariable String projectCode,
            @PathVariable String operationCode,
            @RequestBody DroneOperation request
    ) {
        Project project = projectService.getProjectByCode(projectCode);
        DroneOperation existing = droneOperationService.getByCode(operationCode);

        validateOperationBelongsToProject(existing, project);

        existing.setCode(request.getCode());
        existing.setName(request.getName());
        existing.setObjective(request.getObjective());
        existing.setOperationDate(request.getOperationDate());
        existing.setDescription(request.getDescription());
        existing.setLocation(request.getLocation());
        existing.setDrone(request.getDrone());
        existing.setFlightMode(request.getFlightMode());
        existing.setWeatherDescription(request.getWeatherDescription());
        existing.setKpIndex(request.getKpIndex());
        existing.setTakeoffTime(request.getTakeoffTime());
        existing.setLandingTime(request.getLandingTime());
        existing.setFlightLength(request.getFlightLength());
        existing.setFlightDuration(request.getFlightDuration());
        existing.setAvgRecordingAltitude(request.getAvgRecordingAltitude());
        existing.setRecordingLength(request.getRecordingLength());
        existing.setRecordingStart(request.getRecordingStart());
        existing.setRecordingEnd(request.getRecordingEnd());
        existing.setNumberOfRecordings(request.getNumberOfRecordings());
        existing.setProject(project);

        droneOperationService.save(existing);

        return ResponseEntity.ok(existing);
    }

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

    private void validateOperationBelongsToProject(DroneOperation droneOperation, Project project) {
        if (droneOperation.getProject() == null ||
                !droneOperation.getProject().getId().equals(project.getId())) {
            throw new IllegalArgumentException("Drone operation does not belong to the specified project.");
        }
    }
}