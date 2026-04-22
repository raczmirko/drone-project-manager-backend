package hu.okrim.droneprojectmanager.repository;

import hu.okrim.droneprojectmanager.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    Project findByCode(String code);
}
