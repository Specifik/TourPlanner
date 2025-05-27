package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.dal.TourLogDao;
import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TourLogDetailsViewModelTest {

    private TourLogDetailsViewModel viewModel;

    @Mock
    private DAL mockDAL;

    @Mock
    private TourLogDao mockTourLogDao;

    @Mock
    private TourLogDetailsViewModel.CloseListener mockCloseListener;

    @Mock
    private TourLogDetailsViewModel.LogUpdatedListener mockLogUpdatedListener;

    @BeforeEach
    void setUp() {
        viewModel = new TourLogDetailsViewModel();
    }

    @Test
    void testSetTourLogNewLog() {
        // Arrange
        Tour tour = new Tour();
        tour.setId(1);
        TourLog tourLog = new TourLog(tour, LocalDateTime.now(), "Test comment", "Easy", 5.0, 60, 4);
        tourLog.setId(1);

        // Act
        viewModel.setTourLog(tourLog, true);

        // Assert
        assertEquals("Test comment", viewModel.commentProperty().get());
        assertEquals("Easy", viewModel.difficultyProperty().get());
        assertEquals("5.0", viewModel.distanceStringProperty().get());
        assertEquals("60", viewModel.timeStringProperty().get());
        assertEquals(4, viewModel.ratingProperty().get());
    }

    @Test
    void testSetTourLogNull() {
        // Arrange & Act
        viewModel.setTourLog(null, false);

        // Assert
        assertEquals(LocalDate.now(), viewModel.dateProperty().get());
        assertEquals("", viewModel.difficultyProperty().get());
        assertEquals("0.0", viewModel.distanceStringProperty().get());
        assertEquals("0", viewModel.timeStringProperty().get());
        assertEquals(3, viewModel.ratingProperty().get()); // default rating
    }

    @Test
    void testValidationEmptyDate() {
        // Arrange
        viewModel.dateProperty().set(null);
        viewModel.difficultyProperty().set("Easy");
        viewModel.distanceStringProperty().set("5.0");
        viewModel.timeStringProperty().set("60");

        // Act & Assert
        assertFalse(viewModel.isValidInput());
        assertTrue(viewModel.getValidationErrors().contains("Date is required"));
    }

    @Test
    void testValidationFutureDate() {
        // Arrange
        viewModel.dateProperty().set(LocalDate.now().plusDays(1));
        viewModel.difficultyProperty().set("Easy");
        viewModel.distanceStringProperty().set("5.0");
        viewModel.timeStringProperty().set("60");

        // Act & Assert
        assertFalse(viewModel.isValidInput());
        assertTrue(viewModel.getValidationErrors().contains("Date cannot be in the future"));
    }

    @Test
    void testValidationEmptyDifficulty() {
        // Arrange
        viewModel.dateProperty().set(LocalDate.now());
        viewModel.difficultyProperty().set("");
        viewModel.distanceStringProperty().set("5.0");
        viewModel.timeStringProperty().set("60");

        // Act & Assert
        assertFalse(viewModel.isValidInput());
        assertTrue(viewModel.getValidationErrors().contains("Difficulty is required"));
    }

    @Test
    void testValidationInvalidDistance() {
        // Arrange
        viewModel.dateProperty().set(LocalDate.now());
        viewModel.difficultyProperty().set("Easy");
        viewModel.distanceStringProperty().set("invalid");
        viewModel.timeStringProperty().set("60");

        // Act & Assert
        assertFalse(viewModel.isValidInput());
        assertTrue(viewModel.getValidationErrors().contains("Distance must be a valid number"));
    }

    @Test
    void testValidationNegativeDistance() {
        // Arrange
        viewModel.dateProperty().set(LocalDate.now());
        viewModel.difficultyProperty().set("Easy");
        viewModel.distanceStringProperty().set("-5.0");
        viewModel.timeStringProperty().set("60");

        // Act & Assert
        assertFalse(viewModel.isValidInput());
        assertTrue(viewModel.getValidationErrors().contains("Distance cannot be negative"));
    }

    @Test
    void testValidationInvalidTime() {
        // Arrange
        viewModel.dateProperty().set(LocalDate.now());
        viewModel.difficultyProperty().set("Easy");
        viewModel.distanceStringProperty().set("5.0");
        viewModel.timeStringProperty().set("invalid");

        // Act & Assert
        assertFalse(viewModel.isValidInput());
        assertTrue(viewModel.getValidationErrors().contains("Time must be a valid number"));
    }

    @Test
    void testValidationNegativeTime() {
        // Arrange
        viewModel.dateProperty().set(LocalDate.now());
        viewModel.difficultyProperty().set("Easy");
        viewModel.distanceStringProperty().set("5.0");
        viewModel.timeStringProperty().set("-30");

        // Act & Assert
        assertFalse(viewModel.isValidInput());
        assertTrue(viewModel.getValidationErrors().contains("Time cannot be negative"));
    }

    @Test
    void testValidationInvalidRating() {
        // Arrange
        viewModel.dateProperty().set(LocalDate.now());
        viewModel.difficultyProperty().set("Easy");
        viewModel.distanceStringProperty().set("5.0");
        viewModel.timeStringProperty().set("60");
        viewModel.setRating(0); // Invalid rating

        // Act & Assert
        assertFalse(viewModel.isValidInput());
        assertTrue(viewModel.getValidationErrors().contains("Rating must be between 1 and 5"));
    }

    @Test
    void testValidInput() {
        // Arrange
        viewModel.dateProperty().set(LocalDate.now());
        viewModel.difficultyProperty().set("Easy");
        viewModel.distanceStringProperty().set("5.0");
        viewModel.timeStringProperty().set("60");
        viewModel.ratingProperty().set(4);
        viewModel.commentProperty().set("Good day");

        // Act & Assert
        assertTrue(viewModel.isValidInput());
        assertEquals("", viewModel.getValidationErrors());
    }

    @Test
    void testSaveTourLogInvalid() {
        // Arrange
        viewModel.dateProperty().set(null); // Invalid

        // Act
        boolean result = viewModel.saveTourLog();

        // Assert
        assertFalse(result);
    }

    @Test
    void testCloseDetails() {
        // Arrange
        viewModel.addCloseListener(mockCloseListener);

        // Act
        viewModel.closeDetails();

        // Assert
        verify(mockCloseListener).onClose();
    }

    @Test
    void testAddAndRemoveListeners() {
        // Arrange & Act
        viewModel.addCloseListener(mockCloseListener);
        viewModel.addLogUpdatedListener(mockLogUpdatedListener);
        viewModel.removeCloseListener(mockCloseListener);
        viewModel.removeLogUpdatedListener(mockLogUpdatedListener);

        // Assert (no exception should be thrown)
        assertDoesNotThrow(() -> {
            viewModel.closeDetails();
        });
    }

    @Test
    void testSetAutoCloseOnSave() {
        // Arrange & Act
        viewModel.setAutoCloseOnSave(false);

        // Assert (no exception, setter works)
        assertDoesNotThrow(() -> viewModel.setAutoCloseOnSave(true));
    }
}