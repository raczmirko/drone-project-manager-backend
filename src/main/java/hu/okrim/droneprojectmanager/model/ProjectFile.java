package hu.okrim.droneprojectmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "project_files")
@AllArgsConstructor
@NoArgsConstructor
public class ProjectFile {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "upload_date", nullable = false)
    private LocalDate uploadDate;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "binary_content", nullable = false, columnDefinition = "bytea")
    private byte[] binaryContent;
}
