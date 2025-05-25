package at.fhtw.tourplanner.dal;

import at.fhtw.tourplanner.dal.config.DALConfiguration;
import at.fhtw.tourplanner.dal.repository.TourLogRepository;
import at.fhtw.tourplanner.dal.repository.TourRepository;
import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TourLogDao {

    private final TourLogRepository tourLogRepository;
    private final TourRepository tourRepository;

    public TourLogDao() {
        ApplicationContext context = DALConfiguration.getContext();
        this.tourLogRepository = context.getBean(TourLogRepository.class);
        this.tourRepository = context.getBean(TourRepository.class);

        initializeSampleData(); // if database is empty
    }

    private void initializeSampleData() {
        if (tourLogRepository.count() == 0) {
            List<Tour> tours = tourRepository.findAll();
            if (!tours.isEmpty()) {
                Tour tour1 = tours.get(0);
                Tour tour2 = tours.size() > 1 ? tours.get(1) : tour1;
                Tour tour3 = tours.size() > 2 ? tours.get(2) : tour1;

                TourLog log1 = new TourLog(0, tour1, LocalDateTime.now().minusDays(7),
                        "War ziemlich heiß, nächstes Mal mehr Wasser mitnehmen", "Leicht", 4.8, 135, 3);
                TourLog log2 = new TourLog(0, tour1, LocalDateTime.now().minusDays(3),
                        "Bei Karlsplatz steht eine Baustelle, musste umgehen", "Leicht", 5.2, 145, 3);
                TourLog log3 = new TourLog(0, tour2, LocalDateTime.now().minusDays(5),
                        "Super Wetter, viele andere Radfahrer unterwegs", "Mittel", 9.3, 82, 4);
                TourLog log4 = new TourLog(0, tour3, LocalDateTime.now().minusDays(2),
                        "Letzte halbe Stunde im Regen, trotzdem cool", "Schwer", 13.2, 215, 4);

                tourLogRepository.save(log1);
                tourLogRepository.save(log2);
                tourLogRepository.save(log3);
                tourLogRepository.save(log4);
            }
        }
    }

    public Optional<TourLog> get(int id) {
        return tourLogRepository.findById(id);
    }

    public List<TourLog> getAll() {
        return tourLogRepository.findAll();
    }

    public List<TourLog> getByTourId(int tourId) {
        return tourLogRepository.findByTour_Id(tourId);
    }

    public TourLog create(int tourId) {
        Optional<Tour> tourOpt = tourRepository.findById(tourId);
        if (tourOpt.isPresent()) {
            Tour tour = tourOpt.get();
            TourLog tourLog = new TourLog(0, tour, LocalDateTime.now(), "", "Leicht", 0.0, 0, 3);
            return tourLogRepository.save(tourLog);
        }
        throw new IllegalArgumentException("Tour with ID " + tourId + " not found");
    }

    public void update(TourLog tourLog, List<?> params) {
        if (params.size() >= 8) {
            int logId = (Integer) params.get(0);
            int tourId = (Integer) params.get(1);

            Optional<TourLog> existingLogOpt = tourLogRepository.findById(logId);
            Optional<Tour> tourOpt = tourRepository.findById(tourId);

            if (existingLogOpt.isPresent() && tourOpt.isPresent()) {
                TourLog existingLog = existingLogOpt.get();
                Tour tour = tourOpt.get();

                existingLog.setTour(tour);
                existingLog.setDateTime((LocalDateTime) params.get(2));
                existingLog.setComment((String) params.get(3));
                existingLog.setDifficulty((String) params.get(4));

                try {
                    existingLog.setTotalDistance((Double) params.get(5));
                } catch (ClassCastException e) {
                    existingLog.setTotalDistance(Double.parseDouble(params.get(5).toString()));
                }

                try {
                    existingLog.setTotalTime((Integer) params.get(6));
                } catch (ClassCastException e) {
                    existingLog.setTotalTime(Integer.parseInt(params.get(6).toString()));
                }

                try {
                    existingLog.setRating((Integer) params.get(7));
                } catch (ClassCastException e) {
                    existingLog.setRating(Integer.parseInt(params.get(7).toString()));
                }

                tourLogRepository.save(existingLog);
            }
        }
    }

    public void delete(TourLog tourLog) {
        tourLogRepository.delete(tourLog);
    }

    public List<TourLog> findBySearchText(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAll();
        }
        return tourLogRepository.findBySearchText(searchText.trim());
    }
}