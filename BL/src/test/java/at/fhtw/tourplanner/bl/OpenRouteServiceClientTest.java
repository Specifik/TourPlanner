package at.fhtw.tourplanner.bl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OpenRouteServiceClientTest {

    private OpenRouteServiceClient client;

    @BeforeEach
    void setUp() {
        client = new OpenRouteServiceClient();
    }

    @Test
    void testGetProfileWalking() {
        // Arrange
        String transportType = "Walking";

        // Act
        String result = client.getProfile(transportType);

        // Assert
        assertEquals("foot-walking", result);
    }

    @Test
    void testGetProfileBiking() {
        // Arrange
        String transportType = "Biking";

        // Act
        String result = client.getProfile(transportType);

        // Assert
        assertEquals("cycling-regular", result);
    }

    @Test
    void testGetProfileHiking() {
        // Arrange
        String transportType = "Hiking";

        // Act
        String result = client.getProfile(transportType);

        // Assert
        assertEquals("foot-hiking", result);
    }

    @Test
    void testGetProfileCar() {
        // Arrange
        String transportType = "Car";

        // Act
        String result = client.getProfile(transportType);

        // Assert
        assertEquals("driving-car", result);
    }

    @Test
    void testGetProfileRunning() {
        // Arrange
        String transportType = "Running";

        // Act
        String result = client.getProfile(transportType);

        // Assert
        assertEquals("foot-walking", result);
    }

    @Test
    void testGetProfileUnknown() {
        // Arrange
        String transportType = "Flying";

        // Act
        String result = client.getProfile(transportType);

        // Assert
        assertEquals("driving-car", result);
    }

    @Test
    void testGetProfileNull() {
        // Arrange
        String transportType = null;

        // Act
        String result = client.getProfile(transportType);

        // Assert
        assertEquals("driving-car", result);
    }

    @Test
    void testGetProfileCaseInsensitive() {
        // Arrange
        String transportType = "WALKING";

        // Act
        String result = client.getProfile(transportType);

        // Assert
        assertEquals("foot-walking", result);
    }

    @Test
    void testCoordinatesCreation() {
        // Arrange
        double lon = 16.3738;
        double lat = 48.2082;

        // Act
        OpenRouteServiceClient.Coordinates coords = new OpenRouteServiceClient.Coordinates(lon, lat);

        // Assert
        assertEquals(lon, coords.lon);
        assertEquals(lat, coords.lat);
    }

    @Test
    void testRouteResultCreation() {
        // Arrange
        double distance = 15.5;
        int duration = 90;
        String geoJson = "{\"type\":\"LineString\"}";

        // Act
        OpenRouteServiceClient.RouteResult result = new OpenRouteServiceClient.RouteResult(distance, duration, geoJson);

        // Assert
        assertEquals(distance, result.getDistance());
        assertEquals(duration, result.getDuration());
        assertEquals(geoJson, result.getGeoJson());
    }
}