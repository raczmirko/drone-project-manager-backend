package hu.okrim.droneprojectmanager.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "operation_image_metadata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroneOperationImageMetadata extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "operation_id", nullable = false)
    private DroneOperation operation;

    @Column(name = "original_filename", nullable = false, length = 512)
    private String originalFilename;

    @Column(name = "mime_type", length = 128)
    private String mimeType;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "image_width")
    private Integer imageWidth;

    @Column(name = "image_height")
    private Integer imageHeight;

    @Column(name = "captured_at")
    private LocalDateTime capturedAt;

    @Column(name = "gps_latitude")
    private Double gpsLatitude;

    @Column(name = "gps_longitude")
    private Double gpsLongitude;

    @Column(name = "gps_altitude")
    private Double gpsAltitude;

    @Column(name = "camera_make", length = 255)
    private String cameraMake;

    @Column(name = "camera_model", length = 255)
    private String cameraModel;

    @Column(name = "orientation")
    private Integer orientation;

    @Column(name = "focal_length")
    private Double focalLength;

    @Column(name = "iso_value")
    private Integer isoValue;

    @Column(name = "aperture")
    private Double aperture;

    @Column(name = "exposure_time", length = 64)
    private String exposureTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "metadata_status", nullable = false, length = 32)
    private OperationImageMetadataStatus metadataStatus;

    @Column(name = "metadata_error")
    private String metadataError;
}