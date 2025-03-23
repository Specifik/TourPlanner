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
        tourList.setItems(tourOverviewViewModel.getObservableTours());

        tourList.getSelectionModel().selectedItemProperty().addListener(tourOverviewViewModel.getChangeListener());
    }

    @FXML
    public void onButtonAdd(ActionEvent event) {
        System.out.println("Add button clicked");
        Tour newTour = tourOverviewViewModel.addNewTour();

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
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Delete");
            confirmDialog.setHeaderText("Delete Tour");
            confirmDialog.setContentText("Are you sure you want to delete the tour '" + selectedTour.getName() + "'?");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                tourOverviewViewModel.deleteTour(selectedTour);

                Platform.runLater(() -> {
                    tourList.refresh();
                    if (!tourOverviewViewModel.getObservableTours().isEmpty()) {
                        tourList.getSelectionModel().select(0);
                    }
                });
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Tour Selected");
            alert.setContentText("Please select a tour to delete.");
            alert.showAndWait();
        }
    }
}