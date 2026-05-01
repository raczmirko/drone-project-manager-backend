package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.dto.*;
import hu.okrim.droneprojectmanager.mapper.OperationImageMetadataMapper;
import hu.okrim.droneprojectmanager.model.DroneOperation;
import hu.okrim.droneprojectmanager.model.OperationImageMetadata;
import hu.okrim.droneprojectmanager.model.OperationImageMetadataStatus;
import hu.okrim.droneprojectmanager.repository.OperationImageMetadataRepository;
import hu.okrim.droneprojectmanager.repository.DroneOperationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service class for handling image metadata operations.
 */
@Service
@RequiredArgsConstructor
public class OperationImageMetadataServiceImpl implements OperationImageMetadataService {

    private final DroneOperationRepository droneOperationRepository;
    private final OperationImageMetadataRepository imageMetadataRepository;
    private final ImageMetadataExtractorService imageMetadataExtractorService;

    @Override
    @Transactional
    public OperationImageMetadataExtractionResponse extractAndPersist(String operationCode, List<MultipartFile> files) {
        DroneOperation operation = getOperation(operationCode);

        List<MultipartFile> safeFiles = files == null ? List.of() : files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .toList();

        if (safeFiles.isEmpty()) {
            return OperationImageMetadataExtractionResponse.builder()
                    .processedCount(0)
                    .extractedCount(0)
                    .errorCount(0)
                    .build();
        }

        List<OperationImageMetadata> entities = new ArrayList<>();
        for (MultipartFile file : safeFiles) {
            entities.add(imageMetadataExtractorService.extract(operation, file));
        }

        imageMetadataRepository.saveAll(entities);

        int errorCount = (int) entities.stream()
                .filter(entity -> entity.getMetadataStatus() == OperationImageMetadataStatus.ERROR)
                .count();

        return OperationImageMetadataExtractionResponse.builder()
                .processedCount(entities.size())
                .extractedCount(entities.size() - errorCount)
                .errorCount(errorCount)
                .build();
    }

    /**
     * Get a page of image metadata for a specific operation.
     * @param operationCode The code of the operation.
     * @param pageable The pagination information.
     * @return The page of image metadata.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OperationImageMetadataListItemResponse> getPage(String operationCode, Pageable pageable) {
        Sort defaultSort = Sort.by(Sort.Direction.DESC, "capturedAt")
                .and(Sort.by(Sort.Direction.DESC, "createdAt"));

        Pageable effectivePageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().isSorted() ? pageable.getSort() : defaultSort
        );

        return imageMetadataRepository.findByOperationCode(operationCode, effectivePageable)
                .map(OperationImageMetadataMapper::toListItemResponse);
    }

    /**
     * Analyzes the flight of a drone operation and updates the operation's metadata.
     * @param operationCode The code of the drone operation.
     * @return The updated operation's flight analysis.
     */
    @Override
    @Transactional
    public OperationFlightAnalysisResponse analyzeAndUpdateOperation(String operationCode) {
        DroneOperation operation = getOperation(operationCode);

        List<OperationImageMetadata> rows = imageMetadataRepository
                .findAllByOperationCodeOrderByCapturedAtAscCreatedAtAsc(operationCode)
                .stream()
                .filter(row -> row.getMetadataStatus() == OperationImageMetadataStatus.EXTRACTED)
                .toList();

        operation.setNumberOfRecordings(rows.size());

        LocalDateTime recordingStart = rows.stream()
                .map(OperationImageMetadata::getCapturedAt)
                .filter(value -> value != null)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime recordingEnd = rows.stream()
                .map(OperationImageMetadata::getCapturedAt)
                .filter(value -> value != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Double avgAltitude = rows.stream()
                .map(OperationImageMetadata::getGpsAltitude)
                .filter(value -> value != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(Double.NaN);

        Double cumulativeDistance = calculateCumulativeDistanceMeters(rows);

        operation.setRecordingStart(recordingStart);
        operation.setRecordingEnd(recordingEnd);
        operation.setFlightDurationSeconds(
                recordingStart != null && recordingEnd != null
                        ? Math.toIntExact(Duration.between(recordingStart, recordingEnd).getSeconds())
                        : null
        );
        operation.setAvgRecordingAltitude(Double.isNaN(avgAltitude) ? null : avgAltitude);
        operation.setRecordingLength(cumulativeDistance);

        droneOperationRepository.save(operation);

        return OperationImageMetadataMapper.toFlightAnalysisResponse(operation);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OperationFlightPathPointResponseDto> getFlightPath(String operationCode) {
        return imageMetadataRepository
                .findAllByOperationCodeOrderByCapturedAtAscCreatedAtAsc(operationCode)
                .stream()
                .filter(row -> row.getGpsLatitude() != null && row.getGpsLongitude() != null)
                .map(row -> new OperationFlightPathPointResponseDto(
                        row.getId(),
                        row.getCapturedAt(),
                        row.getGpsLatitude(),
                        row.getGpsLongitude()
                ))
                .toList();
    }

    /**
     * Get the dashboard data for a specific operation.
     * @param operationCode The code of the operation.
     * @return The dashboard data for the operation.
     */
    @Override
    public OperationImageMetadataDashboardResponse getDashboard(String operationCode) {
        List<OperationImageMetadata> metadata = imageMetadataRepository.findAllByOperationCodeOrderByCapturedAtAscCreatedAtAsc(operationCode);

        List<OperationImageMetadata> sorted = metadata.stream()
                .sorted(
                        Comparator.comparing(
                                        OperationImageMetadata::getCapturedAt,
                                        Comparator.nullsLast(LocalDateTime::compareTo)
                                )
                                .thenComparing(OperationImageMetadata::getCreatedAt)
                )
                .toList();

        List<OperationImageMetadataDashboardResponse.AltitudeProfilePointDto> altitudeProfile = buildAltitudeProfile(sorted);

        List<OperationImageMetadataDashboardResponse.AltitudeDistributionBucketDto> altitudeDistribution = buildAltitudeDistribution(sorted);

        List<OperationImageMetadataDashboardResponse.DistanceAltitudePointDto> distanceAltitudeProfile = buildDistanceAltitudeProfile(sorted);

        List<OperationImageMetadataDashboardResponse.GroundTrackPointDto> groundTrack = buildGroundTrack(sorted);

        return new OperationImageMetadataDashboardResponse(
                altitudeProfile,
                altitudeDistribution,
                distanceAltitudeProfile,
                groundTrack
        );
    }

    @Override
    @Transactional
    public void deleteAllOperationMetadata(String operationCode) {
        imageMetadataRepository.deleteByOperationCode(operationCode);
    }

    /**
     * Builds an altitude profile based on the provided list of operation image metadata.
     *
     * @param sorted a list of OperationImageMetadata objects sorted by capture time
     * @return a list of AltitudeProfilePointDto objects representing the altitude profile
     */
    private List<OperationImageMetadataDashboardResponse.AltitudeProfilePointDto> buildAltitudeProfile(
            List<OperationImageMetadata> sorted
    ) {
        List<OperationImageMetadataDashboardResponse.AltitudeProfilePointDto> result = new ArrayList<>();
        int sequence = 1;

        for (OperationImageMetadata item : sorted) {
            if (item.getGpsAltitude() == null) {
                continue;
            }

            result.add(new OperationImageMetadataDashboardResponse.AltitudeProfilePointDto(
                    sequence++,
                    item.getCapturedAt(),
                    item.getGpsAltitude()
            ));
        }

        return result;
    }

    /**
     * Builds a distribution of altitude ranges based on a sorted list of operation image metadata.
     * Each range is represented as a bucket with a label and count of occurrences.
     *
     * @param sorted the list of OperationImageMetadata objects sorted by their respective attributes
     * @return a list of AltitudeDistributionBucketDto instances, each representing a range of altitude values and the number of occurrences in that range
     */
    private List<OperationImageMetadataDashboardResponse.AltitudeDistributionBucketDto> buildAltitudeDistribution(
            List<OperationImageMetadata> sorted
    ) {
        long below20 = 0;
        long from20To40 = 0;
        long from40To60 = 0;
        long from60To80 = 0;
        long from80To100 = 0;
        long above100 = 0;

        for (OperationImageMetadata item : sorted) {
            Double altitude = item.getGpsAltitude();
            if (altitude == null) {
                continue;
            }

            if (altitude < 20) {
                below20++;
            } else if (altitude < 40) {
                from20To40++;
            } else if (altitude < 60) {
                from40To60++;
            } else if (altitude < 80) {
                from60To80++;
            } else if (altitude < 100) {
                from80To100++;
            } else {
                above100++;
            }
        }

        return List.of(
                new OperationImageMetadataDashboardResponse.AltitudeDistributionBucketDto("< 20 m", below20),
                new OperationImageMetadataDashboardResponse.AltitudeDistributionBucketDto("20–40 m", from20To40),
                new OperationImageMetadataDashboardResponse.AltitudeDistributionBucketDto("40–60 m", from40To60),
                new OperationImageMetadataDashboardResponse.AltitudeDistributionBucketDto("60–80 m", from60To80),
                new OperationImageMetadataDashboardResponse.AltitudeDistributionBucketDto("80–100 m", from80To100),
                new OperationImageMetadataDashboardResponse.AltitudeDistributionBucketDto("100+ m", above100)
        );
    }

    /**
     * Builds a distance-altitude profile based on a sorted list of OperationImageMetadata.
     * Each point in the profile consists of cumulative distance and corresponding altitude.
     *
     * @param sorted a list of sorted OperationImageMetadata objects containing GPS coordinates and altitude data.
     * @return a list of DistanceAltitudePointDto objects representing the distance-altitude profile.
     */
    private List<OperationImageMetadataDashboardResponse.DistanceAltitudePointDto> buildDistanceAltitudeProfile(
            List<OperationImageMetadata> sorted
    ) {
        List<OperationImageMetadataDashboardResponse.DistanceAltitudePointDto> result = new ArrayList<>();
        OperationImageMetadata previous = null;
        double cumulativeDistanceMeters = 0.0;

        for (OperationImageMetadata item : sorted) {
            if (item.getGpsLatitude() == null || item.getGpsLongitude() == null || item.getGpsAltitude() == null) {
                continue;
            }

            if (previous != null
                    && previous.getGpsLatitude() != null
                    && previous.getGpsLongitude() != null) {
                cumulativeDistanceMeters += haversineMeters(
                        previous.getGpsLatitude(),
                        previous.getGpsLongitude(),
                        item.getGpsLatitude(),
                        item.getGpsLongitude()
                );
            }

            result.add(new OperationImageMetadataDashboardResponse.DistanceAltitudePointDto(
                    cumulativeDistanceMeters,
                    item.getGpsAltitude()
            ));

            previous = item;
        }

        return result;
    }

    /**
     * Builds a list of GroundTrackPointDto objects representing the ground track points based on sorted operation image metadata.
     * Each ground track point includes a sequence number, timestamp, GPS latitude, GPS longitude, and GPS altitude,
     * skipping entries where latitude or longitude is null.
     *
     * @param sortedMetadataList the list of sorted operation image metadata to process, where each item contains GPS and timestamp information
     * @return a list of GroundTrackPointDto objects representing the ground track points
     */
    private List<OperationImageMetadataDashboardResponse.GroundTrackPointDto> buildGroundTrack(
            List<OperationImageMetadata> sortedMetadataList
    ) {
        List<OperationImageMetadataDashboardResponse.GroundTrackPointDto> result = new ArrayList<>();
        int sequence = 1;

        for (OperationImageMetadata item : sortedMetadataList) {
            if (item.getGpsLatitude() == null || item.getGpsLongitude() == null) {
                continue;
            }

            result.add(new OperationImageMetadataDashboardResponse.GroundTrackPointDto(
                    sequence++,
                    item.getCapturedAt(),
                    item.getGpsLatitude(),
                    item.getGpsLongitude(),
                    item.getGpsAltitude()
            ));
        }

        return result;
    }

    /**
     * Get the drone operation by code.
     * @param operationCode The code of the drone operation.
     * @return The drone operation.
     */
    private DroneOperation getOperation(String operationCode) {
        return droneOperationRepository.findByCode(operationCode)
                .orElseThrow(() -> new EntityNotFoundException("Drone operation not found: " + operationCode));
    }

    /**
     * Calculates the cumulative distance traveled between all GPS points.
     * @param rows The list of image metadata rows.
     * @return The cumulative distance in meters, or null if no GPS points are present.
     */
    private Double calculateCumulativeDistanceMeters(List<OperationImageMetadata> rows) {
        double total = 0.0;
        OperationImageMetadata previous = null;

        for (OperationImageMetadata current : rows) {
            if (previous == null) {
                previous = current;
                continue;
            }

            if (hasGps(previous) && hasGps(current)) {
                total += haversineMeters(
                        previous.getGpsLatitude(),
                        previous.getGpsLongitude(),
                        current.getGpsLatitude(),
                        current.getGpsLongitude()
                );
            }

            previous = current;
        }

        return total == 0.0 ? null : total;
    }

    /**
     * Checks if the given row has GPS coordinates.
     * @param row The row to check.
     * @return True if the row has GPS coordinates, false otherwise.
     */
    private boolean hasGps(OperationImageMetadata row) {
        return row.getGpsLatitude() != null && row.getGpsLongitude() != null;
    }

    /**
     * Computes great-circle distance in meters between two GPS coordinates
     * using the Haversine formula.
     * <p>
     * Reference:
     * <a href="https://en.wikipedia.org/wiki/Haversine_formula">Haversine formula</a>
     */
    private double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        double earthRadiusMeters = 6_371_000.0;
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(deltaLon / 2)
                * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusMeters * c;
    }
}