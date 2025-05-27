package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.bl.OpenRouteServiceClient.Coordinates;
import at.fhtw.tourplanner.bl.OpenRouteServiceClient.RouteResult;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OpenRouteServiceClientTest {

    @Mock
    private CloseableHttpClient mockHttpClient;
    @Mock
    private CloseableHttpResponse mockHttpResponse;
    // No need to mock HttpEntity if we are creating StringEntity directly in setupMockResponse

    private OpenRouteServiceClient client;

    @BeforeEach
    void setUp() {
        client = new OpenRouteServiceClient(mockHttpClient);
    }

    private void setupMockResponse(String jsonResponse, int statusCode) throws IOException {
        HttpEntity entity = new StringEntity(jsonResponse);
        when(mockHttpResponse.getEntity()).thenReturn(entity);
        when(mockHttpResponse.getCode()).thenReturn(statusCode);
        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockHttpResponse);
    }

    @Test
    void geocodeAddress_success() throws IOException, URISyntaxException {
        String mockJsonResponse = 
            "{\"features\": [{" +
            "\"geometry\": {\"coordinates\": [16.3738, 48.2082]}}" +
            "]}";
        setupMockResponse(mockJsonResponse, 200);

        Coordinates coords = client.geocodeAddress("Vienna");

        assertNotNull(coords);
        assertEquals(16.3738, coords.lon);
        assertEquals(48.2082, coords.lat);

        ArgumentCaptor<HttpGet> argumentCaptor = ArgumentCaptor.forClass(HttpGet.class);
        verify(mockHttpClient).execute(argumentCaptor.capture());
        assertTrue(argumentCaptor.getValue().getUri().toString().contains("geocode/search"));
        assertTrue(argumentCaptor.getValue().getUri().toString().contains("text=Vienna%2C+Austria"));
    }

    @Test
    void geocodeAddress_notFound() throws IOException {
        String mockJsonResponse = "{\"features\": []}";
        setupMockResponse(mockJsonResponse, 200);

        Exception exception = assertThrows(IOException.class, () -> {
            client.geocodeAddress("NonExistentPlace123");
        });
        assertTrue(exception.getMessage().contains("Address not found"));
    }

    @Test
    void geocodeAddress_apiError() throws IOException {
        setupMockResponse("{\"error\":\"API Error\"}", 500); // Provide some valid JSON for error too

        Exception exception = assertThrows(IOException.class, () -> {
            client.geocodeAddress("Vienna");
        });
        assertTrue(exception.getMessage().contains("API error: 500"));
    }

    @Test
    void getRoute_success() throws IOException {
        String geocodeViennaResponse = "{\"features\": [{\"geometry\": {\"coordinates\": [16.37, 48.2]}}]}";
        String geocodeGrazResponse = "{\"features\": [{\"geometry\": {\"coordinates\": [15.43, 47.07]}}]}";
        String routeResponse = 
            "{\"features\": [{" +
            "\"properties\": {\"summary\": {\"distance\": 200000, \"duration\": 7200}}," +
            "\"geometry\": {\"coordinates\": [[16.37,48.2],[15.43,47.07]]}}" +
            "]}";

        HttpEntity viennaEntity = new StringEntity(geocodeViennaResponse);
        HttpEntity grazEntity = new StringEntity(geocodeGrazResponse);
        HttpEntity routeEntity = new StringEntity(routeResponse);

        when(mockHttpResponse.getCode()).thenReturn(200);
        when(mockHttpClient.execute(any(HttpGet.class)))
            .thenReturn(mockHttpResponse); 

        when(mockHttpResponse.getEntity())
            .thenReturn(viennaEntity)
            .thenReturn(grazEntity)
            .thenReturn(routeEntity);
        
        RouteResult result = client.getRoute("Vienna", "Graz", "Car");

        assertNotNull(result);
        assertEquals(200.0, result.getDistance(), 0.01);
        assertEquals(120, result.getDuration());
        assertTrue(result.getGeoJson().contains("16.37,48.2"));
    }
    
    @Test
    void getRoute_geocodingFirstAddressFails() throws IOException {
        String geocodeErrorResponse = "{\"features\": []}";
        setupMockResponse(geocodeErrorResponse, 200);

        Exception exception = assertThrows(IOException.class, () -> {
            client.getRoute("NonExistentPlace1", "Graz", "Car");
        });
        assertTrue(exception.getMessage().contains("Address not found: NonExistentPlace1"));
    }

    @Test
    void getProfile_returnsCorrectProfiles() {
        OpenRouteServiceClient localClient = new OpenRouteServiceClient();
        assertEquals("foot-walking", localClient.getProfile("Walking"));
        assertEquals("cycling-regular", localClient.getProfile("Biking"));
        assertEquals("foot-hiking", localClient.getProfile("Hiking"));
        assertEquals("driving-car", localClient.getProfile("Car"));
        assertEquals("driving-car", localClient.getProfile("UnknownType"));
        assertEquals("driving-car", localClient.getProfile(null));
    }

    @Test
    void testConnection_success() throws IOException {
        String mockJsonResponse = "{\"features\": [{\"geometry\": {\"coordinates\": [16.3738, 48.2082]}}]}";
        setupMockResponse(mockJsonResponse, 200);
        assertTrue(client.testConnection());
    }

    @Test
    void testConnection_failure() throws IOException {
        setupMockResponse("{\"error\":\"Simulated API Error\"}", 500);
        assertFalse(client.testConnection());
    }
} 