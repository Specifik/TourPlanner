package at.fhtw.tourplanner.dal;

import at.fhtw.tourplanner.dal.config.DALConfiguration;
import at.fhtw.tourplanner.model.Tour;

public class DAL {

    private final Dao<Tour> tourDao;
    private final TourLogDao tourLogDao;

    private DAL() {
        // Initialize Spring Boot context
        DALConfiguration.initialize();

        tourDao = new TourDao();
        tourLogDao = new TourLogDao();
    }

    public Dao<Tour> tourDao() {
        return tourDao;
    }

    public TourLogDao tourLogDao() {
        return tourLogDao;
    }

    private static final DAL instance = new DAL();

    public static DAL getInstance() {
        return instance;
    }

    public static void shutdown() {
        DALConfiguration.shutdown();
    }
}