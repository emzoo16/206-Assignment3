package namesayer.fxmlControllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import namesayer.helperClasses.DatabaseList;
import namesayer.helperClasses.WorkspaceModel;

import java.net.URL;
import java.util.ResourceBundle;

public class DatabaseViewController implements Initializable {
    @FXML
    private ListView listView;
    @FXML
    private TextField searchField;

    private ObservableSet<String> selectedItems;
    private DatabaseList databaseList;
    private FilteredList<String> filteredList;
    private ObservableList<String> searchableItems;

    /**
     * Invoked when the add button is pressed, this method adds all of the selected recordings to the model to be passed
     * to the Playlist creator contorller. After this is done the stage is colsed.
     */
    @FXML
    private void add(ActionEvent event) {
        DatabaseList returnedRecordings = new DatabaseList();
        //Add all the selected names to a databaselist so it can pass them to the model
        for (String recordingName : selectedItems) {
            returnedRecordings.add(recordingName);
        }
        WorkspaceModel model = WorkspaceModel.getInstance();
        //Pass the names to the model
        model.addToCurrentWorkspaceRecordings(returnedRecordings);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        //Notify that the databaseView stage has closed so PlaylistCreator can update the listView
        WorkspaceModel.getInstance().notifyOfStageClose();
        //Close the current stage
        currentStage.close();
    }

    /**
     * Invoked when the back button is pressed, this method closes the stage.
     */
    @FXML
    private void back(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    /**
     * Sets up the list view to work with checkboxes and initializes the search field
     *
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedItems = FXCollections.observableSet();
        databaseList = new DatabaseList();
        //Set the list to display everything in the database
        databaseList.displayAll();
        //Initialize the search list
        searchableItems = databaseList.getRecordingNames();
        filteredList = new FilteredList<>(searchableItems, data -> true);
        setupSearch();
        listView.setItems(filteredList);
        //Add checkboxes
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
                //Allows the GUI to remember everything which has been selected
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
                //Ignores Capitalization
                String lowerCaseSearch = search.toLowerCase();
                return currentValue.toLowerCase().contains(lowerCaseSearch);
            });
        }));
    }
}
