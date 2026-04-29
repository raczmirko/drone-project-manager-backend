package hu.okrim.droneprojectmanager.service;

import hu.okrim.droneprojectmanager.model.DroneOperation;
import hu.okrim.droneprojectmanager.model.OperationImageMetadata;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for extracting image metadata.
 */
public interface ImageMetadataExtractorService {

    /**
     * Extracts image metadata from the given file.
     * @param operation The drone operation associated with the image
     * @param file The image file to extract metadata from
     * @return The extracted image metadata
     */
    OperationImageMetadata extract(DroneOperation operation, MultipartFile file);

}
