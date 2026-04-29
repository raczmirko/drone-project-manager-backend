package hu.okrim.droneprojectmanager.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OperationImageMetadataDashboardResponse(
        List<AltitudeProfilePointDto> altitudeProfile,
        List<AltitudeDistributionBucketDto> altitudeDistribution,
        List<DistanceAltitudePointDto> distanceAltitudeProfile,
        List<GroundTrackPointDto> groundTrack
) {

    public record AltitudeProfilePointDto(
            int sequence,
            LocalDateTime capturedAt,
            double altitude
    ) {}

    public record AltitudeDistributionBucketDto(
            String bucketLabel,
            long count
    ) {}

    public record DistanceAltitudePointDto(
            double distanceMeters,
            double altitude
    ) {}

    public record GroundTrackPointDto(
            int sequence,
            LocalDateTime capturedAt,
            double latitude,
            double longitude,
            Double altitude
    ) {}
}
