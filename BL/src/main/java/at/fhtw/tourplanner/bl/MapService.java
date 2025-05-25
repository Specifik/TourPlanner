package at.fhtw.tourplanner.bl;

import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Service for displaying maps within the JavaFX application using WebView
 * Required by specification: "route information (an image with the tour map)" + "OpenStreetMap Tile Server"
 */
public class MapService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    /**
     * Display tour map in the provided WebView component
     * Uses OpenStreetMap Tile Server as required by specification
     */
    public void displayTourMap(WebView mapWebView, String routeGeoJson, String tourName) {
        if (mapWebView == null) {
            System.err.println("WebView component is null");
            return;
        }

        if (routeGeoJson == null || routeGeoJson.trim().isEmpty()) {
            showNoRouteMessage(mapWebView);
            return;
        }

        try {
            // Create temporary HTML file with embedded map
            String htmlContent = createMapHtml(routeGeoJson, tourName);
            File tempMapFile = createTempMapFile(htmlContent);

            // Load the map in WebView
            WebEngine webEngine = mapWebView.getEngine();
            webEngine.load(tempMapFile.toURI().toString());

            System.out.println("✓ Map displayed for tour: " + tourName);
            System.out.println("  Route data length: " + routeGeoJson.length() + " characters");

        } catch (Exception e) {
            System.err.println("Failed to display map: " + e.getMessage());
            showErrorMessage(mapWebView, e.getMessage());
        }
    }

    /**
     * Show message when no route data is available
     */
    public void showNoRouteMessage(WebView mapWebView) {
        String noRouteHtml = createNoRouteHtml();
        try {
            File tempFile = createTempMapFile(noRouteHtml);
            mapWebView.getEngine().load(tempFile.toURI().toString());
        } catch (IOException e) {
            mapWebView.getEngine().loadContent(noRouteHtml);
        }
    }

    /**
     * Create HTML content with Leaflet map using OpenStreetMap tiles
     * Follows specification: OpenStreetMap Tile Server integration
     */
    private String createMapHtml(String routeGeoJson, String tourName) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Tour Map - " + tourName + "</title>\n" +
                "    \n" +
                "    <!-- Leaflet CSS and JS for OpenStreetMap integration -->\n" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\"\n" +
                "          integrity=\"sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=\"\n" +
                "          crossorigin=\"\"/>\n" +
                "    <script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\"\n" +
                "            integrity=\"sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=\"\n" +
                "            crossorigin=\"\"></script>\n" +
                "    \n" +
                "    <style>\n" +
                "        body { \n" +
                "            margin: 0; \n" +
                "            padding: 0; \n" +
                "            font-family: Arial, sans-serif;\n" +
                "        }\n" +
                "        #map { \n" +
                "            height: 100vh; \n" +
                "            width: 100vw; \n" +
                "        }\n" +
                "        .map-title {\n" +
                "            position: absolute;\n" +
                "            top: 10px;\n" +
                "            left: 50%;\n" +
                "            transform: translateX(-50%);\n" +
                "            background: rgba(255, 255, 255, 0.9);\n" +
                "            padding: 5px 15px;\n" +
                "            border-radius: 5px;\n" +
                "            font-weight: bold;\n" +
                "            z-index: 1000;\n" +
                "            box-shadow: 0 2px 5px rgba(0,0,0,0.2);\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"map-title\">" + tourName + "</div>\n" +
                "    <div id=\"map\"></div>\n" +
                "\n" +
                "    <script>\n" +
                "        // Route data from OpenRouteService API\n" +
                "        var directions = " + routeGeoJson + ";\n" +
                "        \n" +
                "        try {\n" +
                "            // 1. Initialize map\n" +
                "            var map = L.map('map');\n" +
                "            \n" +
                "            // 2. Get bounding box and set view\n" +
                "            var bbox = directions.bbox;\n" +
                "            if (bbox && bbox.length >= 4) {\n" +
                "                // Fit map to route bounds\n" +
                "                map.fitBounds([[bbox[1], bbox[0]], [bbox[3], bbox[2]]], {\n" +
                "                    padding: [20, 20] // Add some padding\n" +
                "                });\n" +
                "            } else {\n" +
                "                // Default to Vienna if no bounding box\n" +
                "                map.setView([48.2082, 16.3738], 13);\n" +
                "            }\n" +
                "            \n" +
                "            // 3. Add OpenStreetMap tile layer (as required by specification)\n" +
                "            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "                attribution: '© <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors',\n" +
                "                maxZoom: 19\n" +
                "            }).addTo(map);\n" +
                "            \n" +
                "            // 4. Add route as GeoJSON layer\n" +
                "            var routeLayer = L.geoJSON(directions, {\n" +
                "                style: {\n" +
                "                    color: '#2563eb',           // Blue route line\n" +
                "                    weight: 4,                  // Line thickness\n" +
                "                    opacity: 0.8,               // Line opacity\n" +
                "                    lineCap: 'round',           // Rounded ends\n" +
                "                    lineJoin: 'round'           // Rounded corners\n" +
                "                }\n" +
                "            }).addTo(map);\n" +
                "            \n" +
                "            // 5. Add start and end markers\n" +
                "            if (directions.geometry && directions.geometry.coordinates) {\n" +
                "                var coords = directions.geometry.coordinates;\n" +
                "                if (coords.length > 0) {\n" +
                "                    // Start marker (green)\n" +
                "                    var startCoord = coords[0];\n" +
                "                    L.marker([startCoord[1], startCoord[0]], {\n" +
                "                        title: 'Start'\n" +
                "                    }).addTo(map)\n" +
                "                      .bindPopup('<b>Start</b><br/>" + tourName + "')\n" +
                "                      .openPopup();\n" +
                "                    \n" +
                "                    // End marker (red)\n" +
                "                    if (coords.length > 1) {\n" +
                "                        var endCoord = coords[coords.length - 1];\n" +
                "                        L.marker([endCoord[1], endCoord[0]], {\n" +
                "                            title: 'End'\n" +
                "                        }).addTo(map)\n" +
                "                          .bindPopup('<b>End</b><br/>" + tourName + "');\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "            \n" +
                "            // 6. Add distance and time info\n" +
                "            if (directions.properties && directions.properties.summary) {\n" +
                "                var summary = directions.properties.summary;\n" +
                "                var distance = (summary.distance / 1000).toFixed(2);\n" +
                "                var duration = Math.round(summary.duration / 60);\n" +
                "                \n" +
                "                var infoControl = L.control({position: 'bottomright'});\n" +
                "                infoControl.onAdd = function(map) {\n" +
                "                    var div = L.DomUtil.create('div', 'info');\n" +
                "                    div.style.background = 'rgba(255, 255, 255, 0.9)';\n" +
                "                    div.style.padding = '10px';\n" +
                "                    div.style.borderRadius = '5px';\n" +
                "                    div.style.boxShadow = '0 2px 5px rgba(0,0,0,0.2)';\n" +
                "                    div.innerHTML = '<b>Route Info</b><br/>' + \n" +
                "                                   'Distance: ' + distance + ' km<br/>' +\n" +
                "                                   'Duration: ' + duration + ' min';\n" +
                "                    return div;\n" +
                "                };\n" +
                "                infoControl.addTo(map);\n" +
                "            }\n" +
                "            \n" +
                "        } catch (error) {\n" +
                "            console.error('Error displaying map:', error);\n" +
                "            document.getElementById('map').innerHTML = \n" +
                "                '<div style=\"display: flex; align-items: center; justify-content: center; height: 100%; color: #666;\">' +\n" +
                "                '<div style=\"text-align: center;\">' +\n" +
                "                '<h3>Map Display Error</h3>' +\n" +
                "                '<p>Failed to load route data</p>' +\n" +
                "                '</div></div>';\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * Create HTML for when no route data is available
     */
    private String createNoRouteHtml() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <style>\n" +
                "        body { \n" +
                "            margin: 0; \n" +
                "            padding: 0; \n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f0f0f0;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            height: 100vh;\n" +
                "        }\n" +
                "        .message {\n" +
                "            text-align: center;\n" +
                "            color: #666;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"message\">\n" +
                "        <h3>No Route Data Available</h3>\n" +
                "        <p>Please select a tour with route information to display the map.</p>\n" +
                "        <p><small>Maps are powered by OpenStreetMap</small></p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * Create temporary HTML file for WebView
     */
    private File createTempMapFile(String htmlContent) throws IOException {
        File tempFile = new File(TEMP_DIR, "tourplanner_map_" + System.currentTimeMillis() + ".html");
        tempFile.deleteOnExit(); // Clean up on application exit

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(htmlContent);
        }

        return tempFile;
    }

    /**
     * Show error message in WebView
     */
    private void showErrorMessage(WebView mapWebView, String errorMessage) {
        String errorHtml = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <style>\n" +
                "        body { \n" +
                "            margin: 0; \n" +
                "            padding: 20px; \n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f8f8f8;\n" +
                "        }\n" +
                "        .error {\n" +
                "            color: #d32f2f;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"error\">\n" +
                "        <h3>Map Display Error</h3>\n" +
                "        <p>" + errorMessage + "</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        mapWebView.getEngine().loadContent(errorHtml);
    }
}