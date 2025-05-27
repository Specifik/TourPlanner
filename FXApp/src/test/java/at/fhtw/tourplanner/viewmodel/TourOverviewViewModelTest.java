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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TourOverviewViewModelTest {

    private TourOverviewViewModel viewModel;

    @Mock
    private BL mockBL;

    @Mock
    private TourOverviewViewModel.SelectionChangedListener mockListener;

    @BeforeEach
    void setUp() {
        viewModel = new TourOverviewViewModel();
    }

    @Test
    void testSetTours() {
        // Arrange
        List<Tour> tours = Arrays.asList(
                new Tour(1, "Tour 1", "A", "B"),
                new Tour(2, "Tour 2", "C", "D")
        );

        // Act
        viewModel.setTours(tours);

        // Assert
        assertEquals(2, viewModel.getObservableTours().size());
        assertEquals("Tour 1", viewModel.getObservableTours().get(0).getName());
        assertEquals("Tour 2", viewModel.getObservableTours().get(1).getName());
    }

    @Test
    void testAddSelectionChangedListener() {
        // Arrange
        viewModel.addSelectionChangedListener(mockListener);
        Tour tour = new Tour(1, "Test Tour", "A", "B");

        // Act
        viewModel.setSelectedTour(tour);

        // Assert
        verify(mockListener).changeSelection(tour);
    }

    @Test
    void testRemoveSelectionChangedListener() {
        // Arrange
        viewModel.addSelectionChangedListener(mockListener);
        viewModel.removeSelectionChangedListener(mockListener);
        Tour tour = new Tour(1, "Test Tour", "A", "B");

        // Act
        viewModel.setSelectedTour(tour);

        // Assert
        verify(mockListener, never()).changeSelection(any());
    }
}