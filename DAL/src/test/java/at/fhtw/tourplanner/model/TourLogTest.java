package at.fhtw.tourplanner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class TourLogTest {

    private TourLog tourLog;
    private LocalDateTime testDateTime;
    private Tour dummyTour;

    @BeforeEach
    public void setUp() {
        testDateTime = LocalDateTime.of(2023, 7, 25, 17, 45);
        dummyTour = new Tour(); // Create a dummy tour for association
        dummyTour.setId(5);     // Set the ID that was previously tourId

        // Using the constructor that takes a Tour object. Rating is 4.
        tourLog = new TourLog(dummyTour, testDateTime, "Wetter war super, viele andere Radfahrer unterwegs", "Mittel", 8.3, 72, 4);
        tourLog.setId(1); // Manually set the TourLog's ID, as this constructor doesn't take it.
    }

    @Test
    public void testTourLogCreation() {
        assertEquals(1, tourLog.getId());
        assertNotNull(tourLog.getTour(), "Tour object should not be null");
        assertEquals(5, tourLog.getTour().getId(), "Tour ID should match");
        assertEquals(testDateTime, tourLog.getDateTime());
        assertEquals("Wetter war super, viele andere Radfahrer unterwegs", tourLog.getComment());
        assertEquals("Mittel", tourLog.getDifficulty());
        assertEquals(8.3, tourLog.getTotalDistance());
        assertEquals(72, tourLog.getTotalTime());
        assertEquals(4, tourLog.getRating()); // Still asserting rating is 4
    }

    @Test
    public void testTourLogModification() {
        LocalDateTime newDateTime = LocalDateTime.of(2023, 8, 14, 9, 30);
        tourLog.setDateTime(newDateTime);
        tourLog.setComment("Heute war es ziemlich heiß, nächstes Mal mehr Wasser mitnehmen");
        tourLog.setDifficulty("Schwer");
        tourLog.setTotalDistance(12.7);
        tourLog.setTotalTime(95);
        tourLog.setRating(3); // Modifying rating

        // Ensure the dummyTour is still associated or re-associate if necessary for context
        // For this test, direct modification of tourLog fields is being tested, tour association is secondary.

        assertEquals(newDateTime, tourLog.getDateTime());
        assertEquals("Heute war es ziemlich heiß, nächstes Mal mehr Wasser mitnehmen", tourLog.getComment());
        assertEquals("Schwer", tourLog.getDifficulty());
        assertEquals(12.7, tourLog.getTotalDistance());
        assertEquals(95, tourLog.getTotalTime());
        assertEquals(3, tourLog.getRating()); // Asserting modified rating
    }
}