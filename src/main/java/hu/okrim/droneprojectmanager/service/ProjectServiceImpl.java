package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.model.Project;
import hu.okrim.droneprojectmanager.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    public Project getProjectById(UUID id) {
        return projectRepository.getReferenceById(id);
    }

    @Override
    public Project getProjectByCode(String code) {
        Project project = projectRepository.findByCode(code);
        if (project == null) {
            throw new RuntimeException("Project with code " + code + " does not exist.");
        }
        return project;
    }

    @Override
    public Page<Project> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    @Override
    public void saveProject(Project project) {
        projectRepository.save(project);
    }

    @Override
    public void deleteProject(Project project) {

    }
}
