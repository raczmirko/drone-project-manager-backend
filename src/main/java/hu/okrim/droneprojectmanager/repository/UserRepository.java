package hu.okrim.droneprojectmanager.repository;

import hu.okrim.droneprojectmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find a user by account number.
     */
    Optional<User> findByAccountNumber(Long accountNumber);
}
