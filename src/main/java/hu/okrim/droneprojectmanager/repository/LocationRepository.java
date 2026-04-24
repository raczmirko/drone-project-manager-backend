package hu.okrim.droneprojectmanager.repository;

import hu.okrim.droneprojectmanager.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
}
