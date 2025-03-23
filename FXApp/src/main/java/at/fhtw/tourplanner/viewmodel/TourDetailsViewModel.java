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
    private final StringProperty transportType = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();

    public TourDetailsViewModel() {
        name.addListener((arg, oldVal, newVal) -> updateTourModel());
        from.addListener((arg, oldVal, newVal) -> updateTourModel());
        to.addListener((arg, oldVal, newVal) -> updateTourModel());
        transportType.addListener((arg, oldVal, newVal) -> updateTourModel());
        description.addListener((arg, oldVal, newVal) -> updateTourModel());
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

    public String getTransportType() {
        return transportType.get();
    }

    public StringProperty transportTypeProperty() {
        return transportType;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setTourModel(Tour tourModel) {
        isInitValue = true;
        if (tourModel == null) {
            // Reset fields if no tour is selected
            name.set("");
            from.set("");
            to.set("");
            transportType.set("");
            description.set("");
            return;
        }

        this.tourModel = tourModel;
        name.setValue(tourModel.getName());
        from.setValue(tourModel.getFrom());
        to.setValue(tourModel.getTo());
        transportType.setValue(tourModel.getTransportType());
        description.setValue(tourModel.getDescription());
        isInitValue = false;
    }

    private void updateTourModel() {
        if (!isInitValue && tourModel != null) {
            DAL.getInstance().tourDao().update(tourModel, Arrays.asList(
                    tourModel.getId(),
                    name.get(),
                    from.get(),
                    to.get(),
                    transportType.get(),
                    description.get()
            ));
        }
    }
}