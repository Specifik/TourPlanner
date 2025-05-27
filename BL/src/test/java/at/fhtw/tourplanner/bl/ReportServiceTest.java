package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.dal.TourLogDao;
import at.fhtw.tourplanner.dal.Dao;
import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    private ReportService reportService;

    @Mock
    private DAL mockDAL;

    @Mock
    private Dao<Tour> mockTourDao;

    @Mock
    private TourLogDao mockTourLogDao;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        reportService = new ReportService();
    }

    @Test
    void testGenerateTourReport() throws IOException {
        // Arrange
        Tour tour = new Tour(1, "Vienna Walk", "Stephansplatz", "Karlsplatz", "Walking", "Nice city walk");
        tour.setTourDistance(5.5);
        tour.setEstimatedTime(60);

        List<TourLog> logs = Arrays.asList(
                createTourLog(1, tour, "Great weather", "Easy", 5.0, 50, 5),
                createTourLog(2, tour, "Rainy day", "Medium", 5.2, 65, 3)
        );

        String outputPath = tempDir.resolve("tour_report.pdf").toString();

        try (MockedStatic<DAL> dalMock = Mockito.mockStatic(DAL.class)) {
            dalMock.when(DAL::getInstance).thenReturn(mockDAL);
            when(mockDAL.tourLogDao()).thenReturn(mockTourLogDao);
            when(mockTourLogDao.getByTourId(tour.getId())).thenReturn(logs);

            // Act
            reportService.generateTourReport(tour, outputPath);

            // Assert
            File reportFile = new File(outputPath);
            assertTrue(reportFile.exists());
            assertTrue(reportFile.length() > 0);
            verify(mockTourLogDao).getByTourId(tour.getId());
        }
    }

    @Test
    void testGenerateTourReportWithNoLogs() throws IOException {
        // Arrange
        Tour tour = new Tour(1, "Empty Tour", "A", "B", "Walking", "No logs");
        List<TourLog> emptyLogs = Arrays.asList();
        String outputPath = tempDir.resolve("empty_tour_report.pdf").toString();

        try (MockedStatic<DAL> dalMock = Mockito.mockStatic(DAL.class)) {
            dalMock.when(DAL::getInstance).thenReturn(mockDAL);
            when(mockDAL.tourLogDao()).thenReturn(mockTourLogDao);
            when(mockTourLogDao.getByTourId(tour.getId())).thenReturn(emptyLogs);

            // Act
            reportService.generateTourReport(tour, outputPath);

            // Assert
            File reportFile = new File(outputPath);
            assertTrue(reportFile.exists());
            assertTrue(reportFile.length() > 0);
        }
    }

    @Test
    void testGenerateSummaryReport() throws IOException {
        // Arrange
        Tour tour1 = new Tour(1, "Tour 1", "A", "B", "Walking", "First tour");
        Tour tour2 = new Tour(2, "Tour 2", "C", "D", "Biking", "Second tour");
        List<Tour> tours = Arrays.asList(tour1, tour2);

        List<TourLog> logs1 = Arrays.asList(
                createTourLog(1, tour1, "Log 1", "Easy", 5.0, 60, 4),
                createTourLog(2, tour1, "Log 2", "Medium", 6.0, 70, 5)
        );
        List<TourLog> logs2 = Arrays.asList();

        String outputPath = tempDir.resolve("summary_report.pdf").toString();

        try (MockedStatic<DAL> dalMock = Mockito.mockStatic(DAL.class)) {
            dalMock.when(DAL::getInstance).thenReturn(mockDAL);
            when(mockDAL.tourDao()).thenReturn(mockTourDao);
            when(mockDAL.tourLogDao()).thenReturn(mockTourLogDao);
            when(mockTourDao.getAll()).thenReturn(tours);
            when(mockTourLogDao.getByTourId(1)).thenReturn(logs1);
            when(mockTourLogDao.getByTourId(2)).thenReturn(logs2);

            // Act
            reportService.generateSummaryReport(outputPath);

            // Assert
            File reportFile = new File(outputPath);
            assertTrue(reportFile.exists());
            assertTrue(reportFile.length() > 0);
            verify(mockTourDao).getAll();
            verify(mockTourLogDao).getByTourId(1);
            verify(mockTourLogDao).getByTourId(2);
        }
    }

    @Test
    void testGenerateSummaryReportWithNoTours() throws IOException {
        // Arrange
        List<Tour> emptyTours = Arrays.asList();
        String outputPath = tempDir.resolve("empty_summary_report.pdf").toString();

        try (MockedStatic<DAL> dalMock = Mockito.mockStatic(DAL.class)) {
            dalMock.when(DAL::getInstance).thenReturn(mockDAL);
            when(mockDAL.tourDao()).thenReturn(mockTourDao);
            when(mockTourDao.getAll()).thenReturn(emptyTours);

            // Act
            reportService.generateSummaryReport(outputPath);

            // Assert
            File reportFile = new File(outputPath);
            assertTrue(reportFile.exists());
            assertTrue(reportFile.length() > 0);
            verify(mockTourDao).getAll();
        }
    }

    private TourLog createTourLog(int id, Tour tour, String comment, String difficulty,
                                  double distance, int time, int rating) {
        TourLog log = new TourLog(tour, LocalDateTime.now(), comment, difficulty, distance, time, rating);
        log.setId(id);
        return log;
    }
}