package hu.okrim.droneprojectmanager.controller;

import hu.okrim.droneprojectmanager.dto.OperationFlightAnalysisResponse;
import hu.okrim.droneprojectmanager.dto.OperationImageMetadataExtractionResponse;
import hu.okrim.droneprojectmanager.dto.OperationImageMetadataPageResponse;
import hu.okrim.droneprojectmanager.service.OperationImageMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for handling image metadata operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/operations/{operationCode}/image-metadata")
public class OperationImageMetadataController {

    private final OperationImageMetadataService operationImageMetadataService;

    /**
     * Extracts image metadata from the uploaded files and persists it in the database.
     * @param operationCode The code of the operation to extract metadata for.
     * @param files The list of files to extract metadata from.
     * @return The response containing the extracted metadata.
     */
    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public OperationImageMetadataExtractionResponse extract(
            @PathVariable String operationCode,
            @RequestParam("files") List<MultipartFile> files
    ) {
        return operationImageMetadataService.extractAndPersist(operationCode, files);
    }

    /**
     * Get the page of image metadata for the operation.
     */
    @GetMapping
    public OperationImageMetadataPageResponse getPage(
            @PathVariable String operationCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return operationImageMetadataService.getPage(operationCode, page, size);
    }

    /**
     * Analyzes the operation and updates the flight analysis status.
     * @param operationCode The code of the operation to analyze.
     * @return The updated operation's flight analysis.
     */
    @PostMapping("/analyze")
    public OperationFlightAnalysisResponse analyze(@PathVariable String operationCode) {
        return operationImageMetadataService.analyzeAndUpdateOperation(operationCode);
    }
}