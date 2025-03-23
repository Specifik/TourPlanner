package at.fhtw.tourplanner.dal;

import at.fhtw.tourplanner.model.Tour;

public class DAL {

    private final Dao<Tour> tourDao;
    private final TourLogDao tourLogDao;

    private DAL() {
        tourDao = new TourDao();
        tourLogDao = new TourLogDao();
    }

    //
    // Tours:
    //
    public Dao<Tour> tourDao() {
        return tourDao;
    }

    //
    // Tour Logs:
    //
    public TourLogDao tourLogDao() {
        return tourLogDao;
    }

    //
    // Singleton-Pattern for DAL with early-binding
    //
    private static final DAL instance = new DAL();

    public static DAL getInstance() {
        return instance;
    }

}