package hu.okrim.droneprojectmanager.repository;

import hu.okrim.droneprojectmanager.model.ProjectFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, UUID> {
    Page<ProjectFile> findAllByProjectId(UUID projectId, Pageable pageable);
}
