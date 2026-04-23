package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.model.Project;
import hu.okrim.droneprojectmanager.model.ProjectFile;
import hu.okrim.droneprojectmanager.repository.ProjectFileRepository;
import hu.okrim.droneprojectmanager.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectFileServiceImpl implements ProjectFileService {

    private final ProjectFileRepository projectFileRepository;

    @Override
    public Page<ProjectFile> findAllByProjectId(UUID projectId, Pageable pageable) {
        return projectFileRepository.findAllByProjectId(projectId, pageable);
    }

    @Override
    public ProjectFile getProjectFileById(UUID id) {
        return projectFileRepository.getReferenceById(id);
    }

    @Override
    public void saveProjectFile(ProjectFile projectFile) {
        projectFileRepository.save(projectFile);
    }

    @Override
    public void deleteProjectFile(ProjectFile projectFile) {
        projectFileRepository.delete(projectFile);
    }
}
