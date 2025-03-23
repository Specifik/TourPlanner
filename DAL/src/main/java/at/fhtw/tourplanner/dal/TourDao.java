package at.fhtw.tourplanner.dal;

import at.fhtw.tourplanner.model.Tour;

import java.util.*;
import java.util.stream.Collectors;

public class TourDao implements Dao<Tour> {
    private final List<Tour> tours = new ArrayList<>();
    private int nextId = 1;

    public TourDao() {
        tours.add(new Tour(nextId++, "Wien Innere Stadt", "Schwedenplatz", "Karlsplatz",
                "Walking", "Kurzer Spaziergang durch die Altstadt"));
        tours.add(new Tour(nextId++, "Donauinsel Radweg", "Donauinsel Nord", "Donauinsel Süd",
                "Biking", "Gemütliche Radtour entlang des Wassers"));
        tours.add(new Tour(nextId++, "Kahlenberg Wanderung", "Nussdorf", "Kahlenberg",
                "Hiking", "Steiler Weg nach oben, aber tolle Aussicht"));
    }

    @Override
    public Optional<Tour> get(int id) {
        return tours.stream()
                .filter(tour -> tour.getId() == id)
                .findFirst();
    }

    @Override
    public List<Tour> getAll() {
        return new ArrayList<>(tours);
    }

    @Override
    public Tour create() {
        var tour = new Tour(nextId, "New Tour " + nextId, "", "");
        tours.add(tour);
        nextId++;
        return tour;
    }

    @Override
    public void update(Tour tour, List<?> params) {
        int id = (Integer) params.get(0);

        Optional<Tour> existingTourOpt = tours.stream()
                .filter(t -> t.getId() == id)
                .findFirst();

        if (existingTourOpt.isPresent()) {
            Tour existingTour = existingTourOpt.get();

            existingTour.setName(Objects.requireNonNull(params.get(1), "Name cannot be null").toString());
            existingTour.setFrom((params.get(2) == null) ? "" : params.get(2).toString());
            existingTour.setTo((params.get(3) == null) ? "" : params.get(3).toString());
            existingTour.setTransportType((params.get(4) == null) ? "" : params.get(4).toString());
            existingTour.setDescription((params.get(5) == null) ? "" : params.get(5).toString());
        }
    }

    @Override
    public void delete(Tour tour) {
        tours.remove(tour);
    }
}