package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.bl.BL;
import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.Tour;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TourDetailsViewModel {
    private Tour tourModel;
    private volatile boolean isInitValue = false;

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

    private final BooleanProperty isValidInput = new SimpleBooleanProperty(false);
    private final StringProperty validationErrors = new SimpleStringProperty("");

    public interface TourUpdatedListener {
        void onTourUpdated(Tour updatedTour);
    }

    private final List<TourUpdatedListener> tourUpdatedListeners = new ArrayList<>();

    public TourDetailsViewModel() {
        // listeners for validation
        name.addListener((obs, oldVal, newVal) -> validateInput());
        from.addListener((obs, oldVal, newVal) -> validateInput());
        to.addListener((obs, oldVal, newVal) -> validateInput());
    }

    private void validateInput() {
        if (isInitValue) {
            return;
        }

        StringBuilder errorMessage = new StringBuilder();
        boolean isValid = true;

        // validate name
        if (name.get() == null || name.get().trim().isEmpty()) {
            errorMessage.append("• Tour name cannot be empty\n");
            isValid = false;
        } else if (name.get().trim().length() > 100) {
            errorMessage.append("• Tour name cannot exceed 100 characters\n");
            isValid = false;
        }

        // validate from location
        if (from.get() == null || from.get().trim().isEmpty()) {
            errorMessage.append("• Starting location cannot be empty\n");
            isValid = false;
        } else if (from.get().trim().length() > 100) {
            errorMessage.append("• Starting location cannot exceed 100 characters\n");
            isValid = false;
        }

        // validate to location
        if (to.get() == null || to.get().trim().isEmpty()) {
            errorMessage.append("• Destination cannot be empty\n");
            isValid = false;
        } else if (to.get().trim().length() > 100) {
            errorMessage.append("• Destination cannot exceed 100 characters\n");
            isValid = false;
        }

        // Check if from = to
        if (isValid && from.get() != null && to.get() != null &&
                from.get().trim().equalsIgnoreCase(to.get().trim())) {
            errorMessage.append("• Starting location and destination cannot be the same\n");
            isValid = false;
        }

        // validate description length
        if (description.get() != null && description.get().length() > 500) {
            errorMessage.append("• Description cannot exceed 500 characters\n");
            isValid = false;
        }

        isValidInput.set(isValid);
        validationErrors.set(errorMessage.toString());
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

    public boolean isValidInput() {
        return isValidInput.get();
    }

    public BooleanProperty isValidInputProperty() {
        return isValidInput;
    }

    public String getValidationErrors() {
        return validationErrors.get();
    }

    public StringProperty validationErrorsProperty() {
        return validationErrors;
    }

    public void setTourModel(Tour tourModel) {
        isInitValue = true;
        if (tourModel == null) {
            name.set("");
            from.set("");
            to.set("");
            transportType.set("");
            description.set("");

            originalName = "";
            originalFrom = "";
            originalTo = "";
            originalTransportType = "";
            originalDescription = "";

            isInitValue = false;
            validateInput();
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
        validateInput();
    }

    public void resetToOriginal() {
        if (tourModel != null) {
            isInitValue = true;

            name.setValue(originalName);
            from.setValue(originalFrom);
            to.setValue(originalTo);
            transportType.setValue(originalTransportType);
            description.setValue(originalDescription);

            isInitValue = false;
            validateInput();
        }
    }

    public boolean saveTour() {
        validateInput();

        if (!isValidInput.get() || tourModel == null) {
            return false;
        }

        try {
            tourModel.setName(name.get().trim());
            tourModel.setFrom(from.get().trim());
            tourModel.setTo(to.get().trim());
            tourModel.setTransportType(transportType.get());
            tourModel.setDescription(description.get());

            BL.getInstance().updateTour(tourModel);

            originalName = tourModel.getName();
            originalFrom = tourModel.getFrom();
            originalTo = tourModel.getTo();
            originalTransportType = tourModel.getTransportType();
            originalDescription = tourModel.getDescription();

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