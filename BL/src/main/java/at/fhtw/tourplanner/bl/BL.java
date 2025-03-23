package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BL {
    public List<Tour> findMatchingTours(String searchText) {
        return findMatchingTours(searchText, "All");
    }

    public List<Tour> findMatchingTours(String searchText, String searchScope) {
        System.out.println("Searching with scope: " + searchScope + ", text: " + searchText);

        List<Tour> allTours = DAL.getInstance().tourDao().getAll();

        // If search text is empty, return all tours
        if (searchText == null || searchText.trim().isEmpty()) {
            return allTours;
        }

        String lowerCaseSearchText = searchText.toLowerCase();

        // If scope is "All" or "Tours Only", search in tours
        if ("All".equals(searchScope) || "Tours Only".equals(searchScope)) {
            List<Tour> matchingTours = allTours.stream()
                    .filter(tour -> matchesTour(tour, lowerCaseSearchText))
                    .collect(Collectors.toList());

            // If scope is "All", also include tours with matching logs
            if ("All".equals(searchScope)) {
                Set<Integer> tourIdsWithMatchingLogs = findToursWithMatchingLogs(lowerCaseSearchText);

                // Add tours with matching logs that weren't already included
                for (Tour tour : allTours) {
                    if (tourIdsWithMatchingLogs.contains(tour.getId()) &&
                            !matchingTours.contains(tour)) {
                        matchingTours.add(tour);
                    }
                }
            }

            return matchingTours;
        }

        // If search scope is "Logs Only", find tours that have matching logs
        if ("Logs Only".equals(searchScope)) {
            // Get all tour IDs that have matching logs
            Set<Integer> matchingTourIds = findToursWithMatchingLogs(lowerCaseSearchText);
            System.out.println("Found matching tour IDs in logs: " + matchingTourIds);

            if (matchingTourIds.isEmpty()) {
                return new ArrayList<>();
            }

            // Filter tours by matching IDs
            return allTours.stream()
                    .filter(tour -> matchingTourIds.contains(tour.getId()))
                    .collect(Collectors.toList());
        }

        // Default case - return empty list
        return new ArrayList<>();
    }

    private boolean matchesTour(Tour tour, String searchText) {
        // Match in name
        if (tour.getName() != null && tour.getName().toLowerCase().contains(searchText)) {
            return true;
        }

        // Match in from
        if (tour.getFrom() != null && tour.getFrom().toLowerCase().contains(searchText)) {
            return true;
        }

        // Match in to
        if (tour.getTo() != null && tour.getTo().toLowerCase().contains(searchText)) {
            return true;
        }

        // Match in transport type
        if (tour.getTransportType() != null && tour.getTransportType().toLowerCase().contains(searchText)) {
            return true;
        }

        // Match in description
        if (tour.getDescription() != null && tour.getDescription().toLowerCase().contains(searchText)) {
            return true;
        }

        return false;
    }

    private Set<Integer> findToursWithMatchingLogs(String searchText) {
        Set<Integer> matchingTourIds = new HashSet<>();

        // Get all logs
        List<TourLog> allLogs = DAL.getInstance().tourLogDao().getAll();
        System.out.println("Total logs to search: " + allLogs.size());

        // Find logs that match the search text
        for (TourLog log : allLogs) {
            if (matchesLog(log, searchText)) {
                matchingTourIds.add(log.getTourId());
                System.out.println("Found match in log ID: " + log.getId() + " for tour ID: " + log.getTourId());
            }
        }

        return matchingTourIds;
    }

    private boolean matchesLog(TourLog log, String searchText) {
        // Match in comment
        if (log.getComment() != null && log.getComment().toLowerCase().contains(searchText)) {
            return true;
        }

        // Match in difficulty
        if (log.getDifficulty() != null && log.getDifficulty().toLowerCase().contains(searchText)) {
            return true;
        }

        // Match in date (as string)
        if (log.getDateTime() != null && log.getDateTime().toString().toLowerCase().contains(searchText)) {
            return true;
        }

        // Match in total distance (as string)
        if (String.valueOf(log.getTotalDistance()).contains(searchText)) {
            return true;
        }

        // Match in total time (as string)
        if (String.valueOf(log.getTotalTime()).contains(searchText)) {
            return true;
        }

        // Match in rating (as string)
        if (String.valueOf(log.getRating()).contains(searchText)) {
            return true;
        }

        return false;
    }

    //
    // Singleton-Pattern for BL with early-binding
    //
    private static final BL instance = new BL();

    public static BL getInstance() { return instance; }
}