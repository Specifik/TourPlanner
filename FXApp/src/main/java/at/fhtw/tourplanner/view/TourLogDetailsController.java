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

    @FXML
    private Label validationLabel;

    private final TourLogDetailsViewModel viewModel;

    public TourLogDetailsController(TourLogDetailsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML
    void initialize() {
        setupDatePicker();
        setupDataBinding();
        setupValidationBinding();
        setupTooltips();
    }

    private void setupDatePicker() {
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
    }

    private void setupDataBinding() {
        dateField.valueProperty().bindBidirectional(viewModel.dateProperty());
        difficultyField.valueProperty().bindBidirectional(viewModel.difficultyProperty());
        distanceField.textProperty().bindBidirectional(viewModel.distanceStringProperty());
        timeField.textProperty().bindBidirectional(viewModel.timeStringProperty());
        commentField.textProperty().bindBidirectional(viewModel.commentProperty());

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
    }

    private void setupValidationBinding() {
        saveButton.disableProperty().bind(viewModel.isValidInputProperty().not());

        if (validationLabel != null) {
            validationLabel.textProperty().bind(viewModel.validationErrorsProperty());
            validationLabel.visibleProperty().bind(viewModel.isValidInputProperty().not());
            validationLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
        }
    }

    private void setupTooltips() {
        dateField.setTooltip(new Tooltip("Select the date of the tour (cannot be in the future)"));
        difficultyField.setTooltip(new Tooltip("Select the difficulty level"));
        distanceField.setTooltip(new Tooltip("Enter the total distance in km (0-10,000)"));
        timeField.setTooltip(new Tooltip("Enter the total time in minutes (0-10,080)"));
        commentField.setTooltip(new Tooltip("Enter a comment (max 1000 characters)"));
    }


    @FXML
    void onSaveClicked(ActionEvent event) {
        boolean success = viewModel.saveTourLog();
        if (!success && !viewModel.isValidInput()) {
            // Show validation errors in a dialog if not already visible
            showValidationDialog();
        } else if (!success) {
            showErrorMessage("Cannot save tour log due to an unexpected error.");
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        viewModel.closeDetails();
    }

    private void showValidationDialog() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Errors");
        alert.setHeaderText("Please fix the following issues:");
        alert.setContentText(viewModel.getValidationErrors());
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Cannot save tour log");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public TourLogDetailsViewModel getViewModel() {
        return viewModel;
    }
}