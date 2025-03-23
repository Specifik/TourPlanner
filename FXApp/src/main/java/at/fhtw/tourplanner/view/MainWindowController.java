package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.FXMLDependencyInjection;
import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import at.fhtw.tourplanner.viewmodel.MainWindowViewModel;
import at.fhtw.tourplanner.viewmodel.TourLogDetailsViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainWindowController {
    // Controllers of included fxml-files are injected here
    @FXML private SearchBarController searchBarController;
    @FXML private TourOverviewController tourOverviewController;
    @FXML private TourDetailsController tourDetailsController;
    @FXML private TourLogsController tourLogsController;
    @FXML private TabPane detailsTabPane;

    private final MainWindowViewModel mainWindowViewModel;
    private Tab tourLogTab = null;
    private TourLogDetailsController tourLogDetailsController = null;

    public MainWindowController(MainWindowViewModel mainWindowViewModel) {
        this.mainWindowViewModel = mainWindowViewModel;
    }

    public MainWindowViewModel getMainWindowViewModel() {
        return mainWindowViewModel;
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

        // Add listener for opening tour log details
        if (tourLogsController != null) {
            tourLogsController.getTourLogsViewModel().addTourLogDetailsOpenListener(
                    this::showTourLogDetails
            );

            // Add listener for closing tour log details
            tourLogsController.getTourLogsViewModel().addTourLogDetailsCloseListener(
                    this::closeTourLogDetails
            );
        }

        // Add listener for tour updates to forcibly refresh the list view
        if (tourDetailsController != null && tourOverviewController != null) {
            tourDetailsController.getTourDetailsViewModel().addTourUpdatedListener(
                    this::handleTourUpdated
            );
        }
    }

    private void handleTourUpdated(Tour updatedTour) {
        // Force an immediate UI refresh
        Platform.runLater(() -> {
            tourOverviewController.getTourOverviewViewModel().handleTourUpdated(updatedTour);
            tourOverviewController.tourList.refresh();
        });
    }

    private void showTourLogDetails(TourLog tourLog) {
        try {
            if (tourLogTab == null) {
                // First time - create the tab and load the FXML using dependency injection
                FXMLLoader loader = FXMLDependencyInjection.getLoader("TourLogDetails.fxml", Locale.getDefault());
                Parent root = loader.load();
                tourLogDetailsController = loader.getController();

                // Add listener to refresh the logs list when a log is updated
                tourLogDetailsController.getViewModel().addLogUpdatedListener(
                        updatedLog -> tourLogsController.getTourLogsViewModel().handleLogUpdated(updatedLog)
                );

                // Don't close the tab when saving
                tourLogDetailsController.getViewModel().setAutoCloseOnSave(false);

                tourLogTab = new Tab("Tour Log Details", root);
                tourLogTab.setClosable(true);

                // Add listener to handle tab close
                tourLogTab.setOnClosed(event -> {
                    tourLogTab = null;
                    tourLogDetailsController = null;
                });

                detailsTabPane.getTabs().add(tourLogTab);
            }

            // Update the tab with the new tour log data
            updateTourLogTab(tourLog);

            // Select the tab
            detailsTabPane.getSelectionModel().select(tourLogTab);

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open tour log details");
            alert.setContentText("An error occurred while trying to open the tour log details: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void updateTourLogTab(TourLog tourLog) {
        if (tourLogTab != null && tourLogDetailsController != null) {
            // Update the tab title
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            tourLogTab.setText("Log: " + tourLog.getDateTime().toLocalDate().format(formatter));

            // Update the controller with the new tour log
            tourLogDetailsController.getViewModel().setTourLog(tourLog, false);
        }
    }

    private void closeTourLogDetails(TourLog tourLog) {
        if (tourLogTab != null) {
            detailsTabPane.getTabs().remove(tourLogTab);
            tourLogTab = null;
            tourLogDetailsController = null;
        }
    }

    public void onMenuFileQuitClicked(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void onMenuHelpAboutClicked(ActionEvent actionEvent) {
        Alert aboutBox = new Alert(Alert.AlertType.INFORMATION, "TourPlanner Application\nCreated for SWEN2");
        aboutBox.setTitle("About TourPlanner");
        aboutBox.showAndWait();
    }
}