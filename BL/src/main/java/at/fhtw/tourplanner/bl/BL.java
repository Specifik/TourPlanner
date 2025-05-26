package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.dal.TourDao;
import at.fhtw.tourplanner.model.Tour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BL {

    private static final Logger logger = LogManager.getLogger(BL.class);

    private final TourService tourService = new TourService();
    private static boolean initialized = false;
    private static final BL instance = new BL();

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
}