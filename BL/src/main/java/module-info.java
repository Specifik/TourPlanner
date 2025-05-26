module medialibbl {
    requires medialibdal;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires javafx.web;
    requires org.apache.logging.log4j;
    requires kernel;
    requires layout;
    requires io;

    exports at.fhtw.tourplanner.bl;
}
