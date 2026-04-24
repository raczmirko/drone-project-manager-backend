package hu.okrim.droneprojectmanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "locations")
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "gps_longitude", nullable = false)
    private Double gpsLongitude;

    @Column(name = "gps_latitude", nullable = false)
    private Double gpsLatitude;

    public Location(String name, Double gpsLongitude, Double gpsLatitude) {
        this.name = name;
        this.gpsLongitude = gpsLongitude;
        this.gpsLatitude = gpsLatitude;
    }
}