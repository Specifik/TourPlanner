package at.fhtw.medialib.view;

import at.fhtw.medialib.model.MediaItem;
import at.fhtw.medialib.viewmodel.MediaOverviewViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class MediaOverviewController {
    @FXML
    public ListView<MediaItem> mediaItemList;

    private final MediaOverviewViewModel mediaOverviewViewModel;

    public MediaOverviewController(MediaOverviewViewModel mediaOverviewViewModel) {
        this.mediaOverviewViewModel = mediaOverviewViewModel;
    }

    public MediaOverviewViewModel getMediaOverviewViewModel() {
        return mediaOverviewViewModel;
    }

    @FXML
    void initialize() {
        mediaItemList.setItems(mediaOverviewViewModel.getObservableTours());
        mediaItemList.getSelectionModel().selectedItemProperty().addListener(mediaOverviewViewModel.getChangeListener());
    }

    public void onButtonAdd(ActionEvent actionEvent) {
        mediaOverviewViewModel.addNewTour();
        mediaItemList.getSelectionModel().selectLast();
    }

    public void onButtonRemove(ActionEvent actionEvent) {
        mediaOverviewViewModel.deleteTour(mediaItemList.getSelectionModel().getSelectedItem());
    }
}
