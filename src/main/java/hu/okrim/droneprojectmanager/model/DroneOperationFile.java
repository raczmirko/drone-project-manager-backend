package hu.okrim.droneprojectmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "drone_operation_files")
@AllArgsConstructor
@NoArgsConstructor
public class DroneOperationFile extends File {

    @ManyToOne
    @JoinColumn(name = "drone_operation_id")
    private DroneOperation droneOperation;

    public DroneOperationFile(
            DroneOperation droneOperation,
            String filename,
            LocalDate uploadDate,
            Long size,
            byte[] binaryContent
    ) {
        this.droneOperation = droneOperation;
        this.filename = filename;
        this.uploadDate = uploadDate;
        this.size = size;
        this.binaryContent = binaryContent;
    }
}
