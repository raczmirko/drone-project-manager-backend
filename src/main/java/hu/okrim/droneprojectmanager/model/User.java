package hu.okrim.droneprojectmanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * The User class represents an entity that contains information about a system user.
 */
@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @Column(name = "account_number", nullable = false, unique = true)
    private Long accountNumber;

    @Column(name = "schema", nullable = false, unique = true)
    private UUID schema;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "registration_date", nullable = false, columnDefinition = "timestamp")
    private Instant registrationDate;
}