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
        List<Tour> tours = BL.getInstance().getAllTours();
        setTours(tours);
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
        Tour newTour = BL.getInstance().createTour();
        refreshTours();
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
            selectedTour.set(null);
            DAL.getInstance().tourDao().delete(tour);
            refreshTours();
        }
    }

    public ObjectProperty<Tour> selectedTourProperty() {
        return selectedTour;
    }

    public void setSelectedTour(Tour tour) {
        selectedTour.set(tour);
    }
}