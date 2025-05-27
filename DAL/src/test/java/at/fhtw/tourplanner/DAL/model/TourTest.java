package at.fhtw.tourplanner.DAL.model;

import at.fhtw.tourplanner.model.Tour;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TourTest {

    @Test
    void testTourCreation() {
        // Arrange
        String name = "Vienna Walk";
        String from = "Stephansplatz";
        String to = "Karlsplatz";
        String transportType = "Walking";
        String description = "Nice city walk";

        // Act
        Tour tour = new Tour(1, name, from, to, transportType, description);

        // Assert
        assertEquals(1, tour.getId());
        assertEquals(name, tour.getName());
        assertEquals(from, tour.getFrom());
        assertEquals(to, tour.getTo());
        assertEquals(transportType, tour.getTransportType());
        assertEquals(description, tour.getDescription());
    }

    @Test
    void testTourModification() {
        // Arrange
        Tour tour = new Tour(1, "Original", "A", "B", "Car", "Desc");
        String newName = "Modified Tour";
        String newFrom = "Vienna";
        String newTo = "Graz";

        // Act
        tour.setName(newName);
        tour.setFrom(newFrom);
        tour.setTo(newTo);

        // Assert
        assertEquals(newName, tour.getName());
        assertEquals(newFrom, tour.getFrom());
        assertEquals(newTo, tour.getTo());
    }

    @Test
    void testTourApiData() {
        // Arrange
        Tour tour = new Tour();
        double distance = 15.5;
        int estimatedTime = 90;
        String geoJson = "{\"type\":\"LineString\"}";

        // Act
        tour.setTourDistance(distance);
        tour.setEstimatedTime(estimatedTime);
        tour.setRouteGeoJson(geoJson);

        // Assert
        assertEquals(distance, tour.getTourDistance());
        assertEquals(estimatedTime, tour.getEstimatedTime());
        assertEquals(geoJson, tour.getRouteGeoJson());
    }

    @Test
    void testTourToStringWithDistance() {
        // Arrange
        Tour tour = new Tour();
        tour.setName("Test Tour");
        tour.setTourDistance(10.5);
        tour.setEstimatedTime(60);

        // Act
        String result = tour.toString();

        // Assert
        assertEquals("Test Tour (10,5 km, 60 min)", result);
    }

    @Test
    void testTourToStringWithoutDistance() {
        // Arrange
        Tour tour = new Tour();
        tour.setName("Test Tour");
        tour.setTourDistance(0);

        // Act
        String result = tour.toString();

        // Assert
        assertEquals("Test Tour", result);
    }
}