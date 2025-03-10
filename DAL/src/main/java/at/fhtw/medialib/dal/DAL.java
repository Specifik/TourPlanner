package at.fhtw.medialib.dal;

import at.fhtw.medialib.model.MediaItem;

public class DAL {

    private final Dao<MediaItem> tourDao;

    private DAL() {
        tourDao = new MediaItemDao();
    }

    //
    // Tours:
    //
    public Dao<MediaItem> tourDao() {
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
