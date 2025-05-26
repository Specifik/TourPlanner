package at.fhtw.tourplanner.dal;

import at.fhtw.tourplanner.dal.config.DALConfiguration;
import at.fhtw.tourplanner.model.Tour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DAL {

    private static final Logger logger = LogManager.getLogger(DAL.class);
    private final Dao<Tour> tourDao;
    private final TourLogDao tourLogDao;

    private DAL() {
        // Initialize Spring Boot context
        DALConfiguration.initialize();
        logger.info("DAL initialized, Spring Context retrieved.");

        tourDao = new TourDao();
        tourLogDao = new TourLogDao();
        logger.debug("TourDao and TourLogDao instantiated.");
    }

    public Dao<Tour> tourDao() {
        return tourDao;
    }

    public TourLogDao tourLogDao() {
        return tourLogDao;
    }

    private static final DAL instance = new DAL();

    public static DAL getInstance() {
        logger.trace("DAL instance requested.");
        return instance;
    }

    public static void shutdown() {
        logger.info("Shutting down DAL and Spring Context.");
        DALConfiguration.shutdown();
    }
}