package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.viewmodel.TourOverviewViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.util.Optional;

public class TourOverviewController {
    @FXML
    public ListView<Tour> tourList;

    private final TourOverviewViewModel tourOverviewViewModel;

    public TourOverviewController(TourOverviewViewModel tourOverviewViewModel) {
        this.tourOverviewViewModel = tourOverviewViewModel;
    }

    public TourOverviewViewModel getTourOverviewViewModel() {
        return tourOverviewViewModel;
    }

    @FXML
    void initialize() {
        // Bind the ListView to the observable list
        tourList.setItems(tourOverviewViewModel.getObservableTours());

        // Set up the change listener for selection
        tourList.getSelectionModel().selectedItemProperty().addListener(tourOverviewViewModel.getChangeListener());
    }

    @FXML
    public void onButtonAdd(ActionEvent event) {
        System.out.println("Add button clicked");
        Tour newTour = tourOverviewViewModel.addNewTour();

        // Force a JavaFX thread update and select the new tour
        Platform.runLater(() -> {
            tourList.refresh();
            tourList.getSelectionModel().select(newTour);
            tourList.scrollTo(newTour);
        });
    }

    @FXML
    public void onButtonRemove(ActionEvent event) {
        Tour selectedTour = tourList.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            // Ask for confirmation
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Delete");
            confirmDialog.setHeaderText("Delete Tour");
            confirmDialog.setContentText("Are you sure you want to delete the tour '" + selectedTour.getName() + "'?");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // User confirmed, delete the tour
                tourOverviewViewModel.deleteTour(selectedTour);

                // Force a refresh
                Platform.runLater(() -> {
                    tourList.refresh();
                    // Select another item if available
                    if (!tourOverviewViewModel.getObservableTours().isEmpty()) {
                        tourList.getSelectionModel().select(0);
                    }
                });
            }
        } else {
            // No tour selected, show an error
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Tour Selected");
            alert.setContentText("Please select a tour to delete.");
            alert.showAndWait();
        }
    }
}