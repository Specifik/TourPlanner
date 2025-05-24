package at.fhtw.tourplanner.bl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class OpenRouteServiceClient {
    private static final String BASE_URL = "https://api.openrouteservice.org";
    private static final String API_KEY = "5b3ce3597851110001cf6248cc654ebae1004f068c8db0174163a2ce"; // Replace with your actual API key

    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeocodingResult geocodeAddress(String address) throws IOException {
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String url = BASE_URL + "/geocode/search?api_key=" + API_KEY + "&text=" + encodedAddress + "&boundary.country=AT";

        System.out.println("Geocoding URL: " + url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                System.out.println("Geocoding response: " + jsonResponse);

                JsonNode rootNode = objectMapper.readTree(jsonResponse);

                if (rootNode.has("features") && rootNode.get("features").size() > 0) {
                    JsonNode firstFeature = rootNode.get("features").get(0);
                    JsonNode coordinates = firstFeature.get("geometry").get("coordinates");

                    double longitude = coordinates.get(0).asDouble();
                    double latitude = coordinates.get(1).asDouble();

                    return new GeocodingResult(longitude, latitude, address);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        throw new IOException("Could not geocode address: " + address);
    }

    public DirectionsResult getDirections(double fromLon, double fromLat, double toLon, double toLat, String transportType) throws IOException {
        String profile = convertTransportTypeToProfile(transportType);

        String url = BASE_URL + "/v2/directions/" + profile +
                "?api_key=" + API_KEY +
                "&start=" + fromLon + "," + fromLat +
                "&end=" + toLon + "," + toLat;

        System.out.println("Directions URL: " + url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                System.out.println("Directions response status: " + response.getCode());
                System.out.println("Directions response: " + jsonResponse.substring(0, Math.min(500, jsonResponse.length())));

                if (response.getCode() != 200) {
                    throw new IOException("API returned status: " + response.getCode() + ", Response: " + jsonResponse);
                }

                JsonNode rootNode = objectMapper.readTree(jsonResponse);

                if (rootNode.has("features") && rootNode.get("features").size() > 0) {
                    JsonNode route = rootNode.get("features").get(0);
                    JsonNode properties = route.get("properties");

                    if (properties == null || !properties.has("summary")) {
                        throw new IOException("Route response missing summary data");
                    }

                    JsonNode summary = properties.get("summary");

                    if (summary == null || !summary.has("distance") || !summary.has("duration")) {
                        throw new IOException("Route summary missing distance or duration");
                    }

                    double distance = summary.get("distance").asDouble() / 1000.0; // Convert to km
                    int duration = summary.get("duration").asInt() / 60; // Convert to minutes
                    String geoJson = route.toString();

                    return new DirectionsResult(distance, duration, geoJson);
                } else {
                    throw new IOException("No routes found in response");
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String convertTransportTypeToProfile(String transportType) {
        if (transportType == null) return "driving-car";

        return switch (transportType.toLowerCase()) {
            case "walking" -> "foot-walking";
            case "biking" -> "cycling-regular";
            case "hiking" -> "foot-hiking";
            case "running" -> "foot-walking";
            case "car" -> "driving-car";
            default -> "driving-car";
        };
    }

    public boolean testConnection() {
        try {
            geocodeAddress("Vienna, Austria");
            return true;
        } catch (IOException e) {
            System.err.println("API connection test failed: " + e.getMessage());
            return false;
        }
    }

    // Data classes for API results
    public static class GeocodingResult {
        private final double longitude;
        private final double latitude;
        private final String address;

        public GeocodingResult(double longitude, double latitude, String address) {
            this.longitude = longitude;
            this.latitude = latitude;
            this.address = address;
        }

        public double getLongitude() { return longitude; }
        public double getLatitude() { return latitude; }
        public String getAddress() { return address; }
    }

    public static class DirectionsResult {
        private final double distance;
        private final int estimatedTime;
        private final String geoJson;

        public DirectionsResult(double distance, int estimatedTime, String geoJson) {
            this.distance = distance;
            this.estimatedTime = estimatedTime;
            this.geoJson = geoJson;
        }

        public double getDistance() { return distance; }
        public int getEstimatedTime() { return estimatedTime; }
        public String getGeoJson() { return geoJson; }
    }
}