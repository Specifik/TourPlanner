package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.TourLog;
import javafx.beans.property.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TourLogDetailsViewModel {

    private static final Logger logger = LogManager.getLogger(TourLogDetailsViewModel.class);

    private TourLog tourLog;
    private boolean isNewLog;
    private boolean autoCloseOnSave = true;
    private volatile boolean isInitValue = false;

    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty difficulty = new SimpleStringProperty();
    private final StringProperty distanceString = new SimpleStringProperty();
    private final StringProperty timeString = new SimpleStringProperty();
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final StringProperty comment = new SimpleStringProperty();

    private final BooleanProperty isValidInput = new SimpleBooleanProperty(false);
    private final StringProperty validationErrors = new SimpleStringProperty("");

    public interface CloseListener {
        void onClose();
    }

    public interface LogUpdatedListener {
        void onLogUpdated(TourLog updatedLog);
    }

    private final List<CloseListener> closeListeners = new ArrayList<>();
    private final List<LogUpdatedListener> logUpdatedListeners = new ArrayList<>();

    public TourLogDetailsViewModel() {
        // listeners for validation
        date.addListener((obs, oldVal, newVal) -> validateInput());
        difficulty.addListener((obs, oldVal, newVal) -> validateInput());
        distanceString.addListener((obs, oldVal, newVal) -> validateInput());
        timeString.addListener((obs, oldVal, newVal) -> validateInput());
        rating.addListener((obs, oldVal, newVal) -> validateInput());
        comment.addListener((obs, oldVal, newVal) -> validateInput());
    }

    private void validateInput() {
        if (isInitValue) {
            return;
        }

        StringBuilder errorMessage = new StringBuilder();
        boolean isValid = true;

        // validate date
        if (date.get() == null) {
            errorMessage.append("• Date is required\n");
            isValid = false;
        } else if (date.get().isAfter(LocalDate.now())) {
            errorMessage.append("• Date cannot be in the future\n");
            isValid = false;
        }

        // validate difficulty
        if (difficulty.get() == null || difficulty.get().trim().isEmpty()) {
            errorMessage.append("• Difficulty is required\n");
            isValid = false;
        }

        // validate distance
        if (distanceString.get() == null || distanceString.get().trim().isEmpty()) {
            errorMessage.append("• Distance is required\n");
            isValid = false;
        } else {
            try {
                double distance = Double.parseDouble(distanceString.get().trim());
                if (distance < 0) {
                    errorMessage.append("• Distance cannot be negative\n");
                    isValid = false;
                } else if (distance > 10000) {
                    errorMessage.append("• Distance cannot exceed 10,000 km\n");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                errorMessage.append("• Distance must be a valid number\n");
                isValid = false;
            }
        }

        // validate time
        if (timeString.get() == null || timeString.get().trim().isEmpty()) {
            errorMessage.append("• Time is required\n");
            isValid = false;
        } else {
            try {
                int time = Integer.parseInt(timeString.get().trim());
                if (time < 0) {
                    errorMessage.append("• Time cannot be negative\n");
                    isValid = false;
                } else if (time > 10080) { // More than a week in minutes
                    errorMessage.append("• Time cannot exceed 10,080 minutes (1 week)\n");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                errorMessage.append("• Time must be a valid number\n");
                isValid = false;
            }
        }

        // validate rating
        if (rating.get() < 1 || rating.get() > 5) {
            errorMessage.append("• Rating must be between 1 and 5\n");
            isValid = false;
        }

        // validate comment length
        if (comment.get() != null && comment.get().length() > 1000) {
            errorMessage.append("• Comment cannot exceed 1000 characters\n");
            isValid = false;
        }

        isValidInput.set(isValid);
        validationErrors.set(errorMessage.toString());
    }

    public void setAutoCloseOnSave(boolean autoCloseOnSave) {
        this.autoCloseOnSave = autoCloseOnSave;
    }

    public void setTourLog(TourLog tourLog, boolean isNewLog) {
        isInitValue = true;
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

        isInitValue = false;
        validateInput();
    }

    private void clearFields() {
        date.set(LocalDate.now());
        difficulty.set("");
        distanceString.set("0.0");
        timeString.set("0");
        rating.set(3); // default rating
        comment.set("");
    }

    public boolean saveTourLog() {
        validateInput();

        if (!isValidInput.get() || tourLog == null) {
            logger.warn("Tour log save failed: validation errors or null tour log");
            return false;
        }

        try {
            logger.info("Saving tour log: ID={}, difficulty={}", tourLog.getId(), difficulty.get());

            int logId = tourLog.getId();
            int tourId = tourLog.getTourId();

            LocalDateTime dateTime = LocalDateTime.of(date.get(), LocalTime.now());
            tourLog.setDateTime(dateTime);
            tourLog.setDifficulty(difficulty.get().trim());
            tourLog.setTotalDistance(Double.parseDouble(distanceString.get().trim()));
            tourLog.setTotalTime(Integer.parseInt(timeString.get().trim()));
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

            logger.info("Tour log saved successfully: ID={}", tourLog.getId());
            return true;

        } catch (Exception e) {
            logger.error("Failed to save tour log: ID={}", tourLog.getId(), e);
            return false;
        }
    }

    public void closeDetails() {
        for (CloseListener listener : closeListeners) {
            listener.onClose();
        }
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
}