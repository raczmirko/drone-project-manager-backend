package hu.okrim.droneprojectmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "drone_operations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_drone_operations_code", columnNames = "code")
        }
)
public class DroneOperation extends BaseEntity {

    // -------- MANDATORY FIELDS --------

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "drone", nullable = false, length = 255)
    private String drone;

    // -------- OPTIONAL FIELDS --------

    @Column(name = "objective", columnDefinition = "TEXT")
    private String objective;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "flight_mode", length = 100)
    private String flightMode;

    @Column(name = "weather_description", columnDefinition = "TEXT")
    private String weatherDescription;

    @Column(name = "kp_index")
    private Double kpIndex;

    @Column(name = "takeoff_time")
    private LocalDateTime takeoffTime;

    @Column(name = "landing_time")
    private LocalDateTime landingTime;

    // -------- FLIGHT ANALYSIS FIELDS --------

    @Column(name = "flight_length")
    private Double flightLength;

    @Column(name = "flight_duration_seconds", columnDefinition = "interval")
    private Integer flightDurationSeconds;

    @Column(name = "avg_recording_altitude")
    private Double avgRecordingAltitude;

    @Column(name = "recording_length")
    private Double recordingLength;

    @Column(name = "recording_start")
    private LocalDateTime recordingStart;

    @Column(name = "recording_end")
    private LocalDateTime recordingEnd;

    @Column(name = "number_of_recordings")
    private Integer numberOfRecordings = 0;
}