package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.bl.BL;
import at.fhtw.tourplanner.model.Tour;

public class MainWindowViewModel {
    private SearchBarViewModel searchBarViewModel;
    private TourOverviewViewModel tourOverviewViewModel;
    private TourDetailsViewModel tourDetailsViewModel;

    public MainWindowViewModel(SearchBarViewModel searchBarViewModel, TourOverviewViewModel tourOverviewViewModel, TourDetailsViewModel tourDetailsViewModel) {
        this.searchBarViewModel = searchBarViewModel;
        this.tourOverviewViewModel = tourOverviewViewModel;
        this.tourDetailsViewModel = tourDetailsViewModel;

        this.searchBarViewModel.addSearchListener(searchString -> searchTours(searchString));
        // instead of the lambda-expression from above, you also can use the following "classical" event-handler implementation with anonymous inner classes
//        this.searchBarViewModel.addSearchListener(new SearchBarViewModel.SearchListener() {
//            @Override
//            public void search(String searchString) {
//                var tours = BL.getInstance().findMatchingTours( searchString );
//                toursOverviewViewModel.setTours(tours);
//            }
//        });

        this.tourOverviewViewModel.addSelectionChangedListener(selectedTour->selectTour(selectedTour));
    }

    private void selectTour(Tour selectedTour) {
        tourDetailsViewModel.setTourModel(selectedTour);
    }

    private void searchTours(String searchString) {
        var tours = BL.getInstance().findMatchingTours(searchString);
        tourOverviewViewModel.setTours(tours);
    }
}
