package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.Tour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TourService {

    private static final Logger logger = LogManager.getLogger(TourService.class);
    private final OpenRouteServiceClient apiClient = new OpenRouteServiceClient();

    public Tour createTour() {
        try {
            return DAL.getInstance().tourDao().create();
        } catch (Exception e) {
            logger.error("Failed to create tour", e);
            throw e;
        }
    }

    public List<Tour> getAllTours() {
        return DAL.getInstance().tourDao().getAll();
    }

    public Optional<Tour> getTour(int id) {
        return DAL.getInstance().tourDao().get(id);
    }

    public void updateTour(Tour tour) {
        try {
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
                    logger.info("Updating route data for tour: {}", tour.getName());
                    updateTourWithApiData(tour);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to update tour: {}", tour.getName(), e);
            throw e;
        }
    }

    public void deleteTour(Tour tour) {
        try {
            DAL.getInstance().tourDao().delete(tour);
        } catch (Exception e) {
            logger.error("Failed to delete tour: {}", tour.getName(), e);
            throw e;
        }
    }

    public void refreshTourApiData(Tour tour) {
        logger.info("Refreshing API data for tour: {}", tour.getName());
        updateTourWithApiData(tour);
    }

    public void initializeToursWithApiData() {
        logger.info("Initializing tours with API data");
        List<Tour> tours = getAllTours();
        int updated = 0;

        for (Tour tour : tours) {
            if (!tour.getFrom().isEmpty() && !tour.getTo().isEmpty() && tour.getTourDistance() == 0) {
                updateTourWithApiData(tour);
                updated++;
            }
        }

        logger.info("Initialized {} tours with API data", updated);
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

            tour.setTourDistance(result.getDistance());
            tour.setEstimatedTime(result.getDuration());
            tour.setRouteGeoJson(result.getGeoJson());

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
            logger.error("Failed to update API data for tour: {}", tour.getName(), e);
        }
    }
}