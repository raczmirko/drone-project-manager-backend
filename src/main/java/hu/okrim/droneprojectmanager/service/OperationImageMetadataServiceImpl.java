package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.dto.OperationFlightAnalysisResponse;
import hu.okrim.droneprojectmanager.dto.OperationImageMetadataExtractionResponse;
import hu.okrim.droneprojectmanager.dto.OperationImageMetadataListItemResponse;
import hu.okrim.droneprojectmanager.mapper.OperationImageMetadataMapper;
import hu.okrim.droneprojectmanager.model.DroneOperation;
import hu.okrim.droneprojectmanager.model.DroneOperationImageMetadata;
import hu.okrim.droneprojectmanager.model.OperationImageMetadataStatus;
import hu.okrim.droneprojectmanager.repository.DroneOperationImageMetadataRepository;
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
import java.util.List;

/**
 * Service class for handling image metadata operations.
 */
@Service
@RequiredArgsConstructor
public class OperationImageMetadataServiceImpl implements OperationImageMetadataService {

    private final DroneOperationRepository droneOperationRepository;
    private final DroneOperationImageMetadataRepository imageMetadataRepository;
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

        List<DroneOperationImageMetadata> entities = new ArrayList<>();
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

        List<DroneOperationImageMetadata> rows = imageMetadataRepository
                .findAllByOperationCodeOrderByCapturedAtAscCreatedAtAsc(operationCode)
                .stream()
                .filter(row -> row.getMetadataStatus() == OperationImageMetadataStatus.EXTRACTED)
                .toList();

        operation.setNumberOfRecordings(rows.size());

        LocalDateTime recordingStart = rows.stream()
                .map(DroneOperationImageMetadata::getCapturedAt)
                .filter(value -> value != null)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime recordingEnd = rows.stream()
                .map(DroneOperationImageMetadata::getCapturedAt)
                .filter(value -> value != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Double avgAltitude = rows.stream()
                .map(DroneOperationImageMetadata::getGpsAltitude)
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
    private Double calculateCumulativeDistanceMeters(List<DroneOperationImageMetadata> rows) {
        double total = 0.0;
        DroneOperationImageMetadata previous = null;

        for (DroneOperationImageMetadata current : rows) {
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

    private boolean hasGps(DroneOperationImageMetadata row) {
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