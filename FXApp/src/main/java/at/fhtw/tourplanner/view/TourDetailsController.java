package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.viewmodel.TourDetailsViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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

        // Initialize transport type combo box
        transportTypeComboBox.getItems().addAll("Walking", "Biking", "Hiking", "Running", "Car", "Public Transport");
        transportTypeComboBox.valueProperty().bindBidirectional(tourDetailsViewModel.transportTypeProperty());

        // Bind description text area
        descriptionTextArea.textProperty().bindBidirectional(tourDetailsViewModel.descriptionProperty());
    }
}
