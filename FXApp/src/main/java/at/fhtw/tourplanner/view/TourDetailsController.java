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
        nameTextField.textProperty().bindBidirectional(tourDetailsViewModel.nameProperty());
        fromTextField.textProperty().bindBidirectional(tourDetailsViewModel.fromProperty());
        toTextField.textProperty().bindBidirectional(tourDetailsViewModel.toProperty());
        transportTypeComboBox.valueProperty().bindBidirectional(tourDetailsViewModel.transportTypeProperty());
        descriptionTextArea.textProperty().bindBidirectional(tourDetailsViewModel.descriptionProperty());
    }

    @FXML
    void onSaveTourClicked(ActionEvent event) {
        if (validateInputs()) {
            boolean success = tourDetailsViewModel.saveTour();
            if (!success) {
                showErrorMessage("Failed to save tour");
            }
            // No success message - just silently succeed
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        // Reset to original values
        tourDetailsViewModel.resetToOriginal();
    }

    private boolean validateInputs() {
        if (nameTextField.getText() == null || nameTextField.getText().trim().isEmpty()) {
            showErrorMessage("Tour name cannot be empty");
            return false;
        }

        if (fromTextField.getText() == null || fromTextField.getText().trim().isEmpty()) {
            showErrorMessage("From location cannot be empty");
            return false;
        }

        if (toTextField.getText() == null || toTextField.getText().trim().isEmpty()) {
            showErrorMessage("To location cannot be empty");
            return false;
        }

        return true;
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}