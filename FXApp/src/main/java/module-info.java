module medialib {
    requires javafx.fxml;
    requires java.sql;
    requires medialibbl;
    requires medialibdal;
    requires javafx.web;

    opens at.fhtw.tourplanner.view to javafx.graphics, javafx.fxml;
    exports at.fhtw.tourplanner;
    exports at.fhtw.tourplanner.viewmodel;
    exports at.fhtw.tourplanner.view;
}
