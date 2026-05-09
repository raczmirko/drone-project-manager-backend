package hu.okrim.droneprojectmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "project_files")
@AllArgsConstructor
@NoArgsConstructor
public class ProjectFile extends File {

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public ProjectFile(
            Project project,
            String filename,
            LocalDate uploadDate,
            Long size,
            byte[] binaryContent
    ) {
        this.project = project;
        this.filename = filename;
        this.uploadDate = uploadDate;
        this.size = size;
        this.binaryContent = binaryContent;
    }
}
