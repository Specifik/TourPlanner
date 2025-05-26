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
import at.fhtw.tourplanner.bl.MapService;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.stage.FileChooser;
import java.io.File;
import at.fhtw.tourplanner.bl.BL;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;

public class MainWindowController {

    private static final Logger logger = LogManager.getLogger(MainWindowController.class);

    @FXML private SearchBarController searchBarController;
    @FXML private TourOverviewController tourOverviewController;
    @FXML private TourDetailsController tourDetailsController;
    @FXML private TourLogsController tourLogsController;
    @FXML private TabPane detailsTabPane;

    @FXML private WebView mapWebView;
    private MapService mapService = new MapService();

    private final MainWindowViewModel mainWindowViewModel;
    private Tab tourLogTab = null;
    private TourLogDetailsController tourLogDetailsController = null;
    private Tab tourDetailsTab = null;

    public MainWindowController(MainWindowViewModel mainWindowViewModel) {
        this.mainWindowViewModel = mainWindowViewModel;
        logger.debug("MainWindowController initialized");
    }

    public MainWindowViewModel getMainWindowViewModel() {
        return mainWindowViewModel;
    }

    @FXML
    void initialize() {
        logger.info("Initializing main window components");

        try {
            // method called after all @FXML fields have been initialized
            if (detailsTabPane.getTabs().size() > 0) {
                tourDetailsTab = detailsTabPane.getTabs().get(0);
            }

            setupTourSelectionListeners();
            setupTourLogListeners();
            setupTourUpdateListeners();
            setupMapDisplay();

            logger.info("Main window initialization completed successfully");

        } catch (Exception e) {
            logger.error("Failed to initialize main window", e);
        }
    }

    private void setupTourSelectionListeners() {
        if (tourLogsController != null && tourOverviewController != null) {
            tourOverviewController.getTourOverviewViewModel().addSelectionChangedListener(selectedTour -> {
                if (tourDetailsTab != null) {
                    detailsTabPane.getSelectionModel().select(tourDetailsTab);
                }
                tourLogsController.getTourLogsViewModel().setCurrentTour(selectedTour);
            });
        }
    }

    private void setupTourLogListeners() {
        if (tourLogsController != null) {
            tourLogsController.getTourLogsViewModel().addTourLogDetailsOpenListener(
                    this::showTourLogDetails
            );

            tourLogsController.getTourLogsViewModel().addTourLogDetailsCloseListener(
                    this::closeTourLogDetails
            );
        }
    }

    private void setupTourUpdateListeners() {
        if (tourDetailsController != null && tourOverviewController != null) {
            tourDetailsController.getTourDetailsViewModel().addTourUpdatedListener(
                    this::handleTourUpdated
            );
        }
    }

    private void setupMapDisplay() {
        if (mapWebView != null) {
            mapService.showNoRouteMessage(mapWebView);
        }

        if (tourOverviewController != null) {
            tourOverviewController.getTourOverviewViewModel().addSelectionChangedListener(selectedTour -> {
                if (tourDetailsTab != null) {
                    detailsTabPane.getSelectionModel().select(tourDetailsTab);
                }

                tourLogsController.getTourLogsViewModel().setCurrentTour(selectedTour);

                // Display map for selected tour
                displayTourMap(selectedTour);
            });
        }
    }

    private void displayTourMap(Tour selectedTour) {
        if (selectedTour != null && mapWebView != null) {
            if (selectedTour.getRouteGeoJson() != null && !selectedTour.getRouteGeoJson().trim().isEmpty()) {
                logger.debug("Displaying map for tour: {}", selectedTour.getName());
                mapService.displayTourMap(mapWebView, selectedTour.getRouteGeoJson(), selectedTour.getName());
            } else {
                logger.debug("No route data available for tour: {}", selectedTour.getName());
                mapService.showNoRouteMessage(mapWebView);
            }
        }
    }

    private void handleTourUpdated(Tour updatedTour) {
        logger.info("Handling tour update: {}", updatedTour.getName());
        // Force an immediate UI refresh
        Platform.runLater(() -> {
            tourOverviewController.getTourOverviewViewModel().handleTourUpdated(updatedTour);
            tourOverviewController.tourList.refresh();
        });
    }

    private void showTourLogDetails(TourLog tourLog) {
        try {
            logger.debug("Opening tour log details for log ID: {}", tourLog.getId());

            if (tourLogTab == null) {
                FXMLLoader loader = FXMLDependencyInjection.getLoader("TourLogDetails.fxml", Locale.getDefault());
                Parent root = loader.load();
                tourLogDetailsController = loader.getController();

                tourLogDetailsController.getViewModel().addLogUpdatedListener(
                        updatedLog -> tourLogsController.getTourLogsViewModel().handleLogUpdated(updatedLog)
                );

                tourLogDetailsController.getViewModel().setAutoCloseOnSave(false);

                tourLogTab = new Tab("Tour Log Details", root);
                tourLogTab.setClosable(true);

                tourLogTab.setOnClosed(event -> {
                    tourLogTab = null;
                    tourLogDetailsController = null;
                    logger.debug("Tour log details tab closed");
                });

                detailsTabPane.getTabs().add(tourLogTab);
            }

            updateTourLogTab(tourLog);
            detailsTabPane.getSelectionModel().select(tourLogTab);

        } catch (IOException e) {
            logger.error("Failed to open tour log details", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open tour log details");
            alert.setContentText("An error occurred while trying to open the tour log details: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void updateTourLogTab(TourLog tourLog) {
        if (tourLogTab != null && tourLogDetailsController != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            tourLogTab.setText("Log: " + tourLog.getDateTime().toLocalDate().format(formatter));

            tourLogDetailsController.getViewModel().setTourLog(tourLog, false);
        }
    }

    private void closeTourLogDetails(TourLog tourLog) {
        if (tourLogTab != null) {
            logger.debug("Closing tour log details");
            detailsTabPane.getTabs().remove(tourLogTab);
            tourLogTab = null;
            tourLogDetailsController = null;
        }
    }

    public void onMenuFileQuitClicked(ActionEvent actionEvent) {
        logger.info("Application quit requested");
        Platform.exit();
    }

    public void onMenuHelpAboutClicked(ActionEvent actionEvent) {
        logger.debug("About dialog opened");
        Alert aboutBox = new Alert(Alert.AlertType.INFORMATION, "TourPlanner Application\nCreated for SWEN2");
        aboutBox.setTitle("About TourPlanner");
        aboutBox.showAndWait();
    }

    public void onMenuGenerateTourReport(ActionEvent actionEvent) {
        Tour selectedTour = tourOverviewController.getTourOverviewViewModel().selectedTourProperty().get();

        if (selectedTour == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Tour Selected");
            alert.setHeaderText("Please select a tour");
            alert.setContentText("You need to select a tour before generating a tour report.");
            alert.showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Tour Report");
        fileChooser.setInitialFileName(selectedTour.getName().replaceAll("[^a-zA-Z0-9]", "_") + "_report.pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(tourOverviewController.tourList.getScene().getWindow());
        if (file != null) {
            try {
                logger.info("Generating tour report for: {}", selectedTour.getName());
                BL.getInstance().generateTourReport(selectedTour, file.getAbsolutePath());

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Report Generated");
                success.setHeaderText("Tour report created successfully");
                success.setContentText("Report saved to: " + file.getAbsolutePath());
                success.showAndWait();

            } catch (Exception e) {
                logger.error("Failed to generate tour report", e);
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Report Generation Failed");
                error.setHeaderText("Could not generate tour report");
                error.setContentText("Error: " + e.getMessage());
                error.showAndWait();
            }
        }
    }

    public void onMenuGenerateSummaryReport(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Summary Report");
        fileChooser.setInitialFileName("Tour_Summary_Report.pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(tourOverviewController.tourList.getScene().getWindow());
        if (file != null) {
            try {
                logger.info("Generating summary report");
                BL.getInstance().generateSummaryReport(file.getAbsolutePath());

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Report Generated");
                success.setHeaderText("Summary report created successfully");
                success.setContentText("Report saved to: " + file.getAbsolutePath());
                success.showAndWait();

            } catch (Exception e) {
                logger.error("Failed to generate summary report", e);
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Report Generation Failed");
                error.setHeaderText("Could not generate summary report");
                error.setContentText("An error occurred: " + e.getMessage());
                error.showAndWait();
            }
        }
    }

    @FXML
    public void onMenuFileImportClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Tours");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json")
        );
        File file = fileChooser.showOpenDialog(detailsTabPane.getScene().getWindow());

        if (file != null) {
            try {
                logger.info("Importing tours from: {}", file.getAbsolutePath());
                BL.getInstance().importToursFromFile(file.getAbsolutePath());
                // Refresh the tour list in the UI
                tourOverviewController.getTourOverviewViewModel().refreshTours();
                logger.info("Tour list refreshed after import.");

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Import Successful");
                success.setHeaderText("Tours imported successfully.");
                success.setContentText("Tours have been imported from " + file.getName());
                success.showAndWait();
            } catch (IOException e) {
                logger.error("Failed to import tours from file {}: {}", file.getAbsolutePath(), e.getMessage(), e);
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Import Failed");
                errorAlert.setHeaderText("Could not import tours from file.");
                errorAlert.setContentText("An error occurred: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    public void onMenuFileExportClicked(ActionEvent actionEvent) {
        List<Tour> toursToExport = tourOverviewController.getTourOverviewViewModel().getObservableTours();

        if (toursToExport == null || toursToExport.isEmpty()) {
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("No Tours to Export");
            infoAlert.setHeaderText("There are no tours available to export.");
            infoAlert.showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Tours");
        fileChooser.setInitialFileName("tours.json");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json")
        );
        File file = fileChooser.showSaveDialog(detailsTabPane.getScene().getWindow());

        if (file != null) {
            try {
                logger.info("Exporting {} tours to: {}", toursToExport.size(), file.getAbsolutePath());
                BL.getInstance().exportAllTours(file.getAbsolutePath());

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Export Successful");
                success.setHeaderText("Tours exported successfully.");
                success.setContentText("Tours have been exported to " + file.getName());
                success.showAndWait();
            } catch (IOException e) {
                logger.error("Failed to export tours to file {}: {}", file.getAbsolutePath(), e.getMessage(), e);
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Export Failed");
                errorAlert.setHeaderText("Could not export tours to file.");
                errorAlert.setContentText("An error occurred: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }
}