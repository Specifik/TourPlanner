module medialib {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;
    requires medialibbl;
    requires medialibdal;

    opens at.fhtw.medialib.view to javafx.graphics, javafx.fxml;
    exports at.fhtw.medialib;
    exports at.fhtw.medialib.viewmodel;
    exports at.fhtw.medialib.view;
}
