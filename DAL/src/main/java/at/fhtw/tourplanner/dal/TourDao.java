package at.fhtw.tourplanner.dal;

import at.fhtw.tourplanner.model.Tour;

import java.util.*;

public class TourDao implements Dao<Tour> {
    private final List<Tour> tours = new ArrayList<>();
    private int nextId = 1;

    public TourDao() {
        // test data
        tours.add(new Tour(nextId++, "Vienna City Tour", "Stephansplatz", "Schönbrunn Palace"));
        tours.add(new Tour(nextId++, "Danube Island Biking Tour", "Reichsbrücke", "Floridsdorfer Brücke"));
        tours.add(new Tour(nextId++, "Wienerwald Hiking Trip", "Hütteldorf", "Sophienalpe"));
    }

    @Override
    public Optional<Tour> get(int id) {
        return Optional.ofNullable(tours.get(id));
    }

    @Override
    public List<Tour> getAll() {
        return tours;
    }

    @Override
    public Tour create() {
        var tour = new Tour(nextId, "New Media " + nextId,"","");
        tours.add(tour);
        nextId++;
        return tour;
    }

    @Override
    public void update(Tour tour, List<?> params) {
        System.out.println(params);
        tour.setId((Integer) params.get(0));
        tour.setName(Objects.requireNonNull(params.get(1), "Name cannot be null").toString());
        tour.setFrom((params.get(2) == null) ? "" : params.get(2).toString());
        tour.setTo((params.get(3) == null) ? "" : params.get(3).toString());
    }

    @Override
    public void delete(Tour tour) {
        tours.remove(tour);
    }
}
