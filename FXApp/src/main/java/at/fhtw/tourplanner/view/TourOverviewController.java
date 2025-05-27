package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.viewmodel.TourOverviewViewModel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

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

        setupDragAndDrop();
    }

    private void setupDragAndDrop() {
        tourList.setCellFactory(listView -> {
            ListCell<Tour> cell = new ListCell<Tour>() {
                @Override
                protected void updateItem(Tour tour, boolean empty) {
                    super.updateItem(tour, empty);
                    if (empty || tour == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(tour.toString());
                    }
                }
            };

            // Enable drag
            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(String.valueOf(cell.getIndex()));
                    dragboard.setContent(content);

                    // Create drag image (visual feedback)
                    dragboard.setDragView(cell.snapshot(null, null));

                    event.consume();
                }
            });

            // Enable drop on cells
            cell.setOnDragOver(event -> {
                if (event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            // Handle drop
            cell.setOnDragDropped(event -> {
                Dragboard dragboard = event.getDragboard();
                boolean success = false;

                if (dragboard.hasString()) {
                    int draggedIndex = Integer.parseInt(dragboard.getString());
                    int dropIndex = cell.getIndex();

                    // Fix: Handle drop on empty cells (end of list)
                    if (dropIndex >= tourList.getItems().size()) {
                        dropIndex = tourList.getItems().size() - 1;
                    }

                    if (draggedIndex != dropIndex && draggedIndex < tourList.getItems().size()) {
                        ObservableList<Tour> items = tourList.getItems();
                        Tour draggedTour = items.get(draggedIndex);

                        // Remove and re-insert at new position
                        items.remove(draggedIndex);
                        if (dropIndex > draggedIndex) {
                            dropIndex--; // Adjust for removal
                        }
                        items.add(dropIndex, draggedTour);

                        // Keep selection on moved item
                        tourList.getSelectionModel().select(dropIndex);
                        success = true;
                    }
                }

                event.setDropCompleted(success);
                event.consume();
            });

            return cell;
        });

        // Also allow dropping on the ListView itself (for end of list)
        tourList.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        tourList.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasString()) {
                int draggedIndex = Integer.parseInt(dragboard.getString());

                if (draggedIndex < tourList.getItems().size()) {
                    ObservableList<Tour> items = tourList.getItems();
                    Tour draggedTour = items.get(draggedIndex);

                    // Move to end of list
                    items.remove(draggedIndex);
                    items.add(draggedTour);

                    // Keep selection on moved item
                    tourList.getSelectionModel().select(items.size() - 1);
                    success = true;
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    @FXML
    public void onButtonAdd(ActionEvent event) {
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