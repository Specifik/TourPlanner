package at.fhtw.tourplanner.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // For @Mock annotation
public class SearchBarViewModelTest {

    private SearchBarViewModel viewModel;

    @Mock
    private SearchBarViewModel.SearchListener mockListener1;
    @Mock
    private SearchBarViewModel.SearchListener mockListener2;

    @BeforeEach
    void setUp() {
        viewModel = new SearchBarViewModel();
        viewModel.addSearchListener(mockListener1);
        viewModel.addSearchListener(mockListener2);
    }

    @Test
    void testDefaultValues() {
        assertEquals("", viewModel.searchTextProperty().get());
        assertEquals("All", viewModel.searchScopeProperty().get());
    }

    @Test
    void search_notifiesListenersWithCurrentValues() {
        viewModel.searchTextProperty().set("Test Query");
        viewModel.searchScopeProperty().set("Tours");

        viewModel.search();

        verify(mockListener1).onSearch("Test Query", "Tours");
        verify(mockListener2).onSearch("Test Query", "Tours");
    }

    @Test
    void clearSearch_clearsSearchTextAndNotifiesListeners() {
        viewModel.searchTextProperty().set("Initial Query");
        viewModel.searchScopeProperty().set("Logs");

        viewModel.clearSearch();

        assertEquals("", viewModel.searchTextProperty().get(), "Search text should be cleared");
        // Search scope remains unchanged by clearSearch, so it should still be "Logs"
        verify(mockListener1).onSearch("", "Logs"); 
        verify(mockListener2).onSearch("", "Logs");
    }

    @Test
    void search_withNoListeners_doesNotThrowException() {
        SearchBarViewModel freshViewModel = new SearchBarViewModel(); // No listeners added
        freshViewModel.searchTextProperty().set("Query");
        
        assertDoesNotThrow(() -> freshViewModel.search());
    }

    @Test
    void searchTextProperty_canBeSetAndGet() {
        viewModel.searchTextProperty().set("New Search Text");
        assertEquals("New Search Text", viewModel.searchTextProperty().get());
    }

    @Test
    void searchScopeProperty_canBeSetAndGet() {
        viewModel.searchScopeProperty().set("Custom Scope");
        assertEquals("Custom Scope", viewModel.searchScopeProperty().get());
    }
} 