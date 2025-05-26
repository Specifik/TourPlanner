package at.fhtw.tourplanner.bl;

import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapService {

    private static final Logger logger = LogManager.getLogger(MapService.class);

    public void displayTourMap(WebView webView, String routeGeoJson, String tourName) {
        if (webView == null) {
            logger.error("WebView is null - cannot display map");
            return;
        }

        if (routeGeoJson == null || routeGeoJson.trim().isEmpty()) {
            showNoRouteMessage(webView);
            return;
        }

        try {
            String html = createSimpleMap(routeGeoJson, tourName != null ? tourName : "Unknown Tour");
            webView.getEngine().loadContent(html);
        } catch (Exception e) {
            logger.error("Failed to display map for tour: {}", tourName, e);
            showErrorMessage(webView, "Error loading map: " + e.getMessage());
        }
    }

    public void showNoRouteMessage(WebView webView) {
        if (webView == null) return;

        String html = "<html><body style='display:flex;align-items:center;justify-content:center;height:100vh;font-family:Arial;color:#666;'>" +
                "<div><h3>No Route Available</h3><p>Select a tour to display the map</p></div></body></html>";
        webView.getEngine().loadContent(html);
    }

    private void showErrorMessage(WebView webView, String errorMessage) {
        String html = "<html><body style='display:flex;align-items:center;justify-content:center;height:100vh;font-family:Arial;color:#d32f2f;'>" +
                "<div><h3>Map Error</h3><p>" + errorMessage + "</p></div></body></html>";
        webView.getEngine().loadContent(html);
    }

    private String createSimpleMap(String routeGeoJson, String tourName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
                <style>
                    body { margin: 0; padding: 0; }
                    #map { height: 100vh; width: 100vw; }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                <script>
                    try {
                        // Create map
                        var map = L.map('map');
                        
                        // Add OpenStreetMap tiles
                        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            attribution: ''
                        }).addTo(map);
                        
                        // Remove attribution control
                        map.attributionControl.remove();
                        
                        // Add route
                        var route = %s;
                        var routeLayer = L.geoJSON(route, {
                            style: { color: 'blue', weight: 4 }
                        }).addTo(map);
                        
                        // Fit map to route
                        map.fitBounds(routeLayer.getBounds());
                        
                        // Add start/end markers
                        var coords = route.geometry.coordinates;
                        if (coords && coords.length > 0) {
                            L.marker([coords[0][1], coords[0][0]])
                                .addTo(map)
                                .bindPopup('Start: %s');
                            L.marker([coords[coords.length-1][1], coords[coords.length-1][0]])
                                .addTo(map)
                                .bindPopup('End: %s');
                        }
                        
                    } catch (error) {
                        console.error('Map error:', error);
                        document.body.innerHTML = '<div style="padding:20px;color:red;">Error loading map: ' + error.message + '</div>';
                    }
                </script>
            </body>
            </html>
            """.formatted(routeGeoJson, tourName, tourName);
    }
}