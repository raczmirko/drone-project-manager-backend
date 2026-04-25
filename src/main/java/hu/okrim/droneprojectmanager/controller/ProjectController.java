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
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
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
            @PageableDefault(size = 20) Pageable pageable) {
        // Fetch the project by code
        Project project = projectService.getProjectByCode(projectCode);

        // Fetch paginated project files
        Page<ProjectFile> projectFiles = projectFileService.findAllByProjectId(project.getId(), pageable);

        // Map ProjectFile entities to ProjectFileResponseDto
        return projectFiles.map(file -> new ProjectFileResponseDto(
                file.getId(),
                file.getFilename(),
                file.getUploadDate(),
                file.getSize()
        ));
    }

    @PostMapping("/{projectCode}/files")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> uploadProjectFile(
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
                project,
                file.getOriginalFilename(),
                LocalDate.now(),
                file.getSize(),
                file.getBytes()
        );

        projectFileService.saveProjectFile(projectFile);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/files/{documentId}")
    public void deleteProjectFile(@PathVariable UUID documentId) {
        projectFileService.deleteProjectFile(projectFileService.getProjectFileById(documentId));
    }

    @GetMapping("/files/{documentId}/download")
    public ResponseEntity<byte[]> downloadProjectFile(@PathVariable UUID documentId) {

        ProjectFile projectFile = projectFileService.getProjectFileById(documentId);

        if (projectFile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String fileName = projectFile.getFilename();
        byte[] fileContent = projectFile.getBinaryContent();

        if (fileContent == null || fileName == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Handle missing content or filename
        }

        // Encode file name to handle spaces, special characters, and non-ASCII characters
        String encodedFilename = UriUtils.encode(fileName, StandardCharsets.UTF_8);

        // Determine content type based on file's extension
        String contentType = URLConnection.guessContentTypeFromName(fileName);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Default to binary stream if unknown
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(encodedFilename)
                                .build()
                                .toString()
                )
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(fileContent.length)
                .body(fileContent);
    }
}
