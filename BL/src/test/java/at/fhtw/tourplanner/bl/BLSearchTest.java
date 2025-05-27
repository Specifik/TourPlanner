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

        // It's important that BL.getInstance() does not trigger real DAL operations
        // that might interfere or happen before/during static mocking.
        // For unit testing BL, it would be better if BL allowed DAOs to be injected.
        // bl = BL.getInstance(); // Delay getting BL instance until DAL is statically mocked in test method

        // Setup mock tours - ensure these have enough fields for a search to potentially work if it were real
        Tour tour1 = new Tour(1, "Vienna City Walk", "Stephansplatz", "Karlsplatz");
        tour1.setDescription("A walk in Vienna.");
        Tour tour2 = new Tour(2, "Danube Island Tour", "Donauinsel Nord", "Donauinsel Süd");
        tour2.setDescription("Tour of Danube Island.");
        Tour tour3 = new Tour(3, "Kahlenberg Hike", "Nussdorf", "Kahlenberg");
        tour3.setDescription("A hike to Kahlenberg.");

        mockTours = Arrays.asList(tour1, tour2, tour3);

        // General DAL setup
        when(mockDal.tourDao()).thenReturn(mockTourDao);
        when(mockDal.tourLogDao()).thenReturn(mockTourLogDao);

        // General TourDao setup (if methods are called outside specific test path)
        // when(mockTourDao.getAll()).thenReturn(mockTours); // This was for a different test
    }

    @Test
    void testSearchToursWithEmptySearchText() {
        // This test implies findMatchingTours(\"\") should return all tours.
        // The BL.findMatchingTours handles empty searchText by calling getAllTours().
        // So, mockTourDao.getAll() needs to be set up.
        try (MockedStatic<DAL> dalMockedStatic = Mockito.mockStatic(DAL.class)) {
            dalMockedStatic.when(DAL::getInstance).thenReturn(mockDal);
            bl = BL.getInstance();

            // BL.findMatchingTours calls getAllTours() if searchText is empty.
            // getAllTours() calls tourService.getAllTours().
            // tourService.getAllTours() calls DAL.getInstance().tourDao().getAll().
            when(mockTourDao.getAll()).thenReturn(mockTours);

            List<Tour> result = bl.findMatchingTours("");

            assertEquals(3, result.size(), "All tours should be returned for empty search text");
        }
    }

    @Test
    void testSearchToursWithMatchingText() {
        // Get BL instance AFTER static DAL mock is in place for the whole test method duration
        try (MockedStatic<DAL> dalMockedStatic = Mockito.mockStatic(DAL.class)) {
            dalMockedStatic.when(DAL::getInstance).thenReturn(mockDal);
            
            bl = BL.getInstance(); // Get BL instance here, now it will use the mocked DAL via DAL.getInstance()

            Tour kahlenbergTour = mockTours.stream()
                                     .filter(t -> "Kahlenberg Hike".equals(t.getName()))
                                     .findFirst()
                                     .orElseThrow(() -> new AssertionError("Kahlenberg tour not found in mock data"));

            // Explicitly mock the calls made by findMatchingTours("Kahlenberg", "All")
            when(mockTourDao.findBySearchText("Kahlenberg")).thenReturn(List.of(kahlenbergTour));
            when(mockTourDao.findByTourLogSearchText("Kahlenberg")).thenReturn(List.of()); // Assuming no log matches

            List<Tour> result = bl.findMatchingTours("Kahlenberg"); // This will use scope "All"

            assertEquals(1, result.size(), "Only one tour should match 'Kahlenberg'");
            assertEquals("Kahlenberg Hike", result.get(0).getName());
        }
    }

    @Test
    void testSearchToursWithNonMatchingText() {
        try (MockedStatic<DAL> dalMockedStatic = Mockito.mockStatic(DAL.class)) {
            dalMockedStatic.when(DAL::getInstance).thenReturn(mockDal);
            bl = BL.getInstance();

            when(mockTourDao.findBySearchText("Mountain")).thenReturn(List.of());
            when(mockTourDao.findByTourLogSearchText("Mountain")).thenReturn(List.of());

            List<Tour> result = bl.findMatchingTours("Mountain");

            assertEquals(0, result.size(), "No tours should match 'Mountain'");
        }
    }
}