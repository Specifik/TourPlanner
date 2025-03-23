package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.viewmodel.TourDetailsViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TourDetailsController {
    @FXML
    public TextField nameTextField;

    @FXML
    public TextField fromTextField;

    @FXML
    public TextField toTextField;

    @FXML
    public ComboBox<String> transportTypeComboBox;

    @FXML
    public TextArea descriptionTextArea;

    @FXML
    public Button saveTourButton;

    @FXML
    public Button cancelButton;

    private final TourDetailsViewModel tourDetailsViewModel;

    public TourDetailsController(TourDetailsViewModel tourDetailsViewModel) {
        this.tourDetailsViewModel = tourDetailsViewModel;
    }

    public TourDetailsViewModel getTourDetailsViewModel() {
        return tourDetailsViewModel;
    }

    @FXML
    void initialize() {
        // Set up data binding
        nameTextField.textProperty().bindBidirectional(tourDetailsViewModel.nameProperty());
        fromTextField.textProperty().bindBidirectional(tourDetailsViewModel.fromProperty());
        toTextField.textProperty().bindBidirectional(tourDetailsViewModel.toProperty());
        transportTypeComboBox.valueProperty().bindBidirectional(tourDetailsViewModel.transportTypeProperty());
        descriptionTextArea.textProperty().bindBidirectional(tourDetailsViewModel.descriptionProperty());

        // Set up tooltips
        nameTextField.setTooltip(new Tooltip("Enter a name for the tour"));
        fromTextField.setTooltip(new Tooltip("Enter the starting location"));
        toTextField.setTooltip(new Tooltip("Enter the destination"));
        transportTypeComboBox.setTooltip(new Tooltip("Select the mode of transportation"));
    }

    private boolean validateInputs() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder("Please fix the following errors:\n");

        // Validate name
        if (nameTextField.getText() == null || nameTextField.getText().trim().isEmpty()) {
            errorMessage.append("- Tour name cannot be empty\n");
            isValid = false;
        }

        // Validate from
        if (fromTextField.getText() == null || fromTextField.getText().trim().isEmpty()) {
            errorMessage.append("- Starting location cannot be empty\n");
            isValid = false;
        }

        // Validate to
        if (toTextField.getText() == null || toTextField.getText().trim().isEmpty()) {
            errorMessage.append("- Destination cannot be empty\n");
            isValid = false;
        }

        if (!isValid) {
            showErrorMessage(errorMessage.toString());
        }

        return isValid;
    }

    @FXML
    void onSaveTourClicked(ActionEvent event) {
        if (validateInputs()) {
            boolean success = tourDetailsViewModel.saveTour();
            if (!success) {
                showErrorMessage("Failed to save tour");
            }
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        // Reset to original values
        tourDetailsViewModel.resetToOriginal();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Cannot save tour");
        alert.setContentText(message);
        alert.showAndWait();
    }
}