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
    private boolean autoCloseOnSave = true;

    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty difficulty = new SimpleStringProperty();
    private final StringProperty distanceString = new SimpleStringProperty();
    private final StringProperty timeString = new SimpleStringProperty();
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final StringProperty comment = new SimpleStringProperty();

    public interface CloseListener {
        void onClose();
    }

    public interface LogUpdatedListener {
        void onLogUpdated(TourLog updatedLog);
    }

    private final List<CloseListener> closeListeners = new ArrayList<>();
    private final List<LogUpdatedListener> logUpdatedListeners = new ArrayList<>();

    public TourLogDetailsViewModel() {
    }

    public void setAutoCloseOnSave(boolean autoCloseOnSave) {
        this.autoCloseOnSave = autoCloseOnSave;
    }

    public void setTourLog(TourLog tourLog, boolean isNewLog) {
        this.tourLog = tourLog;
        this.isNewLog = isNewLog;

        if (tourLog != null) {
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
            int logId = tourLog.getId();
            int tourId = tourLog.getTourId();

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

            DAL.getInstance().tourLogDao().update(tourLog, Arrays.asList(
                    logId,
                    tourId,
                    tourLog.getDateTime(),
                    tourLog.getComment(),
                    tourLog.getDifficulty(),
                    tourLog.getTotalDistance(),
                    tourLog.getTotalTime(),
                    tourLog.getRating()
            ));

            for (LogUpdatedListener listener : logUpdatedListeners) {
                listener.onLogUpdated(tourLog);
            }

            if (autoCloseOnSave) {
                closeDetails();
            }

            return true;
        }

        return false;
    }

    public boolean validateInput() {
        // Basic validation checks
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

    public void addLogUpdatedListener(LogUpdatedListener listener) {
        logUpdatedListeners.add(listener);
    }

    public void removeLogUpdatedListener(LogUpdatedListener listener) {
        logUpdatedListeners.remove(listener);
    }

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