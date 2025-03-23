package at.fhtw.tourplanner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class TourLogTest {

    private TourLog tourLog;
    private LocalDateTime testDateTime;

    @BeforeEach
    public void setUp() {
        testDateTime = LocalDateTime.of(2023, 7, 25, 17, 45);
        tourLog = new TourLog(1, 5, testDateTime, "Wetter war super, viele andere Radfahrer unterwegs", "Mittel", 8.3, 72, 4);
    }

    @Test
    public void testTourLogCreation() {
        assertEquals(1, tourLog.getId());
        assertEquals(5, tourLog.getTourId());
        assertEquals(testDateTime, tourLog.getDateTime());
        assertEquals("Wetter war super, viele andere Radfahrer unterwegs", tourLog.getComment());
        assertEquals("Mittel", tourLog.getDifficulty());
        assertEquals(8.3, tourLog.getTotalDistance());
        assertEquals(72, tourLog.getTotalTime());
        assertEquals(4, tourLog.getRating());
    }

    @Test
    public void testTourLogModification() {
        LocalDateTime newDateTime = LocalDateTime.of(2023, 8, 14, 9, 30);
        tourLog.setDateTime(newDateTime);
        tourLog.setComment("Heute war es ziemlich heiß, nächstes Mal mehr Wasser mitnehmen");
        tourLog.setDifficulty("Schwer");
        tourLog.setTotalDistance(12.7);
        tourLog.setTotalTime(95);
        tourLog.setRating(3);

        assertEquals(newDateTime, tourLog.getDateTime());
        assertEquals("Heute war es ziemlich heiß, nächstes Mal mehr Wasser mitnehmen", tourLog.getComment());
        assertEquals("Schwer", tourLog.getDifficulty());
        assertEquals(12.7, tourLog.getTotalDistance());
        assertEquals(95, tourLog.getTotalTime());
        assertEquals(3, tourLog.getRating());
    }
}