package hu.okrim.droneprojectmanager.model;

import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "schema_name", nullable = false, unique = true)
    private String schemaName;

    @Column(name = "account_number", nullable = false, unique = true)
    private Long accountNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "registration_date", nullable = false, columnDefinition = "timestamp")
    private Instant registrationDate;
}