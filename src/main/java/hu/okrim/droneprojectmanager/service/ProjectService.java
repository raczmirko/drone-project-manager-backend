package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for managing Project.
 */
public interface ProjectService {

    /**
     * Get project by id.
     * @param id The id of the project.
     * @return The project.
     */
    Project getProjectById(UUID id);

    /**
     * Get all projects.
     * @param pageable The pagination information.
     * @return A page of projects.
     */
    Page<Project> getAllProjects(Pageable pageable);

    /**
     * Save a project.
     * @param project The project to save.
     */
    void saveProject(Project project);

    /**
     * Delete a project.
     * @param project The project to delete.
     */
    void deleteProject(Project project);

    /**
     * Get project by code
     * @param code The code of the project
     * @return The project
     */
    Project getProjectByCode(String code);
}
