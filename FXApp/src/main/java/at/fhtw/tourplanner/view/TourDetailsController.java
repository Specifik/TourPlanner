package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.viewmodel.TourDetailsViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

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

    @FXML
    public Label validationLabel;

    @FXML
    private Button copyFromAddressButton;

    @FXML
    private Button copyToAddressButton;

    private final TourDetailsViewModel tourDetailsViewModel;

    public TourDetailsController(TourDetailsViewModel tourDetailsViewModel) {
        this.tourDetailsViewModel = tourDetailsViewModel;
    }

    public TourDetailsViewModel getTourDetailsViewModel() {
        return tourDetailsViewModel;
    }

    @FXML
    void initialize() {
        // data binding
        nameTextField.textProperty().bindBidirectional(tourDetailsViewModel.nameProperty());
        fromTextField.textProperty().bindBidirectional(tourDetailsViewModel.fromProperty());
        toTextField.textProperty().bindBidirectional(tourDetailsViewModel.toProperty());
        transportTypeComboBox.valueProperty().bindBidirectional(tourDetailsViewModel.transportTypeProperty());
        descriptionTextArea.textProperty().bindBidirectional(tourDetailsViewModel.descriptionProperty());

        saveTourButton.disableProperty().bind(tourDetailsViewModel.isValidInputProperty().not());

        if (validationLabel != null) {
            validationLabel.textProperty().bind(tourDetailsViewModel.validationErrorsProperty());
            validationLabel.visibleProperty().bind(tourDetailsViewModel.isValidInputProperty().not());
            validationLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
        }

        // tooltips
        nameTextField.setTooltip(new Tooltip("Enter a name for the tour (max 100 characters)"));
        fromTextField.setTooltip(new Tooltip("Enter the starting location (max 100 characters)"));
        toTextField.setTooltip(new Tooltip("Enter the destination (max 100 characters)"));
        transportTypeComboBox.setTooltip(new Tooltip("Select the mode of transportation"));
        descriptionTextArea.setTooltip(new Tooltip("Enter a description (max 500 characters)"));


    }

    @FXML
    void onSaveTourClicked(ActionEvent event) {
        boolean success = tourDetailsViewModel.saveTour();
        if (!success && !tourDetailsViewModel.isValidInput()) {
            showValidationDialog();
        } else if (!success) {
            showErrorMessage("Failed to save tour due to an unexpected error.");
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        tourDetailsViewModel.resetToOriginal();
    }

    @FXML
    void onCopyFromAddressClicked(ActionEvent event) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(fromTextField.getText());
        clipboard.setContent(content);
    }

    @FXML
    void onCopyToAddressClicked(ActionEvent event) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(toTextField.getText());
        clipboard.setContent(content);
    }

    private void showValidationDialog() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Errors");
        alert.setHeaderText("Please fix the following issues:");
        alert.setContentText(tourDetailsViewModel.getValidationErrors());
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Cannot save tour");
        alert.setContentText(message);
        alert.showAndWait();
    }
}