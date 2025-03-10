package at.fhtw.medialib.viewmodel;

import at.fhtw.medialib.dal.DAL;
import at.fhtw.medialib.model.MediaItem;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Arrays;

public class MediaDetailsViewModel {
    private MediaItem mediaItemModel;
    private volatile boolean isInitValue = false;

    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty distance = new SimpleDoubleProperty();
    private final StringProperty plannedTime = new SimpleStringProperty();

    public MediaDetailsViewModel() {
        name.addListener( (arg, oldVal, newVal)->updateTourModel());
    }


    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public double getDistance() {
        return distance.get();
    }

    public DoubleProperty distanceProperty() {
        return distance;
    }

    public String getPlannedTime() {
        return plannedTime.get();
    }

    public StringProperty plannedTimeProperty() {
        return plannedTime;
    }

    public void setTourModel(MediaItem mediaItemModel) {
        isInitValue = true;
        if( mediaItemModel ==null ) {
            // select the first in the list
            name.set("");
            distance.set(0.0);
            plannedTime.set("");
            return;
        }
        System.out.println("setTourModel name=" + mediaItemModel.getName() + ", distance=" + mediaItemModel.getDuration() + ", plannedTime=" + mediaItemModel.getContent());
        this.mediaItemModel = mediaItemModel;
        name.setValue( mediaItemModel.getName() );
        distance.set( mediaItemModel.getDuration() );
        plannedTime.set( mediaItemModel.getContent() );
        isInitValue = false;
    }

    private void updateTourModel() {
        if( !isInitValue )
            DAL.getInstance().tourDao().update(mediaItemModel, Arrays.asList(mediaItemModel.getId(), name.get(), distance.get(), plannedTime.get()));
    }


}
