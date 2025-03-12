package at.fhtw.tourplanner.dal;

import at.fhtw.tourplanner.model.Tour;

public class DAL {

    private final Dao<Tour> tourDao;

    private DAL() {
        tourDao = new TourDao();
    }

    //
    // Tours:
    //
    public Dao<Tour> tourDao() {
        return tourDao;
    }


    //
    // Singleton-Pattern for DAL with early-binding
    //
    private static final DAL instance = new DAL();

    public static DAL getInstance() {
        return instance;
    }

}
