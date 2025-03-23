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
        searchTextField.textProperty().bindBidirectional(searchBarViewModel.searchTextProperty());

        if (searchScopeComboBox != null) {
            searchScopeComboBox.valueProperty().bindBidirectional(searchBarViewModel.searchScopeProperty());
        }

        searchTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onSearchButton(new ActionEvent());
            }
        });

        // Tooltips
        searchTextField.setTooltip(new javafx.scene.control.Tooltip("Enter search text"));
        searchButton.setTooltip(new javafx.scene.control.Tooltip("Search tours and logs"));
        clearButton.setTooltip(new javafx.scene.control.Tooltip("Clear search"));

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