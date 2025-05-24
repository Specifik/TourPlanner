module medialibbl {
    requires medialibdal;
    requires com.fasterxml.jackson.databind;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;

    exports at.fhtw.tourplanner.bl;
}
