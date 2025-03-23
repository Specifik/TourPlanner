package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.model.TourLog;
import at.fhtw.tourplanner.viewmodel.TourLogsViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;

public class TourLogsController {
    @FXML
    private TableView<TourLog> tourLogsTable;

    @FXML
    private TableColumn<TourLog, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<TourLog, String> difficultyColumn;

    @FXML
    private TableColumn<TourLog, Integer> totalTimeColumn;

    @FXML
    private TableColumn<TourLog, Double> totalDistanceColumn;

    @FXML
    private TableColumn<TourLog, Integer> ratingColumn;

    private final TourLogsViewModel tourLogsViewModel;

    public TourLogsController(TourLogsViewModel tourLogsViewModel) {
        this.tourLogsViewModel = tourLogsViewModel;
    }

    public TourLogsViewModel getTourLogsViewModel() {
        return tourLogsViewModel;
    }

    @FXML
    void initialize() {
        // Configure the table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        totalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
        totalDistanceColumn.setCellValueFactory(new PropertyValueFactory<>("totalDistance"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        // Bind the table to the view model
        tourLogsTable.setItems(tourLogsViewModel.getObservableTourLogs());

        // Handle row selection
        tourLogsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        tourLogsViewModel.selectTourLog(newValue);
                    }
                }
        );

        // Handle double-click on row
        tourLogsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tourLogsTable.getSelectionModel().getSelectedItem() != null) {
                tourLogsViewModel.openTourLogDetails(tourLogsTable.getSelectionModel().getSelectedItem());
            }
        });
    }

    @FXML
    void onButtonAddLog(ActionEvent event) {
        tourLogsViewModel.addNewTourLog();
    }

    @FXML
    void onButtonRemoveLog(ActionEvent event) {
        tourLogsViewModel.deleteTourLog(tourLogsTable.getSelectionModel().getSelectedItem());
    }

    @FXML
    void onButtonEditLog(ActionEvent event) {
        tourLogsViewModel.editTourLog(tourLogsTable.getSelectionModel().getSelectedItem());
    }
}