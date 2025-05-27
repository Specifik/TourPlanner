package at.fhtw.tourplanner.DAL.model;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class TourLogTest {

    @Test
    void testTourLogCreation() {
        // Arrange
        Tour tour = new Tour();
        tour.setId(1);
        LocalDateTime dateTime = LocalDateTime.of(2023, 7, 15, 10, 30);
        String comment = "Great weather";
        String difficulty = "Easy";
        double distance = 5.5;
        int time = 45;
        int rating = 4;

        // Act
        TourLog tourLog = new TourLog(tour, dateTime, comment, difficulty, distance, time, rating);
        tourLog.setId(1);

        // Assert
        assertEquals(1, tourLog.getId());
        assertEquals(tour, tourLog.getTour());
        assertEquals(dateTime, tourLog.getDateTime());
        assertEquals(comment, tourLog.getComment());
        assertEquals(difficulty, tourLog.getDifficulty());
        assertEquals(distance, tourLog.getTotalDistance());
        assertEquals(time, tourLog.getTotalTime());
        assertEquals(rating, tourLog.getRating());
    }

    @Test
    void testTourLogModification() {
        // Arrange
        Tour tour = new Tour();
        TourLog tourLog = new TourLog(tour, LocalDateTime.now(), "Original", "Easy", 1.0, 10, 3);
        String newComment = "Updated comment";
        String newDifficulty = "Hard";
        int newRating = 5;

        // Act
        tourLog.setComment(newComment);
        tourLog.setDifficulty(newDifficulty);
        tourLog.setRating(newRating);

        // Assert
        assertEquals(newComment, tourLog.getComment());
        assertEquals(newDifficulty, tourLog.getDifficulty());
        assertEquals(newRating, tourLog.getRating());
    }

    @Test
    void testGetTourId() {
        // Arrange
        Tour tour = new Tour();
        tour.setId(42);
        TourLog tourLog = new TourLog(tour, LocalDateTime.now(), "", "Easy", 0, 0, 1);

        // Act
        int tourId = tourLog.getTourId();

        // Assert
        assertEquals(42, tourId);
    }

    @Test
    void testGetTourIdWithNullTour() {
        // Arrange
        TourLog tourLog = new TourLog(null, LocalDateTime.now(), "", "Easy", 0, 0, 1);

        // Act
        int tourId = tourLog.getTourId();

        // Assert
        assertEquals(0, tourId);
    }

    @Test
    void testTourLogToString() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2023, 7, 15, 10, 30);
        TourLog tourLog = new TourLog(new Tour(), dateTime, "", "Easy", 0, 0, 1);
        tourLog.setId(5);

        // Act
        String result = tourLog.toString();

        // Assert
        assertEquals("Log #5 (2023-07-15)", result);
    }
}