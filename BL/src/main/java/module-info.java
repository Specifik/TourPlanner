module medialibbl {
    requires medialibdal;
    requires com.fasterxml.jackson.databind;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires javafx.web;
    requires org.apache.logging.log4j;

    exports at.fhtw.tourplanner.bl;
}
