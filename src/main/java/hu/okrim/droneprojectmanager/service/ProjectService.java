package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProjectService {

    Project getProjectById(UUID id);

    Page<Project> getAllProjects(Pageable pageable);

    void saveProject(Project project);

    void deleteProject(Project project);

    Project getProjectByCode(String code);
}
