package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.dal.TourDao;
import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class BL {

    private static final Logger logger = LogManager.getLogger(BL.class);

    private final TourService tourService = new TourService();
    private final TourLogService tourLogService = new TourLogService();
    private static boolean initialized = false;
    private static final BL instance = new BL();
    private final ReportService reportService = new ReportService();
    private final ImportExportService importExportService = new ImportExportService();

    private BL() {
        logger.info("Business Logic layer initialized");
    }

    public static BL getInstance() {
        return instance;
    }

    private void ensureInitialized() {
        if (!initialized) {
            try {
                tourService.initializeToursWithApiData();
                initialized = true;
            } catch (Exception e) {
                logger.error("Failed to initialize tours with API data", e);
                initialized = true; // Prevent repeated attempts
            }
        }
    }

    public List<Tour> getAllTours() {
        ensureInitialized();
        return tourService.getAllTours();
    }

    public Tour createTour() {
        return tourService.createTour();
    }

    public void updateTour(Tour tour) {
        tourService.updateTour(tour);
    }

    public void deleteTour(Tour tour) {
        tourService.deleteTour(tour);
    }

    public TourLog createTourLog(int tourId) {
        return tourLogService.createTourLog(tourId);
    }

    public List<TourLog> getTourLogsByTourId(int tourId) {
        return tourLogService.getTourLogsByTourId(tourId);
    }

    public Optional<TourLog> getTourLog(int id) {
        return tourLogService.getTourLog(id);
    }

    public void updateTourLog(TourLog tourLog) {
        tourLogService.updateTourLog(tourLog);
    }

    public void deleteTourLog(TourLog tourLog) {
        tourLogService.deleteTourLog(tourLog);
    }

    public List<TourLog> findTourLogsBySearchText(String searchText) {
        return tourLogService.findTourLogsBySearchText(searchText);
    }

    public List<TourLog> getAllTourLogs() {
        return tourLogService.getAllTourLogs();
    }

    public void refreshTourApiData(Tour tour) {
        tourService.refreshTourApiData(tour);
    }

    public List<Tour> findMatchingTours(String searchText) {
        return findMatchingTours(searchText, "All");
    }

    public List<Tour> findMatchingTours(String searchText, String searchScope) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAllTours();
        }

        String trimmedSearchText = searchText.trim();
        logger.info("Searching tours: '{}' in scope: {}", trimmedSearchText, searchScope);

        try {
            TourDao tourDao = (TourDao) DAL.getInstance().tourDao();

            if ("All".equals(searchScope)) {
                List<Tour> tourMatches = tourDao.findBySearchText(trimmedSearchText);
                List<Tour> logMatches = tourDao.findByTourLogSearchText(trimmedSearchText);

                Set<Tour> combinedResults = new HashSet<>(tourMatches);
                combinedResults.addAll(logMatches);

                List<Tour> results = new ArrayList<>(combinedResults);
                logger.info("Search found {} results", results.size());
                return results;

            } else if ("Tours Only".equals(searchScope)) {
                return tourDao.findBySearchText(trimmedSearchText);

            } else if ("Logs Only".equals(searchScope)) {
                return tourDao.findByTourLogSearchText(trimmedSearchText);
            }

            return new ArrayList<>();

        } catch (Exception e) {
            logger.error("Search failed for: '{}'", trimmedSearchText, e);
            return new ArrayList<>();
        }
    }

    public void generateTourReport(Tour tour, String outputPath) {
        try {
            reportService.generateTourReport(tour, outputPath);
            logger.info("Tour report generated: {}", outputPath);
        } catch (Exception e) {
            logger.error("Failed to generate tour report for: {}", tour.getName(), e);
            throw new RuntimeException("Report generation failed", e);
        }
    }

    public void generateSummaryReport(String outputPath) {
        try {
            reportService.generateSummaryReport(outputPath);
            logger.info("Summary report generated: {}", outputPath);
        } catch (Exception e) {
            logger.error("Failed to generate summary report", e);
            throw new RuntimeException("Summary report generation failed", e);
        }
    }

    public void exportAllTours(String filePath) throws IOException {
        logger.info("Attempting to export all tours to: {}", filePath);
        List<Tour> allTours = tourService.getAllToursWithLogs();
        if (allTours.isEmpty()) {
            logger.warn("No tours found to export.");
        }
        importExportService.exportTours(allTours, filePath);
        logger.info("Successfully initiated export of {} tours to: {}", allTours.size(), filePath);
    }

    public void importToursFromFile(String filePath) throws IOException {
        logger.info("Attempting to import tours from: {}", filePath);
        List<Tour> importedTours = importExportService.importTours(filePath);

        if (importedTours.isEmpty()) {
            logger.info("No tours were imported from the file: {}", filePath);
            return;
        }

        logger.info("Imported {} tours. Saving them to the database...", importedTours.size());
        TourDao tourDao = (TourDao) DAL.getInstance().tourDao();
        at.fhtw.tourplanner.dal.TourLogDao tourLogDao = DAL.getInstance().tourLogDao();

        for (Tour tour : importedTours) {
            Tour savedTour = tourDao.save(tour);
            logger.debug("Saved imported tour: {} with new ID: {}", savedTour.getName(), savedTour.getId());

            if (tour.getTourLogs() != null && !tour.getTourLogs().isEmpty()) {
                logger.debug("Saving {} logs for tour: {}", tour.getTourLogs().size(), savedTour.getName());
                for (TourLog log : tour.getTourLogs()) {
                    log.setTour(savedTour);
                    tourLogDao.save(log);
                    logger.trace("Saved log for tour {}: {}", savedTour.getName(), log.getComment());
                }
            }
        }
        logger.info("Successfully imported and saved {} tours from: {}", importedTours.size(), filePath);
    }
}