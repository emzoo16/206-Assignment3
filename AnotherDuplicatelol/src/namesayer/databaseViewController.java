package namesayer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class databaseViewController implements Initializable {
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;
    @FXML
    private ListView listView;
    @FXML
    private TextField searchField;

    private ObservableSet<String> selectedItems;
    private DatabaseList databaseList;
    private FilteredList<String> filteredList;
    private ObservableList<String> searchableItems;

    /**
     * Invoked when the add button is pressed
     */
    @FXML
    private void add() {

    }

    /**
     * Invoked when the back button is pressed
     */
    @FXML
    private void back() {

    }

    /**
     * Sets up the list view to work with checkboxes and to search correctly
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedItems = FXCollections.observableSet();
        databaseList = new DatabaseList();
        databaseList.displayAll();
        searchableItems = databaseList.getRecordingNames();
        filteredList = new FilteredList<>(searchableItems, data -> true);
        setupSearch();
        listView.setItems(filteredList);
        listView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) -> {
                    if(isNowSelected) {
                        selectedItems.add(item);
                    } else {
                        selectedItems.remove(item);
                    }
                });
                observable.set(selectedItems.contains(item));
                selectedItems.addListener((SetChangeListener.Change<? extends String> c) ->
                        observable.set(selectedItems.contains(item)));
                return observable;
            }
        }));
    }

    /**
     * Sets up the textfield to work as a search bar for the listView.
     */
    private void setupSearch() {
        searchField.textProperty().addListener(((observable, oldValue, newValue) -> {
            String search = searchField.getText();
            filteredList.setPredicate(currentValue -> {

                //If nothing new has been entered
                if (search == null || search.isEmpty()){
                    return true;
                }
                String lowerCaseSearch = search.toLowerCase();
                return currentValue.toLowerCase().contains(lowerCaseSearch);
            });
        }));
    }
}
