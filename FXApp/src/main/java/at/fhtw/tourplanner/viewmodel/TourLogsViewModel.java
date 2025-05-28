package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.bl.BL;
import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TourLogsViewModel {

    private static final Logger logger = LogManager.getLogger(TourLogsViewModel.class);

    private final ObservableList<TourLog> observableTourLogs = FXCollections.observableArrayList();
    private final ObjectProperty<Tour> currentTour = new SimpleObjectProperty<>();
    private final ObjectProperty<TourLog> selectedTourLog = new SimpleObjectProperty<>();

    public interface TourLogDetailsOpenListener {
        void openTourLogDetails(TourLog tourLog);
    }

    public interface TourLogDetailsCloseListener {
        void closeTourLogDetails(TourLog tourLog);
    }

    private final List<TourLogDetailsOpenListener> tourLogDetailsOpenListeners = new ArrayList<>();
    private final List<TourLogDetailsCloseListener> tourLogDetailsCloseListeners = new ArrayList<>();

    public TourLogsViewModel() {
        // When the current tour changes, update the tour logs list
        currentTour.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                logger.debug("Current tour changed to: {}", newValue.getName());
                loadTourLogs(newValue.getId());
            } else {
                observableTourLogs.clear();
            }
        });
    }

    public ObservableList<TourLog> getObservableTourLogs() {
        return observableTourLogs;
    }

    public void setCurrentTour(Tour tour) {
        currentTour.set(tour);
    }

    public Tour getCurrentTour() {
        return currentTour.get();
    }

    public ObjectProperty<Tour> currentTourProperty() {
        return currentTour;
    }

    private void loadTourLogs(int tourId) {
        try {
            observableTourLogs.clear();

            List<TourLog> tourLogs = BL.getInstance().getTourLogsByTourId(tourId);
            observableTourLogs.addAll(tourLogs);
            logger.debug("Loaded {} tour logs for tour ID: {}", tourLogs.size(), tourId);
        } catch (Exception e) {
            logger.error("Failed to load tour logs for tour ID: {}", tourId, e);
        }
    }

    public void refreshTourLogs() {
        if (currentTour.get() != null) {
            Platform.runLater(() -> loadTourLogs(currentTour.get().getId()));
        }
    }

    public void handleLogUpdated(TourLog updatedLog) {
        refreshTourLogs();
    }

    public void addNewTourLog() {
        Tour tour = currentTour.get();
        if (tour != null) {
            try {
                logger.info("Creating new tour log for tour: {}", tour.getName());

                TourLog newLog = BL.getInstance().createTourLog(tour.getId());

                refreshTourLogs(); // Reload list
                openTourLogDetails(newLog);
                logger.info("New tour log created with ID: {}", newLog.getId());
            } catch (Exception e) {
                logger.error("Failed to create new tour log for tour: {}", tour.getName(), e);
            }
        }
    }

    public void deleteTourLog(TourLog tourLog) {
        if (tourLog != null) {
            try {
                logger.info("Deleting tour log ID: {}", tourLog.getId());

                BL.getInstance().deleteTourLog(tourLog);

                refreshTourLogs();
                logger.info("Tour log deleted successfully");
            } catch (Exception e) {
                logger.error("Failed to delete tour log ID: {}", tourLog.getId(), e);
            }
        }
    }

    public void editTourLog(TourLog tourLog) {
        if (tourLog != null) {
            logger.debug("Opening tour log for editing: ID {}", tourLog.getId());
            openTourLogDetails(tourLog);
        }
    }

    public void selectTourLog(TourLog tourLog) {
        selectedTourLog.set(tourLog);
    }

    public TourLog getSelectedTourLog() {
        return selectedTourLog.get();
    }

    public ObjectProperty<TourLog> selectedTourLogProperty() {
        return selectedTourLog;
    }

    public void openTourLogDetails(TourLog tourLog) {
        if (tourLog != null) {
            for (TourLogDetailsOpenListener listener : tourLogDetailsOpenListeners) {
                listener.openTourLogDetails(tourLog);
            }
        }
    }

    public void closeTourLogDetails(TourLog tourLog) {
        if (tourLog != null) {
            for (TourLogDetailsCloseListener listener : tourLogDetailsCloseListeners) {
                listener.closeTourLogDetails(tourLog);
            }
        }
    }

    public void addTourLogDetailsOpenListener(TourLogDetailsOpenListener listener) {
        tourLogDetailsOpenListeners.add(listener);
    }

    public void removeTourLogDetailsOpenListener(TourLogDetailsOpenListener listener) {
        tourLogDetailsOpenListeners.remove(listener);
    }

    public void addTourLogDetailsCloseListener(TourLogDetailsCloseListener listener) {
        tourLogDetailsCloseListeners.add(listener);
    }

    public void removeTourLogDetailsCloseListener(TourLogDetailsCloseListener listener) {
        tourLogDetailsCloseListeners.remove(listener);
    }
}