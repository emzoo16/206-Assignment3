package namesayer.fxmlControllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.event.ActionEvent;
import namesayer.helperClasses.DatabaseList;
import namesayer.helperClasses.UIManager;
import namesayer.helperClasses.WorkspaceModel;
import namesayer.interfaces.ConcatenatedRecordingLoader;
import namesayer.interfaces.ParentStageController;
import namesayer.recordingTypes.ConcatenatedRecording;
import namesayer.recordingTypes.DemoRecording;

import java.net.URL;
import java.util.*;

public class PlaylistCreatorController implements Initializable, ConcatenatedRecordingLoader, ParentStageController {
    @FXML
    private ListView listView;
    @FXML
    private TextField inputField;
    @FXML
    private ListView searchView;
    @FXML
    private CheckBox randomiseBox;
    @FXML
    private Button databaseButton;

    private DatabaseList searchList;
    private ObservableList<String> searchableItems;
    private String currentPartialSearch;
    private FilteredList<String> filteredList;

    private DatabaseList playlist;
    private ObservableSet<String> selectedItems;

    private static final int SEARCHVIEW_CELL_HEIGHT = 24;
    private static final int SEARCHVIEW_MAX_HEIGHT = 216;

    private WorkspaceModel model;

    /**
     * Invoked when returnButton is pressed, returns the user to the playscreen
     */
    @FXML
    private void returnToPlaylist() {
        UIManager.changeScenes("fxmlFiles/PlaylistScreen.fxml");
    }

    /**
     * Invoked when addButton is pressed, this adds the current name in the text field to the workspace.
     */
    @FXML
    private void addToPlaylist() {
        //Changes all hyphens to spaces
        String searchedItem = inputField.getText().trim().replaceAll("-", " ");
        //Checks for max length
        if (searchedItem.length() <= 50) {
            //Splits the name by its spaces
            List<String> names = Arrays.asList(searchedItem.split(" "));
            List<String> updatedNames = new ArrayList<>();
            String updatedSearch = "";
            //Ignores empty names, i.e double spaces.
            if (!searchedItem.isEmpty()) {
                //Updates all names to fit internal representation, first letter uppercase and rest lowercase
                for (String name : names) {
                    if (name.length() > 1) {
                        String updatedName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                        updatedNames.add(updatedName);
                        updatedSearch = updatedSearch + updatedName + " ";
                    } else if (name.length() == 1) {
                        String updatedName = name.toUpperCase();
                        updatedNames.add(updatedName);
                        updatedSearch = updatedSearch + updatedName + " ";
                    }
                }
                //trim the extra space off the end
                updatedSearch = updatedSearch.trim();
                //Checks the user isn't creating a duplicate
                if (!alreadyAdded(updatedSearch)) {
                    //if it is a concatenated recording
                    if (searchedItem.contains(" ")) {
                        boolean invalid = false;
                        //Checks all the names are in the database
                        for (String name : updatedNames) {
                            if (!InputExists(name)) {
                                invalid = true;
                                break;
                            }
                        }
                        //If all the names are in the database
                        if (!invalid) {
                            List<String> fileNames = new ArrayList<>();
                            //Gets a list of all file names in the concatenated recording
                            for (String recording : updatedNames) {
                                fileNames.add(searchList.getRecording(recording).getFileName());
                            }
                            //Creates the concatenated recording
                            DemoRecording concatenatedRecording = new ConcatenatedRecording(fileNames, updatedSearch, this);
                            //clears the text field
                            inputField.clear();
                        }
                    } else {
                        //For single length names, checks if they exist
                        if (InputExists(updatedSearch)) {
                            //Adds the name to the playlist and clears the text field
                            playlist.add(updatedSearch);
                            inputField.clear();
                        }
                        //Updates the listView
                        listView.setItems(playlist.getRecordingNames());
                    }
                }
            }
        } else {
            //Warns the user their name is too long
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("The input is too long.");
            alert.setContentText("The maximum number of characters allowed is 50");
            alert.showAndWait();
        }
    }

    /**
     * Checks the  input string is a name in the database
     * @param input the name of the database recording to check
     * @return true if it exists false if it doesn't
     */
    private boolean InputExists(String input) {
        if (searchableItems.contains(input)) {
            return true;
        } else {
            //Warns the user if the name isn't found
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("The name \"" + input + "\" does not appear in the database");
            alert.setContentText("If this is empty you may want to check for double spaces");
            alert.showAndWait();
            return false;
        }
    }

    /**
     * Checks if a name has already been added to the playlist
     * @param recordingName the name of the database recording to check
     * @return true if it has been added, false if it hasn't
     */
    private boolean alreadyAdded(String recordingName) {
        if (playlist.getRecordingNames().contains(recordingName)) {
            //Warns the user the name has already been added
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Incorrect Input");
            alert.setHeaderText("The name \"" + recordingName + "\" has already been added");
            alert.showAndWait();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Invoked when databaseButton is pressed, this model opens the viewDatabase stage
     */
    @FXML
    private void viewDatabase() {
        //set the current controller of the primary stage to this one.
        model.setStageController(this);
        UIManager.openStage("fxmlFiles/DatabaseView.fxml");
    }

    /**
     * Invoked when removeButton is pressed, removes all items selected in the playlist
     */
    @FXML
    private void removeFromPlaylist() {
        for (String recordingName : selectedItems) {
            playlist.remove(recordingName);
        }
        selectedItems.clear();
        listView.setItems(playlist.getRecordingNames());
    }

    /**
     * Invoked when continueButton is pressed, it changes to the workspace scene, after setting the model to
     * give the correct display, (random if the randomise box is ticked)
     */
    @FXML
    private void continueToWorkspace() {
        //Checks the user has added something to the playlist
        if (!playlist.getRecordingNames().isEmpty()) {
            //Sets the models recordings to pass the information to the workspace
            WorkspaceModel.getInstance().setCurrentWorkspaceRecordings(playlist);
            //Sets the display of the workspace to random if randomise box is checked.
            if (randomiseBox.isSelected()) {
                ObservableList<String> randomisedList = playlist.getRecordingNames();
                Collections.shuffle(randomisedList);
                WorkspaceModel.getInstance().setCurrentRecordingsDisplay(randomisedList);
            } else {
                WorkspaceModel.getInstance().setCurrentRecordingsDisplay(playlist.getRecordingNames());
            }
            UIManager.changeScenes("fxmlFiles/Workspace.fxml");
        } else {
            //Warns the user to add at least one recording to the playlist
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid WorkSpace");
            alert.setHeaderText(null);
            alert.setContentText("Please select atleast one recording to add to the workspace");

            alert.showAndWait();
        }
    }

    /**
     * Updates the controller to indicate the concatenated recording has finished loading.
     */
    public void concatenationComplete() {
        playlist = model.getCurrentWorkspaceRecordings();
        listView.setItems(model.getCurrentRecordingsDisplay());
    }

    /**
     * Updates the controller/playlist when the viewDatabase stage is closed
     */
    public void stageHasClosed() {
        //Adds the recordings added in the viewDatabase stage
        playlist = model.getCurrentWorkspaceRecordings();
        listView.setItems(model.getCurrentRecordingsDisplay());
    }

    /**
     * Initializes the playlist to have checkboxes and the input field to have a search list
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Sets the model
        model = WorkspaceModel.getInstance();
        model.setLoadingController(this);

        //Set tooltip hints for user.
        inputField.setTooltip(new Tooltip("Type a name you want to \nadd to the playlist"));
        databaseButton.setTooltip(new Tooltip("Shows a list of all names in the database"));

        //Initializes the searchlist
        searchView.setVisible(false);
        searchList = new DatabaseList();
        searchList.displayAll();
        inputField.setEditable(true);
        searchableItems = searchList.getRecordingNames();
        filteredList = new FilteredList<>(searchableItems, data -> true);
        setupSearch();
        searchView.setItems(filteredList);

        //Adds a listener to the searchlist which changes the size as the list changes.
        filteredList.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                int updatedListSize = filteredList.size() * SEARCHVIEW_CELL_HEIGHT + 2;
                if (updatedListSize > SEARCHVIEW_MAX_HEIGHT) {
                    searchView.setPrefHeight(SEARCHVIEW_MAX_HEIGHT);
                } else {
                    searchView.setPrefHeight(updatedListSize);

                }
            }
        });
        //Initializes keyboard shortcuts
        setupShortcuts();

        //Initializes checkboxes on the playlist view
        selectedItems = FXCollections.observableSet();
        playlist = model.getCurrentWorkspaceRecordings();
        listView.setItems(playlist.getRecordingNames());
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
     * Sets up the search functionality for the input field
     */
    private void setupSearch() {
        inputField.textProperty().addListener(((observable, oldValue, newValue) -> {
            String search = inputField.getText();
            if (search.isEmpty()) {
                searchView.setVisible(false);
            } else {
                searchView.setVisible(true);
            }
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

    /**
     * Sets up keyboard shortcuts to allow for easy manipulation
     */
    private void setupShortcuts() {
        //Makes the searchview work with double clicks on the search list
        searchView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    appendSelectedName();
                }
            }
        });
        //Makes the input field add the name if you press enter and go into the search list if you press down
        inputField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    addToPlaylist();
                } else if (event.getCode().equals(KeyCode.DOWN)) {
                    searchView.requestFocus();
                    searchView.getSelectionModel().select(0);
                }
            }
        });
        //Makes the search list navigate up and down with the arrow keys and return to the inputfield when you move up
        //at the top index.
        searchView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    appendSelectedName();
                } else if (event.getCode().equals(KeyCode.DOWN)) {
                    searchView.getSelectionModel().select(searchView.getSelectionModel().getSelectedIndex());
                } else if (event.getCode().equals(KeyCode.UP)) {
                    if (searchView.getSelectionModel().getSelectedIndex() == 0) {
                        inputField.requestFocus();
                        inputField.end();
                    } else {
                        searchView.getSelectionModel().select(searchView.getSelectionModel().getSelectedIndex());
                    }
                }
            }
        });
    }

    /**
     * Adds the selected item from the searchlist to the input field to allow for autofilling.
     */
    private void appendSelectedName() {
        String updatedSearch;
        String selectedSearch = searchView.getSelectionModel().getSelectedItem() + " ";
        String currentSearch = inputField.getText();
        if (currentSearch.contains(" ")) {
            updatedSearch = currentSearch.substring(0, currentSearch.lastIndexOf(" ") + 1) + selectedSearch;
        } else {
            updatedSearch = selectedSearch;
        }
        inputField.setText(updatedSearch);
        inputField.requestFocus();
        inputField.end();
    }
}