package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.Tour;

import java.util.List;
import java.util.stream.Collectors;

public class BL {
    public List<Tour> findMatchingTours(String searchText) {
        var tours = DAL.getInstance().tourDao().getAll();
        if (searchText==null || searchText.isEmpty()) {
            return tours;
        }
        return tours.stream()
                .filter(t -> t.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                        (t.getFrom() != null && t.getFrom().toLowerCase().contains(searchText.toLowerCase())) ||
                        (t.getTo() != null && t.getTo().toLowerCase().contains(searchText.toLowerCase())))
                .collect(Collectors.toList());
    }

    //
    // Singleton-Pattern for BL with early-binding
    //
    private static final BL instance = new BL();

    public static BL getInstance() { return instance; }
}
