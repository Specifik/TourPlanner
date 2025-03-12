module medialib {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;
    requires medialibbl;
    requires medialibdal;

    opens at.fhtw.tourplanner.view to javafx.graphics, javafx.fxml;
    exports at.fhtw.tourplanner;
    exports at.fhtw.tourplanner.viewmodel;
    exports at.fhtw.tourplanner.view;
}
