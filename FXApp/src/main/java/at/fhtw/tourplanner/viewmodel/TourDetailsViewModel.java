package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.Tour;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Arrays;

public class TourDetailsViewModel {
    private Tour tourModel;
    private volatile boolean isInitValue = false;

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty from = new SimpleStringProperty();
    private final StringProperty to = new SimpleStringProperty();

    public TourDetailsViewModel() {
        name.addListener( (arg, oldVal, newVal)->updateTourModel());
        from.addListener((arg, oldVal, newVal) -> updateTourModel());
        to.addListener((arg, oldVal, newVal) -> updateTourModel());
    }


    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getFrom() {
        return from.get();
    }

    public StringProperty fromProperty() {
        return from;
    }

    public String getTo() {
        return to.get();
    }

    public StringProperty toProperty() {
        return to;
    }

    public void setTourModel(Tour tourModel) {
        isInitValue = true;
        if (tourModel == null) {
            // Reset fields if no tour is selected
            name.set("");
            from.set("");
            to.set("");
            return;
        }

        System.out.println("setTourModel name=" + tourModel.getName() + ", from=" + tourModel.getFrom() + ", plannedTime=" + tourModel.getTo());

        this.tourModel = tourModel;
        name.setValue(tourModel.getName());
        from.setValue(tourModel.getFrom());
        to.setValue(tourModel.getTo());
        isInitValue = false;
    }

    private void updateTourModel() {
        if( !isInitValue && tourModel != null)
            DAL.getInstance().tourDao().update(tourModel, Arrays.asList(
                    tourModel.getId(),
                    name.get(),
                    from.get(),
                    to.get()
            ));
    }
}
