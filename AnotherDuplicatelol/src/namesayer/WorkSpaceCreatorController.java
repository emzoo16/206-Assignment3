package namesayer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class WorkSpaceCreatorController implements Initializable {
    @FXML
    ListView<String> databaseRecordingsView;
    @FXML
    ListView<String> workspaceRecordingsView;
    @FXML
    Button addButton;
    @FXML
    Button returnButton;
    @FXML
    Button removeButton;
    @FXML
    Button continueButton;
    @FXML
    CheckBox randomiseBox;
    @FXML
    Button addSearchButton;
    @FXML
    TextField searchField;
    @FXML
    ListView<String> searchList;

    private DatabaseList databaseList;
    private DatabaseList workspaceList;
    private ObservableSet<String> selectedDatabaseItems;
    private ObservableSet<String> selectedWorkspaceItems;

    private DatabaseList searchableList;
    private ObservableList<String> searchableItems;

    private String currentPartialSearch;
    private FilteredList<String> filteredList;

    /*
     * This methods the selected names from the database to the playQueue list. It then removes
     *  the selected items from the database list. 
     */
    @FXML
    private void addToWorkspace() {
        for (String recordingName : selectedDatabaseItems) {
            workspaceList.add(recordingName);
            databaseList.remove(recordingName);
        }
        selectedDatabaseItems.clear();
        databaseRecordingsView.setItems(databaseList.getRecordingNames());
        workspaceRecordingsView.setItems(workspaceList.getRecordingNames());
    }
    
    /*
     * This methods removes selected names from the playQueue list.
     */
    @FXML
    private void removeFromWorkspace() {
        for (String recordingName : selectedWorkspaceItems) {
            if (!recordingName.contains(" ")) {
                workspaceList.remove(recordingName);
                databaseList.add(recordingName);
            } else {
                workspaceList.remove(recordingName);
                //Maybe delete file
            }
        }
        selectedWorkspaceItems.clear();
        workspaceRecordingsView.setItems(workspaceList.getRecordingNames());
        databaseRecordingsView.setItems(databaseList.getRecordingNames());
    }

    @FXML
    private void addSearchedItem() {
        String searchedItem = searchField.getText().trim();
        if (!alreadyAdded(searchedItem)) {
            if (searchedItem.contains(" ")) {
                List<String> names = Arrays.asList(searchedItem.split(" "));
                boolean invalid = false;
                for (String name : names) {
                    if (!InputExists(name)) {
                        invalid = true;
                        break;
                    }
                }
                if (!invalid) {
                    List<String> fileNames = new ArrayList<>();
                    for (String recording : names) {
                        fileNames.add(searchableList.getRecording(recording).getFileName());
                    }
                    DemoRecording concatenatedRecording = new ConcatenatedRecording(fileNames, searchedItem);
                    workspaceList.add(concatenatedRecording);
                    workspaceRecordingsView.setItems(workspaceList.getRecordingNames());
                }
            } else {
                if (InputExists(searchedItem)) {
                    workspaceList.add(searchedItem);
                    databaseList.remove(searchedItem);
                }
                databaseRecordingsView.setItems(databaseList.getRecordingNames());
                workspaceRecordingsView.setItems(workspaceList.getRecordingNames());
            }
        }
    }

    private boolean InputExists(String input) {
        if (searchableItems.contains(input)) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Incorrect Input");
            alert.setHeaderText("The name \"" + input + "\" does not appear in the database");
            alert.setContentText("If this is empty you may want to check for double spaces");
            alert.showAndWait();
            return false;
        }
    }

    private boolean alreadyAdded(String recordingName) {
        if (workspaceList.getRecordingNames().contains(recordingName)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Incorrect Input");
            alert.setHeaderText("The name \"" + recordingName + "\" has already been added");
            alert.showAndWait();
            return true;
        } else {
            return false;
        }
    }

    /*
     * This method takes the user to the workspace scene.
     */
    @FXML
    private void continueToPractice() {
        if (!workspaceList.getRecordingNames().isEmpty()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("workspace.fxml"));
                Parent createScene = fxmlLoader.load();
                WorkSpaceController controller = fxmlLoader.getController();
                if (randomiseBox.isSelected()) {
                    ObservableList<String> randomisedList = workspaceList.getRecordingNames();
                    Collections.shuffle(randomisedList);
                    controller.setWorkspaceRecordingsAndController(workspaceList, randomisedList);
                } else {
                    controller.setWorkspaceRecordingsAndController(workspaceList, workspaceList.getRecordingNames());
                }
                Stage stage = (Stage) continueButton.getScene().getWindow();
                stage.setScene(new Scene(createScene, 700, 500));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid WorkSpace");
            alert.setHeaderText(null);
            alert.setContentText("Please select atleast one recording to add to the workspace");

            alert.showAndWait();
        }
    }

    /**
     * Invoked when the return button is pushed
     */
    @FXML
    private void returnToMenu() {
        try {
            Stage stage = (Stage) returnButton.getScene().getWindow();
            Parent createScene = FXMLLoader.load(getClass().getResource("startMenu.fxml"));
            stage.setScene(new Scene(createScene, 700, 500));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public void setWorkspaceRecordings(DatabaseList list) {
        List<String> keptRecordings = list.getRecordingNames();
        for (String recordingName : keptRecordings) {
            if (recordingName.contains(" ")){
                workspaceList.add(list.getRecording(recordingName));
            } else {
                workspaceList.add(recordingName);
                databaseList.remove(recordingName);
            }
        }
        selectedDatabaseItems.clear();
        databaseRecordingsView.setItems(databaseList.getRecordingNames());
        workspaceRecordingsView.setItems(workspaceList.getRecordingNames());
    }

    private void setupSearchBar() {
        searchField.textProperty().addListener(((observable, oldValue, newValue) -> {
            String search = searchField.getText();
            if (!search.contains(" ") ) {
                currentPartialSearch = search;
            } else {
                if (search.lastIndexOf(" ") == search.length() - 1) {
                    currentPartialSearch = "";
                } else {
                    currentPartialSearch = search.substring(search.lastIndexOf(" ") + 1);
                }
            }

            filteredList.setPredicate(currentValue -> {
                //If nothing new has been entered
                if (currentPartialSearch == null || currentPartialSearch.isEmpty()){
                    return true;
                }
                String lowerCaseSearch = currentPartialSearch.toLowerCase();
                return currentValue.toLowerCase().contains(lowerCaseSearch);
            });
        }));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchableList = new DatabaseList();
        //Sets up the two list views to work with checkboxes
        selectedDatabaseItems = FXCollections.observableSet();
        databaseList = searchableList;
        databaseList.displayAll();
        databaseRecordingsView.setItems(databaseList.getRecordingNames());
        databaseRecordingsView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) -> {
                    if(isNowSelected) {
                        selectedDatabaseItems.add(item);
                    } else {
                        selectedDatabaseItems.remove(item);
                    }
                });
                observable.set(selectedDatabaseItems.contains(item));
                selectedDatabaseItems.addListener((SetChangeListener.Change<? extends String> c) ->
                        observable.set(selectedDatabaseItems.contains(item)));
                return observable;
            }
        }));

        selectedWorkspaceItems = FXCollections.observableSet();
        workspaceList = new DatabaseList();
        workspaceRecordingsView.setItems(workspaceList.getRecordingNames());
        workspaceRecordingsView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) -> {
                        if(isNowSelected) {
                            selectedWorkspaceItems.add(item);
                        } else {
                            selectedWorkspaceItems.remove(item);
                        }
                });
                observable.set(selectedWorkspaceItems.contains(item));
                selectedWorkspaceItems.addListener((SetChangeListener.Change<? extends String> c) ->
                    observable.set(selectedWorkspaceItems.contains(item)));

                return observable;
            }
        }));
        searchableItems = databaseRecordingsView.getItems();
        filteredList = new FilteredList<>(searchableItems, data -> true);
        setupSearchBar();
        searchList.setItems(filteredList);
        searchList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    String updatedSearch;
                    String selectedSearch = searchList.getSelectionModel().getSelectedItem() + " ";
                    String currentSearch = searchField.getText();
                    if (currentSearch.contains(" ")) {
                        updatedSearch = currentSearch.substring(0, currentSearch.lastIndexOf(" ") + 1) + selectedSearch;
                    } else {
                        updatedSearch = selectedSearch;
                    }
                    searchField.setText(updatedSearch);
                    searchField.requestFocus();
                    searchField.end();
                }
            }
        });
    }
}
