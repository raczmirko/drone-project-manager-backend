package hu.okrim.droneprojectmanager.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Response DTO for image metadata page.
 */
@Value
@Builder
public class OperationImageMetadataPageResponse {
    List<OperationImageMetadataListItemResponse> content;
    long totalElements;
    int page;
    int size;
}