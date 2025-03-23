package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.viewmodel.MainWindowViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class MainWindowController {
    // Controllers of included fxml-files are injected here
    // fx:id Attribute of <fx:include> tag + "Controller"
    // tutorial see https://riptutorial.com/javafx/example/7285/nested-controllers
    @FXML private SearchBarController searchBarController;    // injected controller of SearchBar.fxml
    @FXML private TourOverviewController tourOverviewController;
    @FXML private TourDetailsController tourDetailsController;
    @FXML private TourLogsController tourLogsController;

    private final MainWindowViewModel mainViewModel;

    public MainWindowController(MainWindowViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public MainWindowViewModel getMainViewModel() {
        return mainViewModel;
    }


    @FXML
    void initialize() {
        // This method is called after all @FXML fields have been initialized

        // Make sure the tour logs view model is updated when a tour is selected
        if (tourLogsController != null && tourOverviewController != null) {
            tourOverviewController.getTourOverviewViewModel().addSelectionChangedListener(
                    selectedTour -> tourLogsController.getTourLogsViewModel().setCurrentTour(selectedTour)
            );
        }
    }

    public void onMenuFileQuitClicked(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void onMenuHelpAboutClicked(ActionEvent actionEvent) {
        Alert aboutBox = new Alert(Alert.AlertType.INFORMATION, "TourPlanner");
        aboutBox.setTitle("About TourPlanner");
        aboutBox.showAndWait();
    }
}
