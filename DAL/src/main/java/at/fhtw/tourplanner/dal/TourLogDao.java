package at.fhtw.tourplanner.dal;

import at.fhtw.tourplanner.model.TourLog;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TourLogDao {
    private final List<TourLog> tourLogs = new ArrayList<>();
    private int nextId = 1;

    public TourLogDao() {
        // Sample test data
        tourLogs.add(new TourLog(nextId++, 1, LocalDateTime.now().minusDays(7),
                "War ziemlich heiß, nächstes Mal mehr Wasser mitnehmen", "Leicht", 4.8, 135, 3));
        tourLogs.add(new TourLog(nextId++, 1, LocalDateTime.now().minusDays(3),
                "Bei Karlsplatz steht eine Baustelle, musste umgehen", "Leicht", 5.2, 145, 3));
        tourLogs.add(new TourLog(nextId++, 2, LocalDateTime.now().minusDays(5),
                "Super Wetter, viele andere Radfahrer unterwegs", "Mittel", 9.3, 82, 4));
        tourLogs.add(new TourLog(nextId++, 3, LocalDateTime.now().minusDays(2),
                "Letzte halbe Stunde im Regen, trotzdem cool", "Schwer", 13.2, 215, 4));
    }

    public Optional<TourLog> get(int id) {
        return tourLogs.stream()
                .filter(log -> log.getId() == id)
                .findFirst();
    }

    public List<TourLog> getAll() {
        return tourLogs;
    }

    public List<TourLog> getByTourId(int tourId) {
        return tourLogs.stream()
                .filter(log -> log.getTourId() == tourId)
                .collect(Collectors.toList());
    }

    public TourLog create(int tourId) {
        var tourLog = new TourLog(nextId, tourId, LocalDateTime.now(), "", "Leicht", 0.0, 0, 3);
        tourLogs.add(tourLog);
        nextId++;
        return tourLog;
    }

    public void update(TourLog tourLog, List<?> params) {
        int logId = (Integer) params.get(0);
        int tourId = (Integer) params.get(1);

        // Find the tour log in our list
        Optional<TourLog> existingLogOpt = tourLogs.stream()
                .filter(log -> log.getId() == logId)
                .findFirst();

        if (existingLogOpt.isPresent()) {
            TourLog existingLog = existingLogOpt.get();

            // Update the existing log with the new values
            existingLog.setTourId(tourId);
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
        }
    }

    public void delete(TourLog tourLog) {
        tourLogs.remove(tourLog);
    }
}