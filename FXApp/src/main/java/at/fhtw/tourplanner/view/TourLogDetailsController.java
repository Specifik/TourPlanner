package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.model.TourLog;
import at.fhtw.tourplanner.viewmodel.TourLogDetailsViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TourLogDetailsController {
    @FXML
    private DatePicker dateField;

    @FXML
    private ComboBox<String> difficultyField;

    @FXML
    private TextField distanceField;

    @FXML
    private TextField timeField;

    @FXML
    private ToggleGroup ratingGroup;

    @FXML
    private RadioButton rating1;

    @FXML
    private RadioButton rating2;

    @FXML
    private RadioButton rating3;

    @FXML
    private RadioButton rating4;

    @FXML
    private RadioButton rating5;

    @FXML
    private TextArea commentField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private final TourLogDetailsViewModel viewModel;

    public TourLogDetailsController(TourLogDetailsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML
    void initialize() {
        dateField.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        dateField.setTooltip(new Tooltip("Select the date of the tour"));
        difficultyField.setTooltip(new Tooltip("Select the difficulty level"));
        distanceField.setTooltip(new Tooltip("Enter the total distance in km"));
        timeField.setTooltip(new Tooltip("Enter the total time in minutes"));

        dateField.valueProperty().bindBidirectional(viewModel.dateProperty());
        difficultyField.valueProperty().bindBidirectional(viewModel.difficultyProperty());
        distanceField.textProperty().bindBidirectional(viewModel.distanceStringProperty());
        timeField.textProperty().bindBidirectional(viewModel.timeStringProperty());

        viewModel.ratingProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                for (Toggle toggle : ratingGroup.getToggles()) {
                    if (toggle.getUserData().equals(newVal)) {
                        ratingGroup.selectToggle(toggle);
                        break;
                    }
                }
            }
        });

        ratingGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                viewModel.setRating(Integer.parseInt(newVal.getUserData().toString()));
            }
        });

        commentField.textProperty().bindBidirectional(viewModel.commentProperty());
    }

    private boolean validateInputs() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder("Please fix the following errors:\n");

        if (dateField.getValue() == null) {
            errorMessage.append("- Date is required\n");
            isValid = false;
        } else if (dateField.getValue().isAfter(LocalDate.now())) {
            errorMessage.append("- Date cannot be in the future\n");
            isValid = false;
        }

        if (difficultyField.getValue() == null || difficultyField.getValue().trim().isEmpty()) {
            errorMessage.append("- Difficulty is required\n");
            isValid = false;
        }

        try {
            if (distanceField.getText() == null || distanceField.getText().trim().isEmpty()) {
                errorMessage.append("- Distance is required\n");
                isValid = false;
            } else {
                double distance = Double.parseDouble(distanceField.getText());
                if (distance < 0) {
                    errorMessage.append("- Distance cannot be negative\n");
                    isValid = false;
                }
            }
        } catch (NumberFormatException e) {
            errorMessage.append("- Distance must be a valid number\n");
            isValid = false;
        }

        try {
            if (timeField.getText() == null || timeField.getText().trim().isEmpty()) {
                errorMessage.append("- Time is required\n");
                isValid = false;
            } else {
                int time = Integer.parseInt(timeField.getText());
                if (time < 0) {
                    errorMessage.append("- Time cannot be negative\n");
                    isValid = false;
                }
            }
        } catch (NumberFormatException e) {
            errorMessage.append("- Time must be a valid number\n");
            isValid = false;
        }

        if (!isValid) {
            showErrorMessage(errorMessage.toString());
        }

        return isValid;
    }

    @FXML
    void onSaveClicked(ActionEvent event) {
        if (validateInputs()) {
            if (viewModel.saveTourLog()) {
            } else {
                showErrorMessage("Cannot save tour log: " + viewModel.getValidationErrors());
            }
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        viewModel.closeDetails();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Cannot save tour log");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public TourLogDetailsViewModel getViewModel() {
        return viewModel;
    }
}