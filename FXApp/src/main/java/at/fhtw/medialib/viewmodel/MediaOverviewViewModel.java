package at.fhtw.medialib.viewmodel;

import at.fhtw.medialib.dal.DAL;
import at.fhtw.medialib.model.MediaItem;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class MediaOverviewViewModel {
    public interface SelectionChangedListener {
        void changeSelection(MediaItem mediaItem);
    }

    private List<SelectionChangedListener> listeners = new ArrayList<>();

    private ObservableList<MediaItem> observableMediaItems = FXCollections.observableArrayList();

    public MediaOverviewViewModel()
    {
        setTours( DAL.getInstance().tourDao().getAll() );
    }

    public ObservableList<MediaItem> getObservableTours() {
        return observableMediaItems;
    }

    public ChangeListener<MediaItem> getChangeListener() {
        return (observableValue, oldValue, newValue) -> notifyListeners(newValue);
    }

    public void addSelectionChangedListener(SelectionChangedListener listener) {
        listeners.add(listener);
    }

    public void removeSelectionChangedListener(SelectionChangedListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(MediaItem newValue) {
        for ( var listener : listeners ) {
            listener.changeSelection(newValue);
        }
    }

    public void setTours(List<MediaItem> mediaItems) {
        observableMediaItems.clear();
        observableMediaItems.addAll(mediaItems);
    }

    public void addNewTour() {
        var tour = DAL.getInstance().tourDao().create();
        observableMediaItems.add(tour);
    }

    public void deleteTour(MediaItem mediaItem) {
        DAL.getInstance().tourDao().delete(mediaItem);
        observableMediaItems.remove(mediaItem);
    }
}
