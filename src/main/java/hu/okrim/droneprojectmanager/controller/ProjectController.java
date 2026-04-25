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

    @GetMapping("/{projectCode}")
    public Project getProjectByCode(@PathVariable String projectCode) {
        return projectService.getProjectByCode(projectCode);
    }

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

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteProject(@PathVariable String code) {
        Project project = projectService.getProjectByCode(code);
        projectService.deleteProject(project);
        return ResponseEntity.noContent().build();
    }
}
