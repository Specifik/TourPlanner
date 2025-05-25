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
    private static final String API_KEY = "5b3ce3597851110001cf6248cc654ebae1004f068c8db0174163a2ce";
    private static final String BASE_URL = "https://api.openrouteservice.org";

    private final ObjectMapper mapper = new ObjectMapper();

    public Coordinates geocodeAddress(String address) throws IOException {
        String url = BASE_URL + "/geocode/search?api_key=" + API_KEY +
                "&text=" + URLEncoder.encode(address + ", Austria", StandardCharsets.UTF_8);

        JsonNode response = makeRequest(url);
        JsonNode features = response.get("features");

        if (features.size() == 0) {
            throw new IOException("Address not found: " + address);
        }

        JsonNode coords = features.get(0).get("geometry").get("coordinates");
        return new Coordinates(coords.get(0).asDouble(), coords.get(1).asDouble());
    }

    public RouteResult getRoute(String fromAddress, String toAddress, String transportType) throws IOException {
        // 1. Get coordinates
        Coordinates from = geocodeAddress(fromAddress);
        Coordinates to = geocodeAddress(toAddress);

        // 2. Get route
        String profile = getProfile(transportType);
        String url = BASE_URL + "/v2/directions/" + profile + "?api_key=" + API_KEY +
                "&start=" + from.lon + "," + from.lat +
                "&end=" + to.lon + "," + to.lat;

        JsonNode response = makeRequest(url);
        JsonNode feature = response.get("features").get(0);
        JsonNode summary = feature.get("properties").get("summary");

        double distance = summary.get("distance").asDouble() / 1000.0;
        int duration = summary.get("duration").asInt() / 60;
        String geoJson = feature.toString();

        return new RouteResult(distance, duration, geoJson);
    }

    public boolean testConnection() {
        try {
            geocodeAddress("Vienna");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private JsonNode makeRequest(String url) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(url))) {

            if (response.getCode() != 200) {
                throw new IOException("API error: " + response.getCode());
            }

            String json = EntityUtils.toString(response.getEntity());
            return mapper.readTree(json);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String getProfile(String transportType) {
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

    public static class Coordinates {
        public final double lon, lat;
        public Coordinates(double lon, double lat) { this.lon = lon; this.lat = lat; }
    }

    public static class RouteResult {
        private final double distance;
        private final int duration;
        private final String geoJson;

        public RouteResult(double distance, int duration, String geoJson) {
            this.distance = distance;
            this.duration = duration;
            this.geoJson = geoJson;
        }

        public double getDistance() { return distance; }
        public int getDuration() { return duration; }
        public String getGeoJson() { return geoJson; }
    }
}