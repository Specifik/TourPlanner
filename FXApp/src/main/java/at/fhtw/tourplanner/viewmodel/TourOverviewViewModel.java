package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.bl.BL;
import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.Tour;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class TourOverviewViewModel {

    private static final Logger logger = LogManager.getLogger(TourOverviewViewModel.class);

    public interface SelectionChangedListener {
        void changeSelection(Tour tour);
    }

    private List<SelectionChangedListener> listeners = new ArrayList<>();
    private final ObjectProperty<Tour> selectedTour = new SimpleObjectProperty<>();
    private final ObservableList<Tour> observableTours = FXCollections.observableArrayList();

    public TourOverviewViewModel() {
        logger.debug("TourOverviewViewModel initialized");
        refreshTours();
        selectedTour.addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                notifyListeners(newVal);
            }
        });
    }

    public ObservableList<Tour> getObservableTours() {
        return observableTours;
    }

    public ChangeListener<Tour> getChangeListener() {
        return (observableValue, oldValue, newValue) -> {
            selectedTour.set(newValue);
        };
    }

    public void addSelectionChangedListener(SelectionChangedListener listener) {
        listeners.add(listener);
    }

    public void removeSelectionChangedListener(SelectionChangedListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Tour newValue) {
        for (var listener : listeners) {
            listener.changeSelection(newValue);
        }
    }

    public void setTours(List<Tour> tours) {
        Tour currentSelection = selectedTour.get();
        int selectedId = currentSelection != null ? currentSelection.getId() : -1;

        observableTours.clear();
        observableTours.addAll(tours);

        if (selectedId != -1) {
            for (Tour tour : tours) {
                if (tour.getId() == selectedId) {
                    Platform.runLater(() -> selectedTour.set(tour));
                    break;
                }
            }
        }
    }

    public void refreshTours() {
        try {
            List<Tour> tours = BL.getInstance().getAllTours();
            setTours(tours);
            logger.debug("Tours refreshed, {} tours loaded", tours.size());
        } catch (Exception e) {
            logger.error("Failed to refresh tours", e);
        }
    }

    public void handleTourUpdated(Tour updatedTour) {
        refreshTours();
        if (updatedTour != null) {
            for (Tour tour : observableTours) {
                if (tour.getId() == updatedTour.getId()) {
                    selectedTour.set(tour);
                    break;
                }
            }
        }
    }

    public Tour addNewTour() {
        try {
            logger.info("Creating new tour");
            Tour newTour = BL.getInstance().createTour();
            refreshTours();
            for (Tour tour : observableTours) {
                if (tour.getId() == newTour.getId()) {
                    selectedTour.set(tour);
                    logger.info("New tour created with ID: {}", newTour.getId());
                    return tour;
                }
            }
            return newTour;
        } catch (Exception e) {
            logger.error("Failed to create new tour", e);
            return null;
        }
    }

    public void deleteTour(Tour tour) {
        if (tour != null) {
            try {
                logger.info("Deleting tour: {}", tour.getName());
                selectedTour.set(null);
                BL.getInstance().deleteTour(tour);
                refreshTours();
                logger.info("Tour deleted successfully: {}", tour.getName());
            } catch (Exception e) {
                logger.error("Failed to delete tour: {}", tour.getName(), e);
            }
        }
    }

    public ObjectProperty<Tour> selectedTourProperty() {
        return selectedTour;
    }

    public void setSelectedTour(Tour tour) {
        selectedTour.set(tour);
    }
}