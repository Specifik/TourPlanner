package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.viewmodel.MainWindowViewModel;
import at.fhtw.tourplanner.viewmodel.SearchBarViewModel;
import at.fhtw.tourplanner.viewmodel.TourDetailsViewModel;
import at.fhtw.tourplanner.viewmodel.TourLogsViewModel;
import at.fhtw.tourplanner.viewmodel.TourOverviewViewModel;
import at.fhtw.tourplanner.viewmodel.TourLogDetailsViewModel;

public class ControllerFactory {
    private final MainWindowViewModel mainWindowViewModel;
    private final SearchBarViewModel searchBarViewModel;
    private final TourOverviewViewModel tourOverviewViewModel;
    private final TourDetailsViewModel tourDetailsViewModel;
    private final TourLogsViewModel tourLogsViewModel;
    private final TourLogDetailsViewModel tourLogDetailsViewModel;

    public ControllerFactory() {
        searchBarViewModel = new SearchBarViewModel();
        tourOverviewViewModel = new TourOverviewViewModel();
        tourDetailsViewModel = new TourDetailsViewModel();
        tourLogsViewModel = new TourLogsViewModel();
        tourLogDetailsViewModel = new TourLogDetailsViewModel();
        mainWindowViewModel = new MainWindowViewModel(
                searchBarViewModel,
                tourOverviewViewModel,
                tourDetailsViewModel,
                tourLogsViewModel
        );
    }

    //
    // Factory-Method Pattern
    //
    public Object create(Class<?> controllerClass) {
        if (controllerClass == MainWindowController.class) {
            return new MainWindowController(mainWindowViewModel);
        } else if (controllerClass == SearchBarController.class) {
            return new SearchBarController(searchBarViewModel);
        } else if (controllerClass == TourDetailsController.class) {
            return new TourDetailsController(tourDetailsViewModel);
        } else if (controllerClass == TourOverviewController.class) {
            return new TourOverviewController(tourOverviewViewModel);
        } else if (controllerClass == TourLogsController.class) {
            return new TourLogsController(tourLogsViewModel);
        } else if (controllerClass == TourLogDetailsController.class) {
            return new TourLogDetailsController(tourLogDetailsViewModel);
        }
        throw new IllegalArgumentException("Unknown controller class: " + controllerClass);
    }


    //
    // Singleton-Pattern with early-binding
    //
    private static final ControllerFactory instance = new ControllerFactory();

    public static ControllerFactory getInstance() {
        return instance;
    }

}