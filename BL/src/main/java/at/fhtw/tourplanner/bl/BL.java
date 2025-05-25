package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.dal.TourDao;
import at.fhtw.tourplanner.model.Tour;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BL {

    private final TourService tourService = new TourService();

    private static boolean initialized = false;

    private void ensureInitialized() {
        if (!initialized) {
            tourService.initializeToursWithApiData();
            initialized = true;
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
        System.out.println("Searching with scope: " + searchScope + ", text: " + searchText);

        if (searchText == null || searchText.trim().isEmpty()) {
            return getAllTours();
        }

        String trimmedSearchText = searchText.trim();

        // Use the enhanced DAO search methods
        TourDao tourDao = (TourDao) DAL.getInstance().tourDao();

        if ("All".equals(searchScope)) {
            // Search in tours
            List<Tour> tourMatches = tourDao.findBySearchText(trimmedSearchText);

            // Search in tour logs and get associated tours
            List<Tour> logMatches = tourDao.findByTourLogSearchText(trimmedSearchText);

            // Combine results and remove duplicates
            Set<Tour> combinedResults = new HashSet<>(tourMatches);
            combinedResults.addAll(logMatches);

            return new ArrayList<>(combinedResults);

        } else if ("Tours Only".equals(searchScope)) {
            return tourDao.findBySearchText(trimmedSearchText);

        } else if ("Logs Only".equals(searchScope)) {
            return tourDao.findByTourLogSearchText(trimmedSearchText);
        }

        return new ArrayList<>();
    }

    private static final BL instance = new BL();

    public static BL getInstance() {
        return instance;
    }
}