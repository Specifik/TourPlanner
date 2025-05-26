package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.bl.BL;
import at.fhtw.tourplanner.model.Tour;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainWindowViewModel {

    private static final Logger logger = LogManager.getLogger(MainWindowViewModel.class);

    private SearchBarViewModel searchBarViewModel;
    private TourOverviewViewModel tourOverviewViewModel;
    private TourDetailsViewModel tourDetailsViewModel;
    private TourLogsViewModel tourLogsViewModel;

    public MainWindowViewModel(SearchBarViewModel searchBarViewModel,
                               TourOverviewViewModel tourOverviewViewModel,
                               TourDetailsViewModel tourDetailsViewModel,
                               TourLogsViewModel tourLogsViewModel) {
        this.searchBarViewModel = searchBarViewModel;
        this.tourOverviewViewModel = tourOverviewViewModel;
        this.tourDetailsViewModel = tourDetailsViewModel;
        this.tourLogsViewModel = tourLogsViewModel;

        logger.info("MainWindowViewModel initialized");
        setupListeners();
    }

    private void setupListeners() {
        this.searchBarViewModel.addSearchListener((searchText, searchScope) -> {
            logger.info("Search initiated: '{}' in scope: {}", searchText, searchScope);
            searchTours(searchText, searchScope);
        });

        this.tourOverviewViewModel.addSelectionChangedListener(selectedTour -> {
            if (selectedTour != null) {
                logger.debug("Tour selected: {}", selectedTour.getName());
            }
            selectTour(selectedTour);
            if (selectedTour != null) {
                tourLogsViewModel.setCurrentTour(selectedTour);
            }
        });

        this.tourDetailsViewModel.addTourUpdatedListener(updatedTour -> {
            logger.info("Tour updated: {}", updatedTour.getName());
            tourOverviewViewModel.handleTourUpdated(updatedTour);
        });
    }

    private void selectTour(Tour selectedTour) {
        tourDetailsViewModel.setTourModel(selectedTour);
    }

    private void searchTours(String searchText, String searchScope) {
        Platform.runLater(() -> {
            try {
                var tours = BL.getInstance().findMatchingTours(searchText, searchScope);
                tourOverviewViewModel.setTours(tours);
                logger.debug("Search completed, {} tours found", tours.size());
            } catch (Exception e) {
                logger.error("Search failed for term: '{}'", searchText, e);
            }
        });
    }
}