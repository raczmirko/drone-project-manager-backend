package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.model.Project;
import hu.okrim.droneprojectmanager.model.ProjectFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProjectFileService {
    Page<ProjectFile> findAllByProjectId(UUID projectId, Pageable pageable);
    ProjectFile getProjectFileById(UUID id);
    void saveProjectFile(ProjectFile projectFile);
    void deleteProjectFile(ProjectFile projectFile);
}
