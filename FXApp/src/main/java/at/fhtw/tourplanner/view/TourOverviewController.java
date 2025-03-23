package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.viewmodel.TourOverviewViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

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

        // Bind the selected item bidirectionally
        tourOverviewViewModel.selectedTourProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal != tourList.getSelectionModel().getSelectedItem()) {
                tourList.getSelectionModel().select(newVal);
            }
        });
    }

    @FXML
    public void onButtonAdd(ActionEvent actionEvent) {
        tourOverviewViewModel.addNewTour();
        tourList.refresh(); // Force a UI refresh
    }

    @FXML
    public void onButtonRemove(ActionEvent actionEvent) {
        Tour selectedTour = tourList.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            tourOverviewViewModel.deleteTour(selectedTour);
            tourList.refresh(); // Force a UI refresh
        }
    }
}