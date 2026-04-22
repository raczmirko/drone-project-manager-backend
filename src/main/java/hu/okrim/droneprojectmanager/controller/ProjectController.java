package hu.okrim.droneprojectmanager.controller;

import hu.okrim.droneprojectmanager.dto.ProjectRequestDto;
import hu.okrim.droneprojectmanager.model.Project;
import hu.okrim.droneprojectmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

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

    @GetMapping("/{code}")
    public Project getProjectByCode(@PathVariable String code) {
        return projectService.getProjectByCode(code);
    }

    @PostMapping
    public void createProject(@RequestBody ProjectRequestDto projectRequestDto) {
        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setName(projectRequestDto.name());
        project.setCode(projectRequestDto.code());
        project.setDescription(projectRequestDto.description());
        project.setObjective(projectRequestDto.objective());
        project.setStartDate(projectRequestDto.startDate());
        project.setEndDate(projectRequestDto.endDate());
        projectService.saveProject(project);
    }

    @DeleteMapping("/{code}")
    public void deleteProject(@PathVariable String code) {
        Project project = projectService.getProjectByCode(code);
        projectService.deleteProject(project);
    }

}
