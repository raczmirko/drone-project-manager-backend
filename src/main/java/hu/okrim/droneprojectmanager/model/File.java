package hu.okrim.droneprojectmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * The File class represents an entity that contains information about a file.
 */

@Getter
@Setter
@MappedSuperclass
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    @Column(name = "filename", nullable = false)
    protected String filename;

    @Column(name = "upload_date", nullable = false)
    protected LocalDate uploadDate;

    @Column(name = "size", nullable = false)
    protected Long size;

    @Column(name = "binary_content", nullable = false, columnDefinition = "bytea")
    protected byte[] binaryContent;
}
