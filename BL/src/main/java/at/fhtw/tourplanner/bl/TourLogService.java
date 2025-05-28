package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.TourLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TourLogService {

    private static final Logger logger = LogManager.getLogger(TourLogService.class);

    public TourLog createTourLog(int tourId) {
        try {
            logger.info("Creating new tour log for tour ID: {}", tourId);
            return DAL.getInstance().tourLogDao().create(tourId);
        } catch (Exception e) {
            logger.error("Failed to create tour log for tour ID: {}", tourId, e);
            throw e;
        }
    }

    public List<TourLog> getTourLogsByTourId(int tourId) {
        try {
            logger.debug("Getting tour logs for tour ID: {}", tourId);
            return DAL.getInstance().tourLogDao().getByTourId(tourId);
        } catch (Exception e) {
            logger.error("Failed to get tour logs for tour ID: {}", tourId, e);
            throw e;
        }
    }

    public Optional<TourLog> getTourLog(int id) {
        try {
            logger.debug("Getting tour log with ID: {}", id);
            return DAL.getInstance().tourLogDao().get(id);
        } catch (Exception e) {
            logger.error("Failed to get tour log with ID: {}", id, e);
            throw e;
        }
    }

    public void updateTourLog(TourLog tourLog) {
        try {
            logger.info("Updating tour log ID: {}", tourLog.getId());

            // Prepare parameters for the update
            List<Object> params = Arrays.asList(
                    tourLog.getId(),
                    tourLog.getTourId(),
                    tourLog.getDateTime(),
                    tourLog.getComment(),
                    tourLog.getDifficulty(),
                    tourLog.getTotalDistance(),
                    tourLog.getTotalTime(),
                    tourLog.getRating()
            );

            DAL.getInstance().tourLogDao().update(tourLog, params);
            logger.info("Tour log updated successfully: {}", tourLog.getId());
        } catch (Exception e) {
            logger.error("Failed to update tour log ID: {}", tourLog.getId(), e);
            throw e;
        }
    }

    public void deleteTourLog(TourLog tourLog) {
        try {
            logger.info("Deleting tour log ID: {}", tourLog.getId());
            DAL.getInstance().tourLogDao().delete(tourLog);
            logger.info("Tour log deleted successfully: {}", tourLog.getId());
        } catch (Exception e) {
            logger.error("Failed to delete tour log ID: {}", tourLog.getId(), e);
            throw e;
        }
    }

    public List<TourLog> findTourLogsBySearchText(String searchText) {
        try {
            logger.debug("Searching tour logs with text: '{}'", searchText);
            return DAL.getInstance().tourLogDao().findBySearchText(searchText);
        } catch (Exception e) {
            logger.error("Failed to search tour logs with text: '{}'", searchText, e);
            throw e;
        }
    }

    public List<TourLog> getAllTourLogs() {
        try {
            logger.debug("Getting all tour logs");
            return DAL.getInstance().tourLogDao().getAll();
        } catch (Exception e) {
            logger.error("Failed to get all tour logs", e);
            throw e;
        }
    }
}