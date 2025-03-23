package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.TourLog;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TourLogDetailsViewModel {
    private TourLog tourLog;
    private boolean isNewLog;

    // Properties for binding with UI
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty difficulty = new SimpleStringProperty();
    private final StringProperty distanceString = new SimpleStringProperty();
    private final StringProperty timeString = new SimpleStringProperty();
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final StringProperty comment = new SimpleStringProperty();

    // Callback for when the editor should be closed
    public interface CloseListener {
        void onClose();
    }

    private final List<CloseListener> closeListeners = new ArrayList<>();

    public TourLogDetailsViewModel() {
    }

    public void setTourLog(TourLog tourLog, boolean isNewLog) {
        this.tourLog = tourLog;
        this.isNewLog = isNewLog;

        if (tourLog != null) {
            // Convert LocalDateTime to LocalDate for DatePicker
            date.set(tourLog.getDateTime() != null ? tourLog.getDateTime().toLocalDate() : LocalDate.now());
            difficulty.set(tourLog.getDifficulty());
            distanceString.set(String.valueOf(tourLog.getTotalDistance()));
            timeString.set(String.valueOf(tourLog.getTotalTime()));
            rating.set(tourLog.getRating());
            comment.set(tourLog.getComment());
        } else {
            clearFields();
        }
    }

    private void clearFields() {
        date.set(LocalDate.now());
        difficulty.set("");
        distanceString.set("0.0");
        timeString.set("0");
        rating.set(3); // Default rating
        comment.set("");
    }

    public boolean saveTourLog() {
        if (!validateInput()) {
            return false;
        }

        if (tourLog != null) {
            // Update the tour log from the UI values
            LocalDateTime dateTime = LocalDateTime.of(date.get(), LocalTime.now());
            tourLog.setDateTime(dateTime);
            tourLog.setDifficulty(difficulty.get());

            try {
                tourLog.setTotalDistance(Double.parseDouble(distanceString.get()));
            } catch (NumberFormatException e) {
                tourLog.setTotalDistance(0.0);
            }

            try {
                tourLog.setTotalTime(Integer.parseInt(timeString.get()));
            } catch (NumberFormatException e) {
                tourLog.setTotalTime(0);
            }

            tourLog.setRating(rating.get());
            tourLog.setComment(comment.get());

            // Save to the DAO
            DAL.getInstance().tourLogDao().update(tourLog, Arrays.asList(
                    tourLog.getId(),
                    tourLog.getTourId(),
                    tourLog.getDateTime(),
                    tourLog.getComment(),
                    tourLog.getDifficulty(),
                    tourLog.getTotalDistance(),
                    tourLog.getTotalTime(),
                    tourLog.getRating()
            ));

            return true;
        }

        return false;
    }

    public boolean validateInput() {
        // Check that all required fields are filled
        if (date.get() == null) {
            validationErrors = "Date is required";
            return false;
        }

        if (difficulty.get() == null || difficulty.get().isEmpty()) {
            validationErrors = "Difficulty is required";
            return false;
        }

        try {
            double distance = Double.parseDouble(distanceString.get());
            if (distance < 0) {
                validationErrors = "Distance cannot be negative";
                return false;
            }
        } catch (NumberFormatException e) {
            validationErrors = "Distance must be a valid number";
            return false;
        }

        try {
            int time = Integer.parseInt(timeString.get());
            if (time < 0) {
                validationErrors = "Time cannot be negative";
                return false;
            }
        } catch (NumberFormatException e) {
            validationErrors = "Time must be a valid number";
            return false;
        }

        return true;
    }

    private String validationErrors = "";

    public String getValidationErrors() {
        return validationErrors;
    }

    public void closeDetails() {
        for (CloseListener listener : closeListeners) {
            listener.onClose();
        }
    }

    public void addCloseListener(CloseListener listener) {
        closeListeners.add(listener);
    }

    public void removeCloseListener(CloseListener listener) {
        closeListeners.remove(listener);
    }

    // Property getters
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public StringProperty difficultyProperty() {
        return difficulty;
    }

    public StringProperty distanceStringProperty() {
        return distanceString;
    }

    public StringProperty timeStringProperty() {
        return timeString;
    }

    public IntegerProperty ratingProperty() {
        return rating;
    }

    public StringProperty commentProperty() {
        return comment;
    }

    public void setRating(int rating) {
        this.rating.set(rating);
    }
}