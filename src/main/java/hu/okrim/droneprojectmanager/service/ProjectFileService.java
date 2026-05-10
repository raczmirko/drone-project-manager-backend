package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.model.ProjectFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for managing ProjectFile.
 */
public interface ProjectFileService {

    /**
     * Find all project files by project id. Return a page of results.
     * @param projectId The project id.
     * @param pageable The pagination information.
     * @return A page of project files.
     */
    Page<ProjectFile> findAllByProjectId(UUID projectId, Pageable pageable);

    /**
     * Get project file by id
     * @param id The id of the project file
     * @return The project file
     */
    ProjectFile getProjectFileById(UUID id);

    /**
     * Save or update a project file.
     * @param projectFile The project file to save or update.
     */
    void saveProjectFile(ProjectFile projectFile);

    /**
     * Delete a project file.
     * @param projectFile The project file to delete.
     */
    void deleteProjectFile(ProjectFile projectFile);
}
