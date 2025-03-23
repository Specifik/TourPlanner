package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.model.TourLog;
import at.fhtw.tourplanner.viewmodel.TourLogsViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @FXML
    private TableColumn<TourLog, String> commentColumn;

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
        // Custom cell factory for date formatting
        dateColumn.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        totalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
        totalDistanceColumn.setCellValueFactory(new PropertyValueFactory<>("totalDistance"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        // Configure comments column
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        // Add tooltip for comments that might be too long
        commentColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    // Truncate long comments in display
                    String displayText = item.length() > 30 ? item.substring(0, 27) + "..." : item;
                    setText(displayText);

                    // Add tooltip for full text
                    if (item.length() > 30) {
                        setTooltip(new Tooltip(item));
                    } else {
                        setTooltip(null);
                    }
                }
            }
        });

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
        TourLog selectedLog = tourLogsTable.getSelectionModel().getSelectedItem();
        if (selectedLog != null) {
            // Close any open detail tab for this log first
            tourLogsViewModel.closeTourLogDetails(selectedLog);
            // Then delete the log
            tourLogsViewModel.deleteTourLog(selectedLog);
        }
    }

    @FXML
    void onButtonEditLog(ActionEvent event) {
        TourLog selectedLog = tourLogsTable.getSelectionModel().getSelectedItem();
        if (selectedLog != null) {
            tourLogsViewModel.editTourLog(selectedLog);
        }
    }
}