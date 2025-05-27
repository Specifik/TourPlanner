package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.dal.Dao;
import at.fhtw.tourplanner.model.Tour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TourServiceTest {

    private TourService tourService;
    private DAL mockDal;
    private Dao<Tour> mockTourDao;

    @BeforeEach
    void setUp() {
        tourService = new TourService();
        mockDal = mock(DAL.class);
        mockTourDao = mock(Dao.class);
    }

    @Test
    void testCreateTour() {
        // Arrange
        Tour expectedTour = new Tour();
        expectedTour.setId(1);
        expectedTour.setName("New Tour 1");

        try (MockedStatic<DAL> dalMock = Mockito.mockStatic(DAL.class)) {
            dalMock.when(DAL::getInstance).thenReturn(mockDal);
            when(mockDal.tourDao()).thenReturn(mockTourDao);
            when(mockTourDao.create()).thenReturn(expectedTour);

            // Act
            Tour result = tourService.createTour();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals("New Tour 1", result.getName());
            verify(mockTourDao).create();
        }
    }

    @Test
    void testGetAllTours() {
        // Arrange
        List<Tour> expectedTours = Arrays.asList(
                new Tour(1, "Tour 1", "A", "B"),
                new Tour(2, "Tour 2", "C", "D")
        );

        try (MockedStatic<DAL> dalMock = Mockito.mockStatic(DAL.class)) {
            dalMock.when(DAL::getInstance).thenReturn(mockDal);
            when(mockDal.tourDao()).thenReturn(mockTourDao);
            when(mockTourDao.getAll()).thenReturn(expectedTours);

            // Act
            List<Tour> result = tourService.getAllTours();

            // Assert
            assertEquals(2, result.size());
            assertEquals("Tour 1", result.get(0).getName());
            assertEquals("Tour 2", result.get(1).getName());
            verify(mockTourDao).getAll();
        }
    }

    @Test
    void testGetTour() {
        // Arrange
        int tourId = 1;
        Tour expectedTour = new Tour(tourId, "Test Tour", "A", "B");

        try (MockedStatic<DAL> dalMock = Mockito.mockStatic(DAL.class)) {
            dalMock.when(DAL::getInstance).thenReturn(mockDal);
            when(mockDal.tourDao()).thenReturn(mockTourDao);
            when(mockTourDao.get(tourId)).thenReturn(Optional.of(expectedTour));

            // Act
            Optional<Tour> result = tourService.getTour(tourId);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("Test Tour", result.get().getName());
            verify(mockTourDao).get(tourId);
        }
    }

    @Test
    void testGetTourNotFound() {
        // Arrange
        int tourId = 999;

        try (MockedStatic<DAL> dalMock = Mockito.mockStatic(DAL.class)) {
            dalMock.when(DAL::getInstance).thenReturn(mockDal);
            when(mockDal.tourDao()).thenReturn(mockTourDao);
            when(mockTourDao.get(tourId)).thenReturn(Optional.empty());

            // Act
            Optional<Tour> result = tourService.getTour(tourId);

            // Assert
            assertFalse(result.isPresent());
            verify(mockTourDao).get(tourId);
        }
    }

    @Test
    void testDeleteTour() {
        // Arrange
        Tour tour = new Tour(1, "Test Tour", "A", "B");

        try (MockedStatic<DAL> dalMock = Mockito.mockStatic(DAL.class)) {
            dalMock.when(DAL::getInstance).thenReturn(mockDal);
            when(mockDal.tourDao()).thenReturn(mockTourDao);

            // Act
            tourService.deleteTour(tour);

            // Assert
            verify(mockTourDao).delete(tour);
        }
    }
}