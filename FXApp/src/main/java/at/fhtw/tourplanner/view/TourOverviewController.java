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
        tourList.setItems(tourOverviewViewModel.getObservableTours());
        tourList.getSelectionModel().selectedItemProperty().addListener(tourOverviewViewModel.getChangeListener());
    }

    public void onButtonAdd(ActionEvent actionEvent) {
        tourOverviewViewModel.addNewTour();
        tourList.getSelectionModel().selectLast();
    }

    public void onButtonRemove(ActionEvent actionEvent) {
        tourOverviewViewModel.deleteTour(tourList.getSelectionModel().getSelectedItem());
    }
}
