package at.fhtw.tourplanner.bl;

import javafx.scene.web.WebView;

public class MapService {

    public void displayTourMap(WebView webView, String routeGeoJson, String tourName) {
        if (webView == null) return;

        if (routeGeoJson == null || routeGeoJson.trim().isEmpty()) {
            showNoRouteMessage(webView);
            return;
        }

        String html = createSimpleMap(routeGeoJson, tourName);
        webView.getEngine().loadContent(html);
    }

    public void showNoRouteMessage(WebView webView) {
        String html = "<html><body style='display:flex;align-items:center;justify-content:center;height:100vh;font-family:Arial;color:#666;'>" +
                "<div><h3>No Route Available</h3><p>Select a tour to display the map</p></div></body></html>";
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
                        // 1. Create map
                        var map = L.map('map');
                        
                        // 2. Add OpenStreetMap tiles
                        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            attribution: ''
                        }).addTo(map);
                        
                        // Remove attribution control entirely
                        map.attributionControl.remove();
                        
                        // 3. Add route
                        var route = %s;
                        var routeLayer = L.geoJSON(route, {
                            style: { color: 'blue', weight: 4 }
                        }).addTo(map);
                        
                        // 4. Fit map to route
                        map.fitBounds(routeLayer.getBounds());
                        
                        // 5. Add start/end markers
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
                        document.body.innerHTML = '<div style="padding:20px;color:red;">Error loading map: ' + error.message + '</div>';
                    }
                </script>
            </body>
            </html>
            """.formatted(routeGeoJson, tourName, tourName);
    }
}