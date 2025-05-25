package at.fhtw.tourplanner.dal;

import at.fhtw.tourplanner.dal.config.DALConfiguration;
import at.fhtw.tourplanner.dal.repository.TourRepository;
import at.fhtw.tourplanner.model.Tour;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TourDao implements Dao<Tour> {

    private final TourRepository tourRepository;

    public TourDao() {
        ApplicationContext context = DALConfiguration.getContext();
        this.tourRepository = context.getBean(TourRepository.class);

        initializeSampleData(); // if database is empty
    }

    private void initializeSampleData() {
        if (tourRepository.count() == 0) {
            tourRepository.save(new Tour(0, "Wien Innere Stadt", "Schwedenplatz, Vienna", "Karlsplatz, Vienna",
                    "Walking", "Kurzer Spaziergang durch die Altstadt"));
            tourRepository.save(new Tour(0, "Donauinsel Radweg", "Reichsbrücke, Vienna", "Praterbrücke, Vienna",
                    "Biking", "Gemütliche Radtour entlang des Wassers"));
            tourRepository.save(new Tour(0, "Kahlenberg Wanderung", "Nussdorf U-Bahn, Vienna", "Kahlenberg, Vienna",
                    "Hiking", "Steiler Weg nach oben, aber tolle Aussicht"));
        }
    }

    @Override
    public Optional<Tour> get(int id) {
        return tourRepository.findById(id);
    }

    @Override
    public List<Tour> getAll() {
        return tourRepository.findAll();
    }

    @Override
    public Tour create() {
        long count = tourRepository.count();
        Tour tour = new Tour(0, "New Tour " + (count + 1), "", "");
        return tourRepository.save(tour);
    }

    @Override
    public void update(Tour tour, List<?> params) {
        if (params.size() >= 6) {
            int id = (Integer) params.get(0);

            Optional<Tour> existingTourOpt = tourRepository.findById(id);
            if (existingTourOpt.isPresent()) {
                Tour existingTour = existingTourOpt.get();

                // Update basic fields
                existingTour.setName(Objects.requireNonNull(params.get(1), "Name cannot be null").toString());
                existingTour.setFrom((params.get(2) == null) ? "" : params.get(2).toString());
                existingTour.setTo((params.get(3) == null) ? "" : params.get(3).toString());
                existingTour.setTransportType((params.get(4) == null) ? "" : params.get(4).toString());
                existingTour.setDescription((params.get(5) == null) ? "" : params.get(5).toString());

                // Update API data
                if (params.size() >= 9) {
                    try {
                        existingTour.setTourDistance((Double) params.get(6));
                    } catch (ClassCastException e) {
                        existingTour.setTourDistance(Double.parseDouble(params.get(6).toString()));
                    }

                    try {
                        existingTour.setEstimatedTime((Integer) params.get(7));
                    } catch (ClassCastException e) {
                        existingTour.setEstimatedTime(Integer.parseInt(params.get(7).toString()));
                    }

                    existingTour.setRouteGeoJson((params.get(8) == null) ? "" : params.get(8).toString());
                }

                tourRepository.save(existingTour);
            }
        }
    }

    @Override
    public void delete(Tour tour) {
        tourRepository.delete(tour);
    }

    public List<Tour> findBySearchText(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAll();
        }
        return tourRepository.findBySearchText(searchText.trim());
    }

    public List<Tour> findByTourLogSearchText(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return List.of();
        }
        return tourRepository.findByTourLogSearchText(searchText.trim());
    }
}