package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TourLogsViewModel {
    private final ObservableList<TourLog> observableTourLogs = FXCollections.observableArrayList();
    private final ObjectProperty<Tour> currentTour = new SimpleObjectProperty<>();
    private final ObjectProperty<TourLog> selectedTourLog = new SimpleObjectProperty<>();

    public interface TourLogDetailsOpenListener {
        void openTourLogDetails(TourLog tourLog);
    }

    private final List<TourLogDetailsOpenListener> tourLogDetailsOpenListeners = new ArrayList<>();

    public TourLogsViewModel() {
        // When the current tour changes, update the tour logs list
        currentTour.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
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
        observableTourLogs.clear();

        if (DAL.getInstance().tourLogDao() != null) {
            List<TourLog> tourLogs = DAL.getInstance().tourLogDao().getByTourId(tourId);
            observableTourLogs.addAll(tourLogs);
        }
    }

    public void addNewTourLog() {
        Tour tour = currentTour.get();
        if (tour != null && DAL.getInstance().tourLogDao() != null) {
            TourLog newLog = DAL.getInstance().tourLogDao().create(tour.getId());
            observableTourLogs.add(newLog);
        }
    }

    public void deleteTourLog(TourLog tourLog) {
        if (tourLog != null && DAL.getInstance().tourLogDao() != null) {
            DAL.getInstance().tourLogDao().delete(tourLog);
            observableTourLogs.remove(tourLog);
        }
    }

    public void editTourLog(TourLog tourLog) {
        if (tourLog != null) {
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

    public void addTourLogDetailsOpenListener(TourLogDetailsOpenListener listener) {
        tourLogDetailsOpenListeners.add(listener);
    }

    public void removeTourLogDetailsOpenListener(TourLogDetailsOpenListener listener) {
        tourLogDetailsOpenListeners.remove(listener);
    }
}