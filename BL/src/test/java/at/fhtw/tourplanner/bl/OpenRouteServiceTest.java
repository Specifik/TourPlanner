package at.fhtw.tourplanner.bl;

/**
 * Simple test class to verify OpenRouteService API integration
 * Run this to test your API key and connection before integrating
 */
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
            OpenRouteServiceClient.GeocodingResult from = client.geocodeAddress("Stephansplatz 1, 1010 Vienna, Austria");
            OpenRouteServiceClient.GeocodingResult to = client.geocodeAddress("Karlsplatz, 1040 Vienna, Austria");

            System.out.println("✓ Geocoding successful!");
            System.out.println("  From: " + from.getAddress() + " -> " + from.getLatitude() + ", " + from.getLongitude());
            System.out.println("  To: " + to.getAddress() + " -> " + to.getLatitude() + ", " + to.getLongitude());

            System.out.println("\n3. Testing directions...");
            OpenRouteServiceClient.DirectionsResult directions = client.getDirections(
                    from.getLongitude(), from.getLatitude(),
                    to.getLongitude(), to.getLatitude(),
                    "Walking"
            );

            System.out.println("✓ Directions successful!");
            System.out.println("  Distance: " + String.format("%.2f km", directions.getDistance()));
            System.out.println("  Estimated time: " + directions.getEstimatedTime() + " minutes");
            System.out.println("  Route data length: " + directions.getGeoJson().length() + " characters");

        } catch (Exception e) {
            System.out.println("✗ Test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nAPI integration test completed!");
    }
}