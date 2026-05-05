package hu.okrim.droneprojectmanager.controller;

import hu.okrim.droneprojectmanager.dto.FileResponseDto;
import hu.okrim.droneprojectmanager.model.DroneOperation;
import hu.okrim.droneprojectmanager.model.DroneOperationFile;
import hu.okrim.droneprojectmanager.model.Project;
import hu.okrim.droneprojectmanager.model.ProjectFile;
import hu.okrim.droneprojectmanager.service.DroneOperationFileService;
import hu.okrim.droneprojectmanager.service.DroneOperationService;
import hu.okrim.droneprojectmanager.service.ProjectFileService;
import hu.okrim.droneprojectmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    private final ProjectFileService projectFileService;
    private final DroneOperationFileService droneOperationFileService;
    private final ProjectService projectService;
    private final DroneOperationService droneOperationService;

    // ------------------- Project Files ---------------------

    /**
     * Get all files for a project.
     * @param projectCode The project code.
     * @param pageable The pagination information.
     * @return A page of files.
     */
    @GetMapping("/projects/{projectCode}/files")
    public Page<FileResponseDto> getProjectFiles(
            @PathVariable String projectCode,
            @PageableDefault(size = 20) Pageable pageable) {

        Project project = projectService.getProjectByCode(projectCode);


        Page<ProjectFile> projectFiles = projectFileService.findAllByProjectId(project.getId(), pageable);


        return projectFiles.map(file -> new FileResponseDto(
                file.getId(),
                file.getFilename(),
                file.getUploadDate(),
                file.getSize()
        ));
    }

    /**
     * Upload a file for a project.
     * @param projectCode The project code.
     * @param file The file to upload.
     * @return A ResponseEntity indicating success.
     * @throws IOException If an I/O error occurs during file upload.
     */
    @PostMapping("/projects/{projectCode}/files")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> uploadProjectFile(
            @PathVariable String projectCode,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        Project project = projectService.getProjectByCode(projectCode);

        if (file.isEmpty()) {
            log.error("Uploaded file is empty.");
            throw new IllegalArgumentException("Uploaded file is empty.");
        }

        // Throw an error if the file size exceeds 300MB
        if (file.getSize() > 300_000_000) {
            log.error("Uploaded file is too large. Maximum size is 300MB.");
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

    /**
     * Delete a file for a project.
     * @param documentId The ID of the file to delete.
     */
    @DeleteMapping("/project-files/{documentId}")
    public void deleteProjectFile(@PathVariable UUID documentId) {
        projectFileService.deleteProjectFile(projectFileService.getProjectFileById(documentId));
    }

    /**
     * Download a file for a project.
     * @param documentId The ID of the file to download.
     * @return A ResponseEntity containing the file content.
     */
    @GetMapping("/project-files/{documentId}")
    public ResponseEntity<byte[]> downloadProjectFile(@PathVariable UUID documentId) {

        ProjectFile projectFile = projectFileService.getProjectFileById(documentId);

        if (projectFile == null) {
            log.error("Project file not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String fileName = projectFile.getFilename();
        byte[] fileContent = projectFile.getBinaryContent();

        if (fileContent == null || fileName == null) {
            log.error("Missing content or filename for project file.");
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

    // ------------------- Operation Files ---------------------

    /**
     * Get all files for a drone operation.
     * @param operationCode The operation code.
     * @param pageable The pagination information.
     * @return A page of files.
     */
    @GetMapping("/operations/{operationCode}/files")
    public Page<FileResponseDto> getOperationFiles(
            @PathVariable String operationCode,
            @PageableDefault(size = 20) Pageable pageable) {

        DroneOperation operation = droneOperationService.getByCode(operationCode);

        if (operation == null) {
            log.error("Drone operation not found.");
            throw new IllegalArgumentException("Drone operation not found.");
        }

        Page<DroneOperationFile> operationFiles = droneOperationFileService.findAllByOperationId(operation.getId(), pageable);

        // Map ProjectFile entities to ProjectFileResponseDto
        return operationFiles.map(file -> new FileResponseDto(
                file.getId(),
                file.getFilename(),
                file.getUploadDate(),
                file.getSize()
        ));
    }

    /**
     * Upload a file for a drone operation.
     * @param operationCode The operation code.
     * @param file The file to upload.
     * @return A ResponseEntity indicating success.
     * @throws IOException If an I/O error occurs during file upload.
     */
    @PostMapping("/operations/{operationCode}/files")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> uploadOperationFile(
            @PathVariable String operationCode,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        DroneOperation operation = droneOperationService.getByCode(operationCode);

        if (file.isEmpty()) {
            log.error("Uploaded file is empty.");
            throw new IllegalArgumentException("Uploaded file is empty.");
        }

        // Throw an error if the file size exceeds 300MB
        if (file.getSize() > 300_000_000) {
            log.error("Uploaded file is too large. Maximum size is 300MB.");
            throw new IllegalArgumentException("Uploaded file is too large. Maximum size is 300MB.");
        }

        DroneOperationFile droneOperationFile = new DroneOperationFile(
                operation,
                file.getOriginalFilename(),
                LocalDate.now(),
                file.getSize(),
                file.getBytes()
        );

        droneOperationFileService.saveDroneOperationFile(droneOperationFile);

        return ResponseEntity.ok().build();
    }

    /**
     * Delete a file for a drone operation.
     * @param documentId The ID of the file to delete.
     */
    @DeleteMapping("/operation-files/{documentId}")
    public void deleteOperationFile(@PathVariable UUID documentId) {
        droneOperationFileService.deleteDroneOperationFile(droneOperationFileService.getDroneOperationFileById(documentId));
    }

    /**
     * Download a file for a drone operation.
     * @param documentId The ID of the file to download.
     * @return A ResponseEntity containing the file content.
     */
    @GetMapping("/operation-files/{documentId}")
    public ResponseEntity<byte[]> downloadOperationFile(@PathVariable UUID documentId) {

        DroneOperationFile droneOperationFile = droneOperationFileService.getDroneOperationFileById(documentId);

        if (droneOperationFile == null) {
            log.error("Drone operation file not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String fileName = droneOperationFile.getFilename();
        byte[] fileContent = droneOperationFile.getBinaryContent();

        if (fileContent == null || fileName == null) {
            log.error("Missing content or filename for drone operation file.");
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
