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
    private ObservableList<Tour> observableTours = FXCollections.observableArrayList();

    public TourOverviewViewModel() {
        setTours(DAL.getInstance().tourDao().getAll());

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

        // Create a completely new observable list
        ObservableList<Tour> newList = FXCollections.observableArrayList(tours);
        observableTours = newList;

        // Find the equivalent tour in the new list if we had a selection
        if (selectedId != -1) {
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
        // Force refresh
        Platform.runLater(() -> {
            // Refresh the list directly from the DAO
            List<Tour> freshTours = DAL.getInstance().tourDao().getAll();

            // Clear and re-add all items
            observableTours.clear();
            observableTours.addAll(freshTours);

            // Re-select the updated tour
            if (updatedTour != null) {
                for (Tour tour : observableTours) {
                    if (tour.getId() == updatedTour.getId()) {
                        selectedTour.set(tour);
                        break;
                    }
                }
            }
        });
    }

    public void addNewTour() {
        var tour = DAL.getInstance().tourDao().create();
        refreshTours();

        // Find and select the new tour
        for (Tour t : observableTours) {
            if (t.getId() == tour.getId()) {
                selectedTour.set(t);
                break;
            }
        }
    }

    public void deleteTour(Tour tour) {
        DAL.getInstance().tourDao().delete(tour);
        selectedTour.set(null);
        refreshTours();
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