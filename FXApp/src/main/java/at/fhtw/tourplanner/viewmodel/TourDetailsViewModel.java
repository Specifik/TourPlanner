package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.Tour;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TourDetailsViewModel {
    private Tour tourModel;
    private volatile boolean isInitValue = false;

    // Store original values for cancel operation
    private String originalName;
    private String originalFrom;
    private String originalTo;
    private String originalTransportType;
    private String originalDescription;

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty from = new SimpleStringProperty();
    private final StringProperty to = new SimpleStringProperty();
    private final StringProperty transportType = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();

    // Callback for when a tour is updated
    public interface TourUpdatedListener {
        void onTourUpdated(Tour updatedTour);
    }

    private final List<TourUpdatedListener> tourUpdatedListeners = new ArrayList<>();

    public TourDetailsViewModel() {
        // We'll manually update the model on save instead of using listeners to avoid unwanted updates
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getFrom() {
        return from.get();
    }

    public StringProperty fromProperty() {
        return from;
    }

    public String getTo() {
        return to.get();
    }

    public StringProperty toProperty() {
        return to;
    }

    public String getTransportType() {
        return transportType.get();
    }

    public StringProperty transportTypeProperty() {
        return transportType;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setTourModel(Tour tourModel) {
        isInitValue = true;
        if (tourModel == null) {
            // Reset fields if no tour is selected
            name.set("");
            from.set("");
            to.set("");
            transportType.set("");
            description.set("");

            // Reset originals
            originalName = "";
            originalFrom = "";
            originalTo = "";
            originalTransportType = "";
            originalDescription = "";

            return;
        }

        this.tourModel = tourModel;

        // Set the properties
        name.setValue(tourModel.getName());
        from.setValue(tourModel.getFrom());
        to.setValue(tourModel.getTo());
        transportType.setValue(tourModel.getTransportType());
        description.setValue(tourModel.getDescription());

        // Store original values
        originalName = tourModel.getName();
        originalFrom = tourModel.getFrom();
        originalTo = tourModel.getTo();
        originalTransportType = tourModel.getTransportType();
        originalDescription = tourModel.getDescription();

        isInitValue = false;
    }

    public void resetToOriginal() {
        if (tourModel != null) {
            isInitValue = true;

            // Reset to stored original values
            name.setValue(originalName);
            from.setValue(originalFrom);
            to.setValue(originalTo);
            transportType.setValue(originalTransportType);
            description.setValue(originalDescription);

            isInitValue = false;
        }
    }

    public boolean saveTour() {
        if (tourModel == null) {
            return false;
        }

        try {
            // Update the tour model with current UI values
            tourModel.setName(name.get());
            tourModel.setFrom(from.get());
            tourModel.setTo(to.get());
            tourModel.setTransportType(transportType.get());
            tourModel.setDescription(description.get());

            // Save to the DAO
            DAL.getInstance().tourDao().update(tourModel, Arrays.asList(
                    tourModel.getId(),
                    tourModel.getName(),
                    tourModel.getFrom(),
                    tourModel.getTo(),
                    tourModel.getTransportType(),
                    tourModel.getDescription()
            ));

            // Update original values after successful save
            originalName = tourModel.getName();
            originalFrom = tourModel.getFrom();
            originalTo = tourModel.getTo();
            originalTransportType = tourModel.getTransportType();
            originalDescription = tourModel.getDescription();

            // Notify listeners
            for (TourUpdatedListener listener : tourUpdatedListeners) {
                listener.onTourUpdated(tourModel);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addTourUpdatedListener(TourUpdatedListener listener) {
        tourUpdatedListeners.add(listener);
    }

    public void removeTourUpdatedListener(TourUpdatedListener listener) {
        tourUpdatedListeners.remove(listener);
    }
}