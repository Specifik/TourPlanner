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
    @FXML private TourOverviewController mediaOverviewController;    // injected controller of TourOverview.fxml
    @FXML private TourDetailsController mediaDetailsController;    // injected controller of TourDetails.fxml

    private final MainWindowViewModel mainViewModel;

    public MainWindowController(MainWindowViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public MainWindowViewModel getMainViewModel() {
        return mainViewModel;
    }

    @FXML void initialize() {
    }

    public void onMenuFileQuitClicked(ActionEvent actionEvent) {
        Platform.exit();
    }
    public void onMenuHelpAboutClicked(ActionEvent actionEvent) {
        Alert aboutBox = new Alert(Alert.AlertType.INFORMATION, "Semesterproject BIF4-SWE2\nby Bernhard Wallisch");
        aboutBox.setTitle("About TourPlanner");
        aboutBox.showAndWait();
    }
}
