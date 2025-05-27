package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ImportExportServiceTest {

    private ImportExportService importExportService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        importExportService = new ImportExportService();
    }

    @Test
    void testExportTours() throws IOException {
        // Arrange
        Tour tour1 = new Tour(1, "Vienna Walk", "A", "B", "Walking", "Nice walk");
        Tour tour2 = new Tour(2, "Graz Tour", "C", "D", "Car", "City tour");
        List<Tour> tours = Arrays.asList(tour1, tour2);
        String filePath = tempDir.resolve("test_export.json").toString();

        // Act
        importExportService.exportTours(tours, filePath);

        // Assert
        File exportedFile = new File(filePath);
        assertTrue(exportedFile.exists());
        assertTrue(exportedFile.length() > 0);

        String content = Files.readString(exportedFile.toPath());
        assertTrue(content.contains("Vienna Walk"));
        assertTrue(content.contains("Graz Tour"));
    }

    @Test
    void testExportEmptyToursList() throws IOException {
        // Arrange
        List<Tour> emptyTours = Arrays.asList();
        String filePath = tempDir.resolve("empty_export.json").toString();

        // Act
        importExportService.exportTours(emptyTours, filePath);

        // Assert
        File exportedFile = new File(filePath);
        assertTrue(exportedFile.exists());
        String content = Files.readString(exportedFile.toPath());
        assertEquals("[ ]", content);
    }

    @Test
    void testExportNullToursList() throws IOException {
        // Arrange
        String filePath = tempDir.resolve("null_export.json").toString();

        // Act
        importExportService.exportTours(null, filePath);

        // Assert
        File exportedFile = new File(filePath);
        assertTrue(exportedFile.exists());
        String content = Files.readString(exportedFile.toPath());
        assertEquals("[ ]", content);
    }

    @Test
    void testImportTours() throws IOException {
        // Arrange
        String jsonContent = """
            [
              {
                "id": 1,
                "name": "Test Tour",
                "from": "Start",
                "to": "End",
                "transportType": "Walking",
                "description": "Test description"
              }
            ]
            """;
        Path testFile = tempDir.resolve("test_import.json");
        Files.writeString(testFile, jsonContent);

        // Act
        List<Tour> result = importExportService.importTours(testFile.toString());

        // Assert
        assertEquals(1, result.size());
        Tour importedTour = result.get(0);
        assertEquals(0, importedTour.getId()); // Should be reset to 0
        assertEquals("Test Tour", importedTour.getName());
        assertEquals("Start", importedTour.getFrom());
        assertEquals("End", importedTour.getTo());
    }

    @Test
    void testImportNonExistentFile() throws IOException {
        // Arrange
        String nonExistentFile = tempDir.resolve("does_not_exist.json").toString();

        // Act
        List<Tour> result = importExportService.importTours(nonExistentFile);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testImportEmptyFile() throws IOException {
        // Arrange
        Path emptyFile = tempDir.resolve("empty.json");
        Files.createFile(emptyFile);

        // Act
        List<Tour> result = importExportService.importTours(emptyFile.toString());

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testImportToursWithLogs() throws IOException {
        // Arrange
        String jsonContent = """
            [
              {
                "id": 1,
                "name": "Tour with Logs",
                "from": "A",
                "to": "B",
                "transportType": "Walking",
                "tourLogs": [
                  {
                    "id": 1,
                    "dateTime": "2023-07-15T10:30:00",
                    "comment": "Great day",
                    "difficulty": "Easy",
                    "totalDistance": 5.0,
                    "totalTime": 60,
                    "rating": 5
                  }
                ]
              }
            ]
            """;
        Path testFile = tempDir.resolve("tours_with_logs.json");
        Files.writeString(testFile, jsonContent);

        // Act
        List<Tour> result = importExportService.importTours(testFile.toString());

        // Assert
        assertEquals(1, result.size());
        Tour tour = result.get(0);
        assertEquals(0, tour.getId()); // Reset to 0
        assertNotNull(tour.getTourLogs());
        assertEquals(1, tour.getTourLogs().size());

        TourLog log = tour.getTourLogs().get(0);
        assertEquals(0, log.getId()); // Reset to 0
        assertEquals("Great day", log.getComment());
        assertEquals(tour, log.getTour()); // Should be properly linked
    }
}