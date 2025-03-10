package at.fhtw.medialib.view;

import at.fhtw.medialib.viewmodel.MediaDetailsViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MediaDetailsController {
    @FXML
    public TextField nameTextField;

    private final MediaDetailsViewModel mediaDetailsViewModel;

    public MediaDetailsController(MediaDetailsViewModel mediaDetailsViewModel) {
        this.mediaDetailsViewModel = mediaDetailsViewModel;
    }

    public MediaDetailsViewModel getMediaDetailsViewModel() {
        return mediaDetailsViewModel;
    }

    @FXML
    void initialize() {
        nameTextField.textProperty().bindBidirectional(mediaDetailsViewModel.nameProperty());
    }
}
