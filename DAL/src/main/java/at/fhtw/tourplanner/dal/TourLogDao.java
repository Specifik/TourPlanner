package at.fhtw.tourplanner.dal;

import at.fhtw.tourplanner.dal.config.DALConfiguration;
import at.fhtw.tourplanner.dal.repository.TourLogRepository;
import at.fhtw.tourplanner.dal.repository.TourRepository;
import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TourLogDao {

    private static final Logger logger = LogManager.getLogger(TourLogDao.class);
    private final TourLogRepository tourLogRepository;
    private final TourRepository tourRepository;

    public TourLogDao() {
        ApplicationContext context = DALConfiguration.getContext();
        this.tourLogRepository = context.getBean(TourLogRepository.class);
        this.tourRepository = context.getBean(TourRepository.class);
        logger.info("TourLogDao initialized, TourLogRepository and TourRepository beans acquired.");
    }

    public Optional<TourLog> get(int id) {
        logger.debug("Attempting to retrieve tour log with id: {}", id);
        try {
            Optional<TourLog> tourLog = tourLogRepository.findById(id);
            if (tourLog.isPresent()) {
                logger.info("Retrieved tour log with id: {}", id);
            } else {
                logger.warn("No tour log found with id: {}", id);
            }
            return tourLog;
        } catch (Exception e) {
            logger.error("Error retrieving tour log with id: {}", id, e);
            throw e;
        }
    }

    public List<TourLog> getAll() {
        logger.debug("Attempting to retrieve all tour logs.");
        try {
            List<TourLog> tourLogs = tourLogRepository.findAll();
            logger.info("Retrieved {} tour logs.", tourLogs.size());
            return tourLogs;
        } catch (Exception e) {
            logger.error("Error retrieving all tour logs.", e);
            throw e;
        }
    }

    public List<TourLog> getByTourId(int tourId) {
        logger.debug("Attempting to retrieve tour logs for tourId: {}", tourId);
        try {
            List<TourLog> tourLogs = tourLogRepository.findByTour_Id(tourId);
            logger.info("Retrieved {} tour logs for tourId: {}.", tourLogs.size(), tourId);
            return tourLogs;
        } catch (Exception e) {
            logger.error("Error retrieving tour logs for tourId: {}.", tourId, e);
            throw e;
        }
    }

    public TourLog create(int tourId) {
        logger.debug("Attempting to create a new tour log for tourId: {}", tourId);
        try {
            Optional<Tour> tourOpt = tourRepository.findById(tourId);
            if (tourOpt.isPresent()) {
                Tour tour = tourOpt.get();
                TourLog tourLog = new TourLog(0, tour, LocalDateTime.now(), "", "Leicht", 0.0, 0, 3);
                TourLog savedTourLog = tourLogRepository.save(tourLog);
                logger.info("Successfully created new tour log with id: {} for tourId: {}", savedTourLog.getId(), tourId);
                return savedTourLog;
            }
            logger.warn("Cannot create tour log, tour with ID {} not found.", tourId);
            throw new IllegalArgumentException("Tour with ID " + tourId + " not found");
        } catch (IllegalArgumentException e) {
            throw e; // Re-throw known exception
        } catch (Exception e) {
            logger.error("Error creating new tour log for tourId: {}.", tourId, e);
            throw e;
        }
    }

    public void update(TourLog tourLog, List<?> params) {
        // Assuming params.get(0) is logId and params.get(1) is tourId for logging context
        logger.debug("Attempting to update tour log with id: {} for tour id: {}", params.get(0), params.get(1));
        if (params.size() >= 8) {
            int logId = (Integer) params.get(0);
            int tourId = (Integer) params.get(1);
            try {
                Optional<TourLog> existingLogOpt = tourLogRepository.findById(logId);
                Optional<Tour> tourOpt = tourRepository.findById(tourId);

                if (existingLogOpt.isPresent() && tourOpt.isPresent()) {
                    TourLog existingLog = existingLogOpt.get();
                    Tour tour = tourOpt.get();
                    logger.trace("Found existing tour log id: {} and tour id: {}", logId, tourId);

                    existingLog.setTour(tour);
                    existingLog.setDateTime((LocalDateTime) params.get(2));
                    existingLog.setComment((String) params.get(3));
                    existingLog.setDifficulty((String) params.get(4));

                    try {
                        existingLog.setTotalDistance((Double) params.get(5));
                    } catch (ClassCastException e) {
                        existingLog.setTotalDistance(Double.parseDouble(params.get(5).toString()));
                    }
                    try {
                        existingLog.setTotalTime((Integer) params.get(6));
                    } catch (ClassCastException e) {
                        existingLog.setTotalTime(Integer.parseInt(params.get(6).toString()));
                    }
                    try {
                        existingLog.setRating((Integer) params.get(7));
                    } catch (ClassCastException e) {
                        existingLog.setRating(Integer.parseInt(params.get(7).toString()));
                    }

                    tourLogRepository.save(existingLog);
                    logger.info("Successfully updated tour log with id: {}", logId);
                } else {
                    if (!existingLogOpt.isPresent()) {
                        logger.warn("Update failed: No tour log found with id: {}", logId);
                    }
                    if (!tourOpt.isPresent()) {
                        logger.warn("Update failed: No tour found with id: {} for tour log {}", tourId, logId);
                    }
                }
            } catch (Exception e) {
                logger.error("Error updating tour log with id: {}", logId, e);
                // Consider if re-throwing is always appropriate or if some exceptions can be handled
            }
        } else {
            logger.warn("Update tour log called with insufficient parameters. Expected at least 8, got {}.", params.size());
        }
    }

    public void delete(TourLog tourLog) {
        logger.debug("Attempting to delete tour log with id: {}", tourLog.getId());
        try {
            tourLogRepository.delete(tourLog);
            logger.info("Successfully deleted tour log with id: {}", tourLog.getId());
        } catch (Exception e) {
            logger.error("Error deleting tour log with id: {}", tourLog.getId(), e);
            throw e;
        }
    }

    public List<TourLog> findBySearchText(String searchText) {
        logger.debug("Searching tour logs by text: '{}'", searchText);
        try {
            if (searchText == null || searchText.trim().isEmpty()) {
                logger.trace("Search text is empty, returning all tour logs.");
                return getAll();
            }
            List<TourLog> results = tourLogRepository.findBySearchText(searchText.trim());
            logger.info("Found {} tour logs matching text: '{}'", results.size(), searchText);
            return results;
        } catch (Exception e) {
            logger.error("Error searching tour logs by text: '{}'", searchText, e);
            throw e;
        }
    }

    // save pre-populated tour log from import
    public TourLog save(TourLog tourLog) {
        logger.debug("Attempting to save tour log for tour: {}", tourLog.getTour() != null ? tourLog.getTour().getName() : "null");
        try {
            // ID should be 0 if it is a new log from import -> Spring Data JPA will handle it
            TourLog savedTourLog = tourLogRepository.save(tourLog);
            logger.info("Successfully saved tour log with id: {} for tour id: {}", savedTourLog.getId(), savedTourLog.getTour().getId());
            return savedTourLog;
        } catch (Exception e) {
            logger.error("Error saving tour log for tour: {}", tourLog.getTour() != null ? tourLog.getTour().getName() : "null", e);
            throw e;
        }
    }
}