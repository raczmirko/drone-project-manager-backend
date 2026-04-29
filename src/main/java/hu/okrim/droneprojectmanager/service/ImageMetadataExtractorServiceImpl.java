package hu.okrim.droneprojectmanager.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import hu.okrim.droneprojectmanager.model.DroneOperation;
import hu.okrim.droneprojectmanager.model.OperationImageMetadata;
import hu.okrim.droneprojectmanager.model.OperationImageMetadataStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

/**
 * Service class for extracting image metadata from uploaded files, based on the metadata-extractor library.
 * @see <a href="https://github.com/drewnoakes/metadata-extractor">metadata-extractor</a>
 */
@Service
public class ImageMetadataExtractorServiceImpl implements ImageMetadataExtractorService {

    /**
     * Extracts image metadata from the given file.
     * @param operation The drone operation associated with the image
     * @param file The image file to extract metadata from
     * @return The extracted image metadata
     */
    @Override
    public OperationImageMetadata extract(DroneOperation operation, MultipartFile file) {
        OperationImageMetadata.OperationImageMetadataBuilder builder = OperationImageMetadata.builder()
                .operation(operation)
                .originalFilename(StringUtils.cleanPath(Optional.ofNullable(file.getOriginalFilename()).orElse("unknown-file")))
                .mimeType(file.getContentType())
                .fileSizeBytes(file.getSize())
                .metadataStatus(OperationImageMetadataStatus.EXTRACTED);

        try {
            byte[] bytes = file.getBytes();

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image != null) {
                builder.imageWidth(image.getWidth());
                builder.imageHeight(image.getHeight());
            }

            Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes));

            /*
            ExifIFD0Directory and ExifSubIFDDirectory are metadata-extractor representations of two EXIF metadata
            sections inside an image file, used to read different groups of EXIF tags.
             */
            ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            ExifSubIFDDirectory subIfd = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

            if (ifd0 != null) {
                builder.cameraMake(ifd0.getString(ExifIFD0Directory.TAG_MAKE));
                builder.cameraModel(ifd0.getString(ExifIFD0Directory.TAG_MODEL));
                builder.orientation(ifd0.getInteger(ExifIFD0Directory.TAG_ORIENTATION));
            }

            if (subIfd != null) {
                builder.capturedAt(toLocalDateTime(subIfd.getDateOriginal()));
                builder.isoValue(subIfd.getInteger(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
                builder.aperture(subIfd.getDoubleObject(ExifSubIFDDirectory.TAG_FNUMBER));
                builder.focalLength(subIfd.getDoubleObject(ExifSubIFDDirectory.TAG_FOCAL_LENGTH));
                builder.exposureTime(subIfd.getDescription(ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
            }

            if (gpsDirectory != null) {
                GeoLocation geoLocation = gpsDirectory.getGeoLocation();
                if (geoLocation != null && !geoLocation.isZero()) {
                    builder.gpsLatitude(geoLocation.getLatitude());
                    builder.gpsLongitude(geoLocation.getLongitude());
                }

                Double altitude = gpsDirectory.getDoubleObject(GpsDirectory.TAG_ALTITUDE);
                if (altitude != null) {
                    builder.gpsAltitude(altitude);
                }
            }
        } catch (Exception exception) {
            builder.metadataStatus(OperationImageMetadataStatus.ERROR);
            builder.metadataError(exception.getMessage());
        }

        return builder.build();
    }

    /**
     * Converts a Date object to a LocalDateTime object.
     * @param date The Date object to convert
     * @return The LocalDateTime object representing the date
     */
    private LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }

        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
