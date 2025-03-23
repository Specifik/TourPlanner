package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.model.TourLog;
import at.fhtw.tourplanner.viewmodel.TourLogDetailsViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
        // Bind UI elements to the view model properties
        dateField.valueProperty().bindBidirectional(viewModel.dateProperty());
        difficultyField.valueProperty().bindBidirectional(viewModel.difficultyProperty());

        // Set up numeric text fields
        distanceField.textProperty().bindBidirectional(viewModel.distanceStringProperty());
        timeField.textProperty().bindBidirectional(viewModel.timeStringProperty());

        // Set up the rating toggle group
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

        // Bind the comment field
        commentField.textProperty().bindBidirectional(viewModel.commentProperty());
    }

    @FXML
    void onSaveClicked(ActionEvent event) {
        if (viewModel.saveTourLog()) {
            // Close the tab or panel
            viewModel.closeDetails();
        } else {
            // Show validation errors
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Cannot save tour log");
            alert.setContentText(viewModel.getValidationErrors());
            alert.showAndWait();
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        viewModel.closeDetails();
    }

    public TourLogDetailsViewModel getViewModel() {
        return viewModel;
    }
}