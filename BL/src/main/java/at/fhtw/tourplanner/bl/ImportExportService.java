package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.Tour;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImportExportService {

    private static final Logger logger = LogManager.getLogger(ImportExportService.class);
    private final ObjectMapper objectMapper;

    public ImportExportService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public void exportTours(List<Tour> tours, String filePath) throws IOException {
        if (tours == null || tours.isEmpty()) {
            logger.warn("No tours provided to export. Export operation cancelled.");
            objectMapper.writeValue(new File(filePath), List.of());
            return;
        }
        logger.info("Exporting {} tours to file: {}", tours.size(), filePath);
        try {
            objectMapper.writeValue(new File(filePath), tours);
            logger.info("Successfully exported tours to {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to export tours to file {}: {}", filePath, e.getMessage(), e);
            throw e;
        }
    }

    public List<Tour> importTours(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            logger.warn("Import file does not exist or is empty: {}", filePath);
            return List.of(); // Return empty list
        }
        logger.info("Importing tours from file: {}", filePath);
        try {
            List<Tour> importedTours = objectMapper.readValue(file, new TypeReference<List<Tour>>() {});
            
            // Reset IDs for tours so they are treated as new entities
            for (Tour tour : importedTours) {
                tour.setId(0); // Mark as new entity
                if (tour.getTourLogs() != null) {
                    for (at.fhtw.tourplanner.model.TourLog log : tour.getTourLogs()) {
                        log.setId(0); // Mark log as new entity
                        log.setTour(tour);
                    }
                }
            }
            logger.info("Successfully imported {} tours from {}", importedTours.size(), filePath);
            return importedTours;
        } catch (IOException e) {
            logger.error("Failed to import tours from file {}: {}", filePath, e.getMessage(), e);
            throw e;
        }
    }
} 