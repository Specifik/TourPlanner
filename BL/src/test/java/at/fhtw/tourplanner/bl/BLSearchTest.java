package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.dal.TourDao;
import at.fhtw.tourplanner.dal.TourLogDao;
import at.fhtw.tourplanner.model.Tour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BLSearchTest {

    private BL bl;
    private DAL mockDal;
    private TourDao mockTourDao;
    private TourLogDao mockTourLogDao;
    private List<Tour> mockTours;

    @BeforeEach
    void setUp() {
        // Create mock objects
        mockDal = mock(DAL.class);
        mockTourDao = mock(TourDao.class);
        mockTourLogDao = mock(TourLogDao.class);

        // Setup mock tours
        Tour tour1 = new Tour(1, "Vienna City Walk", "Stephansplatz", "Karlsplatz");
        Tour tour2 = new Tour(2, "Danube Island Tour", "Donauinsel Nord", "Donauinsel Süd");
        Tour tour3 = new Tour(3, "Kahlenberg Hike", "Nussdorf", "Kahlenberg");

        mockTours = Arrays.asList(tour1, tour2, tour3);

        // Configure mock behavior
        when(mockDal.tourDao()).thenReturn(mockTourDao);
        when(mockDal.tourLogDao()).thenReturn(mockTourLogDao);
        when(mockTourDao.getAll()).thenReturn(mockTours);
    }

    @Test
    void testSearchToursWithEmptySearchText() {
        try (MockedStatic<DAL> dalMockedStatic = Mockito.mockStatic(DAL.class)) {
            dalMockedStatic.when(DAL::getInstance).thenReturn(mockDal);

            // Test with empty search text - should return all tours
            List<Tour> result = bl.findMatchingTours("");

            // Verify that all tours are returned
            assertEquals(3, result.size(), "All tours should be returned for empty search text");
        }
    }

    @Test
    void testSearchToursWithMatchingText() {
        try (MockedStatic<DAL> dalMockedStatic = Mockito.mockStatic(DAL.class)) {
            dalMockedStatic.when(DAL::getInstance).thenReturn(mockDal);

            // Test with text that matches one tour
            List<Tour> result = bl.findMatchingTours("Kahlenberg");

            assertEquals(1, result.size(), "Only one tour should match 'Kahlenberg'");
            assertEquals("Kahlenberg Hike", result.get(0).getName());
        }
    }

    @Test
    void testSearchToursWithNonMatchingText() {
        try (MockedStatic<DAL> dalMockedStatic = Mockito.mockStatic(DAL.class)) {
            dalMockedStatic.when(DAL::getInstance).thenReturn(mockDal);

            // Test with text that matches no tours
            List<Tour> result = bl.findMatchingTours("Mountain");

            assertEquals(0, result.size(), "No tours should match 'Mountain'");
        }
    }
}