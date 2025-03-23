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
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
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
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        commentColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
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

        tourLogsTable.setItems(tourLogsViewModel.getObservableTourLogs());

        tourLogsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        tourLogsViewModel.selectTourLog(newValue);
                    }
                }
        );

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
            tourLogsViewModel.closeTourLogDetails(selectedLog);
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