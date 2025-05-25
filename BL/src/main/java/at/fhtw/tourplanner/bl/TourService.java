package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.Tour;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TourService {

    private final OpenRouteServiceClient apiClient = new OpenRouteServiceClient();

    public Tour createTour() {
        return DAL.getInstance().tourDao().create();
    }

    public List<Tour> getAllTours() {
        return DAL.getInstance().tourDao().getAll();
    }

    public Optional<Tour> getTour(int id) {
        return DAL.getInstance().tourDao().get(id);
    }

    public void updateTour(Tour tour) {
        Optional<Tour> existingOpt = DAL.getInstance().tourDao().get(tour.getId());

        if (existingOpt.isPresent()) {
            Tour existing = existingOpt.get();

            // Check if route fields changed
            boolean needsApiUpdate = !Objects.equals(existing.getFrom(), tour.getFrom()) ||
                    !Objects.equals(existing.getTo(), tour.getTo()) ||
                    !Objects.equals(existing.getTransportType(), tour.getTransportType());

            // Update basic tour data
            DAL.getInstance().tourDao().update(tour, Arrays.asList(
                    tour.getId(),
                    tour.getName(),
                    tour.getFrom(),
                    tour.getTo(),
                    tour.getTransportType(),
                    tour.getDescription()
            ));

            // Update with API data if route changed
            if (needsApiUpdate && !tour.getFrom().isEmpty() && !tour.getTo().isEmpty()) {
                updateTourWithApiData(tour);
            }
        }
    }

    public void deleteTour(Tour tour) {
        DAL.getInstance().tourDao().delete(tour);
    }

    public void refreshTourApiData(Tour tour) {
        updateTourWithApiData(tour);
    }

    public void initializeToursWithApiData() {
        List<Tour> tours = getAllTours();
        for (Tour tour : tours) {
            if (!tour.getFrom().isEmpty() && !tour.getTo().isEmpty() && tour.getTourDistance() == 0) {
                updateTourWithApiData(tour);
            }
        }
    }

    private void updateTourWithApiData(Tour tour) {
        if (tour.getFrom().isEmpty() || tour.getTo().isEmpty()) {
            return;
        }
        try {
            OpenRouteServiceClient.RouteResult result = apiClient.getRoute(
                    tour.getFrom(),
                    tour.getTo(),
                    tour.getTransportType()
            );

            // Update tour with API data
            tour.setTourDistance(result.getDistance());
            tour.setEstimatedTime(result.getDuration());
            tour.setRouteGeoJson(result.getGeoJson());

            if (tour.getRouteGeoJson() != null && tour.getRouteGeoJson().length() > 0) {
                String preview = tour.getRouteGeoJson().length() > 100
                        ? tour.getRouteGeoJson().substring(0, 100) + "..."
                        : tour.getRouteGeoJson();
            }

            DAL.getInstance().tourDao().update(tour, Arrays.asList(
                    tour.getId(),
                    tour.getName(),
                    tour.getFrom(),
                    tour.getTo(),
                    tour.getTransportType(),
                    tour.getDescription(),
                    tour.getTourDistance(),
                    tour.getEstimatedTime(),
                    tour.getRouteGeoJson()
            ));
        } catch (Exception e) {
            System.err.println("Failed to update tour with API data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}