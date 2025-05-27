package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.bl.BL;
import at.fhtw.tourplanner.model.Tour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TourDetailsViewModelTest {

    private TourDetailsViewModel viewModel;

    @Mock
    private BL mockBL;

    @Mock
    private TourDetailsViewModel.TourUpdatedListener mockListener;

    @BeforeEach
    void setUp() {
        viewModel = new TourDetailsViewModel();
    }

    @Test
    void testSetTourModel() {
        // Arrange
        Tour tour = new Tour(1, "Vienna Walk", "Stephansplatz", "Karlsplatz", "Walking", "Nice walk");

        // Act
        viewModel.setTourModel(tour);

        // Assert
        assertEquals("Vienna Walk", viewModel.getName());
        assertEquals("Stephansplatz", viewModel.getFrom());
        assertEquals("Karlsplatz", viewModel.getTo());
        assertEquals("Walking", viewModel.getTransportType());
        assertEquals("Nice walk", viewModel.getDescription());
    }

    @Test
    void testSetNullTourModel() {
        // Arrange & Act
        viewModel.setTourModel(null);

        // Assert
        assertEquals("", viewModel.getName());
        assertEquals("", viewModel.getFrom());
        assertEquals("", viewModel.getTo());
        assertEquals("", viewModel.getTransportType());
        assertEquals("", viewModel.getDescription());
    }

    @Test
    void testValidationEmptyName() {
        // Arrange
        viewModel.nameProperty().set("");
        viewModel.fromProperty().set("Valid From");
        viewModel.toProperty().set("Valid To");

        // Act & Assert
        assertFalse(viewModel.isValidInput());
        assertTrue(viewModel.getValidationErrors().contains("Tour name cannot be empty"));
    }

    @Test
    void testValidationEmptyFrom() {
        // Arrange
        viewModel.nameProperty().set("Valid Name");
        viewModel.fromProperty().set("");
        viewModel.toProperty().set("Valid To");

        // Act & Assert
        assertFalse(viewModel.isValidInput());
        assertTrue(viewModel.getValidationErrors().contains("Starting location cannot be empty"));
    }

    @Test
    void testValidationEmptyTo() {
        // Arrange
        viewModel.nameProperty().set("Valid Name");
        viewModel.fromProperty().set("Valid From");
        viewModel.toProperty().set("");

        // Act & Assert
        assertFalse(viewModel.isValidInput());
        assertTrue(viewModel.getValidationErrors().contains("Destination cannot be empty"));
    }

    @Test
    void testValidationSameFromAndTo() {
        // Arrange
        viewModel.nameProperty().set("Valid Name");
        viewModel.fromProperty().set("Vienna");
        viewModel.toProperty().set("Vienna");

        // Act & Assert
        assertFalse(viewModel.isValidInput());
        assertTrue(viewModel.getValidationErrors().contains("Starting location and destination cannot be the same"));
    }

    @Test
    void testValidationNameTooLong() {
        // Arrange
        String longName = "a".repeat(101);
        viewModel.nameProperty().set(longName);
        viewModel.fromProperty().set("Valid From");
        viewModel.toProperty().set("Valid To");

        // Act & Assert
        assertFalse(viewModel.isValidInput());
        assertTrue(viewModel.getValidationErrors().contains("Tour name cannot exceed 100 characters"));
    }

    @Test
    void testValidInput() {
        // Arrange
        viewModel.nameProperty().set("Vienna Walk");
        viewModel.fromProperty().set("Stephansplatz");
        viewModel.toProperty().set("Karlsplatz");
        viewModel.transportTypeProperty().set("Walking");
        viewModel.descriptionProperty().set("Nice walk");

        // Act & Assert
        assertTrue(viewModel.isValidInput());
        assertEquals("", viewModel.getValidationErrors());
    }

    @Test
    void testResetToOriginal() {
        // Arrange
        Tour tour = new Tour(1, "Original Name", "Original From", "Original To", "Car", "Original Desc");
        viewModel.setTourModel(tour);

        // Modify values
        viewModel.nameProperty().set("Modified Name");
        viewModel.fromProperty().set("Modified From");

        // Act
        viewModel.resetToOriginal();

        // Assert
        assertEquals("Original Name", viewModel.getName());
        assertEquals("Original From", viewModel.getFrom());
        assertEquals("Original To", viewModel.getTo());
    }

    @Test
    void testSaveTourWithInvalidInput() {
        // Arrange
        Tour tour = new Tour(1, "Test", "A", "B");
        viewModel.setTourModel(tour);
        viewModel.nameProperty().set(""); // Make it invalid

        // Act
        boolean result = viewModel.saveTour();

        // Assert
        assertFalse(result);
    }

    @Test
    void testSaveTourWithNullModel() {
        // Arrange
        viewModel.setTourModel(null);
        viewModel.nameProperty().set("Valid Name");
        viewModel.fromProperty().set("Valid From");
        viewModel.toProperty().set("Valid To");

        // Act
        boolean result = viewModel.saveTour();

        // Assert
        assertFalse(result);
    }

    @Test
    void testAddAndRemoveTourUpdatedListener() {
        // Arrange & Act
        viewModel.addTourUpdatedListener(mockListener);
        viewModel.removeTourUpdatedListener(mockListener);

        // Assert
        assertDoesNotThrow(() -> {
            viewModel.addTourUpdatedListener(mockListener);
            viewModel.removeTourUpdatedListener(mockListener);
        });
    }
}