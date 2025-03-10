package at.fhtw.medialib.viewmodel;

import at.fhtw.medialib.bl.BL;
import at.fhtw.medialib.model.MediaItem;

public class MainWindowViewModel {
    private SearchBarViewModel searchBarViewModel;
    private MediaOverviewViewModel mediaOverviewViewModel;
    private MediaDetailsViewModel mediaDetailsViewModel;

    public MainWindowViewModel(SearchBarViewModel searchBarViewModel, MediaOverviewViewModel mediaOverviewViewModel, MediaDetailsViewModel mediaDetailsViewModel) {
        this.searchBarViewModel = searchBarViewModel;
        this.mediaOverviewViewModel = mediaOverviewViewModel;
        this.mediaDetailsViewModel = mediaDetailsViewModel;

        this.searchBarViewModel.addSearchListener(searchString->searchTours(searchString));
        // instead of the lambda-expression from above, you also can use the following "classical" event-handler implementation with anonymous inner classes
//        this.searchBarViewModel.addSearchListener(new SearchBarViewModel.SearchListener() {
//            @Override
//            public void search(String searchString) {
//                var tours = BL.getInstance().findMatchingTours( searchString );
//                toursOverviewViewModel.setTours(tours);
//            }
//        });

        this.mediaOverviewViewModel.addSelectionChangedListener(selectedTour->selectTour(selectedTour));
    }

    private void selectTour(MediaItem selectedMediaItem) {
        mediaDetailsViewModel.setTourModel(selectedMediaItem);
    }

    private void searchTours(String searchString) {
        var tours = BL.getInstance().findMatchingTours( searchString );
        mediaOverviewViewModel.setTours(tours);
    }
}
