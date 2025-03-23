package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.Tour;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class TourOverviewViewModel {
    public interface SelectionChangedListener {
        void changeSelection(Tour tour);
    }

    private List<SelectionChangedListener> listeners = new ArrayList<>();
    private final ObjectProperty<Tour> selectedTour = new SimpleObjectProperty<>();
    private final ObservableList<Tour> observableTours = FXCollections.observableArrayList();

    public TourOverviewViewModel() {
        refreshTours();

        // Listen to our own selection property
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
        // Save reference to currently selected tour
        Tour currentSelection = selectedTour.get();
        int selectedId = currentSelection != null ? currentSelection.getId() : -1;

        // Update the observable list
        observableTours.clear();
        observableTours.addAll(tours);

        // Try to restore the selection if we had one
        if (selectedId != -1) {
            // Find the equivalent tour in the new list
            for (Tour tour : tours) {
                if (tour.getId() == selectedId) {
                    // Update our selection
                    Platform.runLater(() -> selectedTour.set(tour));
                    break;
                }
            }
        }
    }

    public void refreshTours() {
        List<Tour> tours = DAL.getInstance().tourDao().getAll();
        setTours(tours);
    }

    public void handleTourUpdated(Tour updatedTour) {
        // Refresh the list to get the latest data
        refreshTours();

        // Re-select the updated tour
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
        // Create a new tour in the DAO
        Tour newTour = DAL.getInstance().tourDao().create();

        // Refresh to get the latest list
        refreshTours();

        // Find and return the newly created tour
        for (Tour tour : observableTours) {
            if (tour.getId() == newTour.getId()) {
                selectedTour.set(tour);
                return tour;
            }
        }

        return newTour;
    }

    public void deleteTour(Tour tour) {
        if (tour != null) {
            // First set selection to null to avoid issues
            selectedTour.set(null);

            // Delete from DAO
            DAL.getInstance().tourDao().delete(tour);

            // Refresh list
            refreshTours();
        }
    }

    // Getter for selected tour property
    public ObjectProperty<Tour> selectedTourProperty() {
        return selectedTour;
    }

    // Setter for selected tour
    public void setSelectedTour(Tour tour) {
        selectedTour.set(tour);
    }
}