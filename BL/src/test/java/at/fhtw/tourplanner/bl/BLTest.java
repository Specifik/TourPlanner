package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.dal.TourDao;
import at.fhtw.tourplanner.model.Tour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BLTest {

    private BL bl;
    private DAL mockDal;
    private TourDao mockTourDao;

    @BeforeEach
    void setUp() {
        bl = BL.getInstance();
        mockDal = mock(DAL.class);
        mockTourDao = mock(TourDao.class);
    }

    @Test
    void testFindMatchingToursWithEmptySearch() {
        // Arrange
        List<Tour> allTours = Arrays.asList(
                new Tour(1, "Vienna Walk", "A", "B"),
                new Tour(2, "Graz Tour", "C", "D")
        );

        try (MockedStatic<DAL> dalMock = Mockito.mockStatic(DAL.class)) {
            dalMock.when(DAL::getInstance).thenReturn(mockDal);
            when(mockDal.tourDao()).thenReturn(mockTourDao);
            when(mockTourDao.getAll()).thenReturn(allTours);

            // Act
            List<Tour> result = bl.findMatchingTours("");

            // Assert
            assertEquals(2, result.size());
            verify(mockTourDao).getAll();
        }
    }

    @Test
    void testFindMatchingToursAllScope() {
        // Arrange
        String searchText = "Vienna";
        Tour tour1 = new Tour(1, "Vienna Walk", "A", "B");
        Tour tour2 = new Tour(2, "Vienna Tour", "C", "D");
        List<Tour> tourMatches = Arrays.asList(tour1);
        List<Tour> logMatches = Arrays.asList(tour2);

        try (MockedStatic<DAL> dalMock = Mockito.mockStatic(DAL.class)) {
            dalMock.when(DAL::getInstance).thenReturn(mockDal);
            when(mockDal.tourDao()).thenReturn(mockTourDao);
            when(mockTourDao.findBySearchText(searchText)).thenReturn(tourMatches);
            when(mockTourDao.findByTourLogSearchText(searchText)).thenReturn(logMatches);

            // Act
            List<Tour> result = bl.findMatchingTours(searchText, "All");

            // Assert
            assertEquals(2, result.size());
            assertTrue(result.contains(tour1));
            assertTrue(result.contains(tour2));
            verify(mockTourDao).findBySearchText(searchText);
            verify(mockTourDao).findByTourLogSearchText(searchText);
        }
    }

    @Test
    void testFindMatchingToursToursOnlyScope() {
        // Arrange
        String searchText = "Vienna";
        List<Tour> expectedTours = Arrays.asList(new Tour(1, "Vienna Walk", "A", "B"));

        try (MockedStatic<DAL> dalMock = Mockito.mockStatic(DAL.class)) {
            dalMock.when(DAL::getInstance).thenReturn(mockDal);
            when(mockDal.tourDao()).thenReturn(mockTourDao);
            when(mockTourDao.findBySearchText(searchText)).thenReturn(expectedTours);

            // Act
            List<Tour> result = bl.findMatchingTours(searchText, "Tours Only");

            // Assert
            assertEquals(1, result.size());
            assertEquals("Vienna Walk", result.get(0).getName());
            verify(mockTourDao).findBySearchText(searchText);
            verify(mockTourDao, never()).findByTourLogSearchText(any());
        }
    }

    @Test
    void testFindMatchingToursLogsOnlyScope() {
        // Arrange
        String searchText = "difficult";
        List<Tour> expectedTours = Arrays.asList(new Tour(1, "Hard Tour", "A", "B"));

        try (MockedStatic<DAL> dalMock = Mockito.mockStatic(DAL.class)) {
            dalMock.when(DAL::getInstance).thenReturn(mockDal);
            when(mockDal.tourDao()).thenReturn(mockTourDao);
            when(mockTourDao.findByTourLogSearchText(searchText)).thenReturn(expectedTours);

            // Act
            List<Tour> result = bl.findMatchingTours(searchText, "Logs Only");

            // Assert
            assertEquals(1, result.size());
            assertEquals("Hard Tour", result.get(0).getName());
            verify(mockTourDao).findByTourLogSearchText(searchText);
            verify(mockTourDao, never()).findBySearchText(any());
        }
    }

    @Test
    void testFindMatchingToursInvalidScope() {
        // Arrange
        String searchText = "test";

        try (MockedStatic<DAL> dalMock = Mockito.mockStatic(DAL.class)) {
            dalMock.when(DAL::getInstance).thenReturn(mockDal);
            when(mockDal.tourDao()).thenReturn(mockTourDao);

            // Act
            List<Tour> result = bl.findMatchingTours(searchText, "Invalid Scope");

            // Assert
            assertEquals(0, result.size());
            verify(mockTourDao, never()).findBySearchText(any());
            verify(mockTourDao, never()).findByTourLogSearchText(any());
        }
    }

    @Test
    void testFindMatchingToursRemovesDuplicates() {
        // Arrange
        String searchText = "Vienna";
        Tour duplicateTour = new Tour(1, "Vienna Walk", "A", "B");
        List<Tour> tourMatches = Arrays.asList(duplicateTour);
        List<Tour> logMatches = Arrays.asList(duplicateTour); // Same tour

        try (MockedStatic<DAL> dalMock = Mockito.mockStatic(DAL.class)) {
            dalMock.when(DAL::getInstance).thenReturn(mockDal);
            when(mockDal.tourDao()).thenReturn(mockTourDao);
            when(mockTourDao.findBySearchText(searchText)).thenReturn(tourMatches);
            when(mockTourDao.findByTourLogSearchText(searchText)).thenReturn(logMatches);

            // Act
            List<Tour> result = bl.findMatchingTours(searchText, "All");

            // Assert
            assertEquals(1, result.size()); // Should remove duplicate
            assertEquals("Vienna Walk", result.get(0).getName());
        }
    }
}