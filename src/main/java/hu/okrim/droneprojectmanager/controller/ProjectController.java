package hu.okrim.droneprojectmanager.controller;

import hu.okrim.droneprojectmanager.dto.ProjectRequestDto;
import hu.okrim.droneprojectmanager.model.Project;
import hu.okrim.droneprojectmanager.service.ProjectFileService;
import hu.okrim.droneprojectmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectFileService projectFileService;

    /**
     * Endpoint to fetch a paginated list of projects.
     *
     * @param pageable Spring Pageable object to define page and size
     * @return a paginated result of projects
     */
    @GetMapping
    public Page<Project> getAllProjects(@PageableDefault(size = 20) Pageable pageable) {
        return projectService.getAllProjects(pageable);
    }

    /**
     * Endpoint to fetch a project by its code.
     * @param projectCode The code of the project.
     * @return The project wrapped in a ResponseEntity.
     */
    @GetMapping("/{projectCode}")
    public Project getProjectByCode(@PathVariable String projectCode) {
        return projectService.getProjectByCode(projectCode);
    }

    /**
     * Endpoint to create a new project.
     * @param projectRequestDto The project data.
     */
    @PostMapping
    public void createProject(@RequestBody ProjectRequestDto projectRequestDto) {
        Project project = new Project();
        project.setName(projectRequestDto.name());
        project.setCode(projectRequestDto.code());
        project.setStatus(projectRequestDto.status());
        project.setDescription(projectRequestDto.description());
        project.setObjective(projectRequestDto.objective());
        project.setStartDate(projectRequestDto.startDate());
        project.setEndDate(projectRequestDto.endDate());
        projectService.saveProject(project);
    }

    /**
     * Endpoint to update an existing project.
     * @param projectCode The code of the project to update.
     * @param projectRequestDto The updated project data.
     */
    @PutMapping("/{projectCode}")
    public void updateProject(
            @PathVariable String projectCode,
            @RequestBody ProjectRequestDto projectRequestDto)
    {
        Project project = projectService.getProjectByCode(projectCode);
        project.setName(projectRequestDto.name());
        project.setStatus(projectRequestDto.status());
        project.setDescription(projectRequestDto.description());
        project.setObjective(projectRequestDto.objective());
        project.setStartDate(projectRequestDto.startDate());
        project.setEndDate(projectRequestDto.endDate());
        projectService.saveProject(project);
    }

    /**
     * Endpoint to delete a project by its code.
     * @param code The code of the project to delete.
     * @return 204 No Content
     */
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteProject(@PathVariable String code) {
        Project project = projectService.getProjectByCode(code);
        projectService.deleteProject(project);
        return ResponseEntity.noContent().build();
    }
}
