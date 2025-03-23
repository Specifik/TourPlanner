package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.viewmodel.SearchBarViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class SearchBarController {
    @FXML
    private TextField searchTextField;

    @FXML
    private ComboBox<String> searchScopeComboBox;

    @FXML
    private Button searchButton;

    @FXML
    private Button clearButton;

    private final SearchBarViewModel searchBarViewModel;

    public SearchBarController(SearchBarViewModel searchBarViewModel) {
        this.searchBarViewModel = searchBarViewModel;
    }

    public SearchBarViewModel getSearchBarViewModel() {
        return searchBarViewModel;
    }

    @FXML
    void initialize() {
        // Set up search input binding
        searchTextField.textProperty().bindBidirectional(searchBarViewModel.searchTextProperty());

        // Set up search scope binding if the ComboBox exists
        if (searchScopeComboBox != null) {
            searchScopeComboBox.valueProperty().bindBidirectional(searchBarViewModel.searchScopeProperty());
        }

        // Handle Enter key in search field
        searchTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onSearchButton(new ActionEvent());
            }
        });

        // Set tooltips
        searchTextField.setTooltip(new javafx.scene.control.Tooltip("Enter search text"));
        searchButton.setTooltip(new javafx.scene.control.Tooltip("Search tours and logs"));
        clearButton.setTooltip(new javafx.scene.control.Tooltip("Clear search"));

        // Add placeholder text
        searchTextField.setPromptText("Search tours and logs...");
    }

    @FXML
    void onSearchButton(ActionEvent event) {
        searchBarViewModel.search();
    }

    @FXML
    void onClearButton(ActionEvent event) {
        searchBarViewModel.clearSearch();
    }
}