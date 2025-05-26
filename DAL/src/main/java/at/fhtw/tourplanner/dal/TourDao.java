package at.fhtw.tourplanner.dal;

import at.fhtw.tourplanner.dal.config.DALConfiguration;
import at.fhtw.tourplanner.dal.repository.TourRepository;
import at.fhtw.tourplanner.model.Tour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TourDao implements Dao<Tour> {

    private static final Logger logger = LogManager.getLogger(TourDao.class);
    private final TourRepository tourRepository;

    public TourDao() {
        ApplicationContext context = DALConfiguration.getContext();
        this.tourRepository = context.getBean(TourRepository.class);
        logger.info("TourDao initialized, TourRepository bean acquired.");
    }

    @Override
    public Optional<Tour> get(int id) {
        logger.debug("Attempting to retrieve tour with id: {}", id);
        try {
            Optional<Tour> tour = tourRepository.findById(id);
            if (tour.isPresent()) {
                logger.info("Retrieved tour with id: {}", id);
            } else {
                logger.warn("No tour found with id: {}", id);
            }
            return tour;
        } catch (Exception e) {
            logger.error("Error retrieving tour with id: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<Tour> getAll() {
        logger.debug("Attempting to retrieve all tours.");
        try {
            List<Tour> tours = tourRepository.findAll();
            logger.info("Retrieved {} tours.", tours.size());
            return tours;
        } catch (Exception e) {
            logger.error("Error retrieving all tours.", e);
            throw e;
        }
    }

    @Override
    public Tour create() {
        logger.debug("Attempting to create a new tour.");
        try {
            long count = tourRepository.count();
            Tour tour = new Tour(0, "New Tour " + (count + 1), "", "");
            Tour savedTour = tourRepository.save(tour);
            logger.info("Successfully created new tour with id: {}", savedTour.getId());
            return savedTour;
        } catch (Exception e) {
            logger.error("Error creating new tour.", e);
            throw e;
        }
    }

    @Override
    public void update(Tour tour, List<?> params) {
        logger.debug("Attempting to update tour with id: {}", params.get(0));
        if (params.size() >= 6) {
            int id = (Integer) params.get(0);
            try {
                Optional<Tour> existingTourOpt = tourRepository.findById(id);
                if (existingTourOpt.isPresent()) {
                    Tour existingTour = existingTourOpt.get();
                    logger.trace("Found existing tour: {}", existingTour.getName());

                    // Update basic fields
                    existingTour.setName(Objects.requireNonNull(params.get(1), "Name cannot be null").toString());
                    existingTour.setFrom((params.get(2) == null) ? "" : params.get(2).toString());
                    existingTour.setTo((params.get(3) == null) ? "" : params.get(3).toString());
                    existingTour.setTransportType((params.get(4) == null) ? "" : params.get(4).toString());
                    existingTour.setDescription((params.get(5) == null) ? "" : params.get(5).toString());

                    // Update API data
                    if (params.size() >= 9) {
                        logger.trace("Updating API data for tour id: {}", id);
                        try {
                            existingTour.setTourDistance((Double) params.get(6));
                        } catch (ClassCastException e) {
                            existingTour.setTourDistance(Double.parseDouble(params.get(6).toString()));
                        }
                        try {
                            existingTour.setEstimatedTime((Integer) params.get(7));
                        } catch (ClassCastException e) {
                            existingTour.setEstimatedTime(Integer.parseInt(params.get(7).toString()));
                        }
                        existingTour.setRouteGeoJson((params.get(8) == null) ? "" : params.get(8).toString());
                    }
                    tourRepository.save(existingTour);
                    logger.info("Successfully updated tour with id: {}", id);
                } else {
                    logger.warn("Update failed: No tour found with id: {}", id);
                }
            } catch (Exception e) {
                logger.error("Error updating tour with id: {}", id, e);
                throw e; // Or handle more gracefully depending on requirements
            }
        } else {
            logger.warn("Update tour called with insufficient parameters. Expected at least 6, got {}.", params.size());
        }
    }

    @Override
    public void delete(Tour tour) {
        logger.debug("Attempting to delete tour with id: {}", tour.getId());
        try {
            tourRepository.delete(tour);
            logger.info("Successfully deleted tour with id: {}", tour.getId());
        } catch (Exception e) {
            logger.error("Error deleting tour with id: {}", tour.getId(), e);
            throw e;
        }
    }

    public List<Tour> findBySearchText(String searchText) {
        logger.debug("Searching tours by text: '{}'", searchText);
        try {
            if (searchText == null || searchText.trim().isEmpty()) {
                logger.trace("Search text is empty, returning all tours.");
                return getAll();
            }
            List<Tour> results = tourRepository.findBySearchText(searchText.trim());
            logger.info("Found {} tours matching text: '{}'", results.size(), searchText);
            return results;
        } catch (Exception e) {
            logger.error("Error searching tours by text: '{}'", searchText, e);
            throw e;
        }
    }

    public List<Tour> findByTourLogSearchText(String searchText) {
        logger.debug("Searching tours by tour log text: '{}'", searchText);
        try {
            if (searchText == null || searchText.trim().isEmpty()) {
                logger.trace("Search text is empty, returning empty list for tour log search.");
                return List.of();
            }
            List<Tour> results = tourRepository.findByTourLogSearchText(searchText.trim());
            logger.info("Found {} tours with logs matching text: '{}'", results.size(), searchText);
            return results;
        } catch (Exception e) {
            logger.error("Error searching tours by tour log text: '{}'", searchText, e);
            throw e;
        }
    }
}