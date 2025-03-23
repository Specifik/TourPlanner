package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.bl.BL;
import at.fhtw.tourplanner.model.Tour;
import javafx.application.Platform;

public class MainWindowViewModel {
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

        this.searchBarViewModel.addSearchListener((searchText, searchScope) -> {
            System.out.println("Searching for: " + searchText + " in scope: " + searchScope);
            searchTours(searchText, searchScope);
        });

        this.tourOverviewViewModel.addSelectionChangedListener(selectedTour -> {
            selectTour(selectedTour);
            if (selectedTour != null) {
                tourLogsViewModel.setCurrentTour(selectedTour);
            }
        });

        this.tourDetailsViewModel.addTourUpdatedListener(updatedTour -> {
            tourOverviewViewModel.handleTourUpdated(updatedTour);
        });
    }

    private void selectTour(Tour selectedTour) {
        tourDetailsViewModel.setTourModel(selectedTour);
    }

    private void searchTours(String searchText, String searchScope) {
        Platform.runLater(() -> {
            var tours = BL.getInstance().findMatchingTours(searchText, searchScope);
            tourOverviewViewModel.setTours(tours);
        });
    }
}