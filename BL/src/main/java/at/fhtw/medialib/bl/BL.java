package at.fhtw.medialib.bl;

import at.fhtw.medialib.dal.DAL;
import at.fhtw.medialib.model.MediaItem;

import java.util.List;
import java.util.stream.Collectors;

public class BL {
    public List<MediaItem> findMatchingTours(String searchText) {
        var tours = DAL.getInstance().tourDao().getAll();
        if (searchText==null || searchText.isEmpty()) {
            return tours;
        }
        return tours.stream()
                .filter(t->t.getName().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
    }

    //
    // Singleton-Pattern for BL with early-binding
    //
    private static final BL instance = new BL();

    public static BL getInstance() { return instance; }
}
