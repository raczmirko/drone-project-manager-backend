package hu.okrim.droneprojectmanager.controller;

import hu.okrim.droneprojectmanager.dto.ProjectFileResponseDto;
import hu.okrim.droneprojectmanager.dto.ProjectRequestDto;
import hu.okrim.droneprojectmanager.model.Project;
import hu.okrim.droneprojectmanager.model.ProjectFile;
import hu.okrim.droneprojectmanager.service.ProjectFileService;
import hu.okrim.droneprojectmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

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
        project.setId(UUID.randomUUID());
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

    @GetMapping("/{projectCode}/files")
    public Page<ProjectFileResponseDto> getProjectFiles(
            @PathVariable String projectCode,
            @PageableDefault(size = 20) Pageable pageable)
    {
        // Fetch the project by code
        Project project = projectService.getProjectByCode(projectCode);

        // Fetch paginated project files
        Page<ProjectFile> projectFiles = projectFileService.findAllByProjectId(project.getId(), pageable);

        // Map ProjectFile entities to ProjectFileResponseDto
        return projectFiles.map(file -> new ProjectFileResponseDto(file.getId(), file.getFilename()));
    }

    @PostMapping("/{projectCode}/files")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadProjectFile(
            @PathVariable String projectCode,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        Project project = projectService.getProjectByCode(projectCode);

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty.");
        }

        // Throw an error if the file size exceeds 300MB
        if (file.getSize() > 300_000_000) {
            throw new IllegalArgumentException("Uploaded file is too large. Maximum size is 300MB.");
        }

        ProjectFile projectFile = new ProjectFile(
                UUID.randomUUID(),
                project,
                file.getOriginalFilename(),
                file.getBytes());

        projectFileService.saveProjectFile(projectFile);
    }

    @DeleteMapping("/documents/{documentId}")
    public void deleteProjectFile(@PathVariable UUID documentId)
    {
        projectFileService.deleteProjectFile(projectFileService.getProjectFileById(documentId));
    }

}
