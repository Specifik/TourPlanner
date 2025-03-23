package at.fhtw.tourplanner.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class SearchBarViewModel {
    private final StringProperty searchText = new SimpleStringProperty("");
    private final StringProperty searchScope = new SimpleStringProperty("All");

    public interface SearchListener {
        void onSearch(String searchText, String searchScope);
    }

    private final List<SearchListener> searchListeners = new ArrayList<>();

    public StringProperty searchTextProperty() {
        return searchText;
    }

    public StringProperty searchScopeProperty() {
        return searchScope;
    }

    public void addSearchListener(SearchListener listener) {
        searchListeners.add(listener);
    }

    public void search() {
        for (SearchListener listener : searchListeners) {
            listener.onSearch(searchText.get(), searchScope.get());
        }
    }

    public void clearSearch() {
        searchText.set("");
        search();
    }
}