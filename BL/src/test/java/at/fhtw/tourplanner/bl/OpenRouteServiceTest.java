package at.fhtw.tourplanner.bl;

public class OpenRouteServiceTest {

    public static void main(String[] args) {
        OpenRouteServiceClient client = new OpenRouteServiceClient();

        System.out.println("Testing OpenRouteService API integration...");

        System.out.println("\n1. Testing API connection...");
        if (client.testConnection()) {
            System.out.println("✓ API connection successful!");
        } else {
            System.out.println("✗ API connection failed! Check your API key.");
            return;
        }

        System.out.println("\n2. Testing geocoding...");
        try {
            OpenRouteServiceClient.Coordinates from = client.geocodeAddress("Stephansplatz Vienna");
            OpenRouteServiceClient.Coordinates to = client.geocodeAddress("Karlsplatz Vienna");

            System.out.println("✓ Geocoding successful!");
            System.out.println("  From: Stephansplatz -> " + from.lat + ", " + from.lon);
            System.out.println("  To: Karlsplatz -> " + to.lat + ", " + to.lon);

        } catch (Exception e) {
            System.out.println("✗ Geocoding failed: " + e.getMessage());
            return;
        }

        System.out.println("\n3. Testing route calculation...");
        try {
            OpenRouteServiceClient.RouteResult result = client.getRoute(
                    "Stephansplatz Vienna",
                    "Karlsplatz Vienna",
                    "Walking"
            );

            System.out.println("✓ Route calculation successful!");
            System.out.println("  Distance: " + String.format("%.2f km", result.getDistance()));
            System.out.println("  Estimated time: " + result.getDuration() + " minutes");
            System.out.println("  GeoJSON length: " + result.getGeoJson().length() + " characters");

        } catch (Exception e) {
            System.out.println("✗ Route calculation failed: " + e.getMessage());
            return;
        }

        System.out.println("\n4. Testing different transport types...");
        String[] transportTypes = {"Walking", "Biking", "Car"};

        for (String transport : transportTypes) {
            try {
                OpenRouteServiceClient.RouteResult result = client.getRoute(
                        "Nussdorf Vienna",
                        "Kahlenberg Vienna",
                        transport
                );

                System.out.println("✓ " + transport + ": " +
                        String.format("%.1f km, %d min", result.getDistance(), result.getDuration()));

            } catch (Exception e) {
                System.out.println("✗ " + transport + " failed: " + e.getMessage());
            }
        }

        System.out.println("\nAPI integration test completed successfully!");
    }
}