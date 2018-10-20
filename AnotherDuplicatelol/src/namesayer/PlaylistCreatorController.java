package namesayer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PlaylistCreatorController implements Initializable, ConcatenatedRecordingLoader {
    @FXML
    private Button returnButton;
    @FXML
    private ListView listView;
    @FXML
    private TextField inputField;
    @FXML
    private ListView searchView;
    @FXML
    private CheckBox randomiseBox;
    @FXML
    private Button continueButton;

    private DatabaseList searchList;
    private ObservableList<String> searchableItems;
    private String currentPartialSearch;
    private FilteredList<String> filteredList;

    private DatabaseList playlist;
    private ObservableSet<String> selectedItems;

    private static final int SEARCHVIEW_CELL_HEIGHT = 24;

    /**
     * Invoked when returnButton is pressed
     */
    @FXML
    private void returnToPlaylist() {
        try {
            Stage stage = (Stage) returnButton.getScene().getWindow();
            Parent createScene = FXMLLoader.load(getClass().getResource("playlistScreen.fxml"));
            stage.setScene(new Scene(createScene));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Invoked when addButton is pressed
     */
    @FXML
    private void addToPlaylist() {
        String searchedItem = inputField.getText().trim().replaceAll("-", " ");
        System.out.println(searchedItem.length());
        if (searchedItem.length() <= 50) {
            List<String> names = Arrays.asList(searchedItem.split(" "));
            List<String> updatedNames = new ArrayList<>();
            String updatedSearch = "";
            if (!searchedItem.isEmpty()) {
                for (String name : names) {
                    String updatedName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                    updatedNames.add(updatedName);
                    updatedSearch = updatedSearch + updatedName + " ";
                }
                updatedSearch = updatedSearch.trim();
                if (!alreadyAdded(searchedItem)) {
                    if (searchedItem.contains(" ")) {
                        boolean invalid = false;
                        for (String name : updatedNames) {
                            if (!InputExists(name)) {
                                invalid = true;
                                break;
                            }
                        }
                        if (!invalid) {
                            List<String> fileNames = new ArrayList<>();
                            for (String recording : updatedNames) {
                                fileNames.add(searchList.getRecording(recording).getFileName());
                            }
                            DemoRecording concatenatedRecording = new ConcatenatedRecording(fileNames, updatedSearch, this);
                            inputField.clear();
                        }
                    } else {
                        if (InputExists(updatedSearch)) {
                            playlist.add(updatedSearch);
                            inputField.clear();
                        }
                        listView.setItems(playlist.getRecordingNames());
                    }
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("The input is too long.");
            alert.setContentText("The maximum number of characters allowed is 50");
            alert.showAndWait();
        }
    }

    private boolean InputExists(String input) {
        if (searchableItems.contains(input)) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("The name \"" + input + "\" does not appear in the database");
            alert.setContentText("If this is empty you may want to check for double spaces");
            alert.showAndWait();
            return false;
        }
    }

    private boolean alreadyAdded(String recordingName) {
        if (playlist.getRecordingNames().contains(recordingName)) {
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
     * Invoked when databaseButton is pressed.
     */
    @FXML
    private void viewDatabase(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("databaseView.fxml"));
            Parent databaseParent = fxmlLoader.load();
            DatabaseViewController controller = fxmlLoader.getController();
            controller.setPlaylistCreatorController(this);
            Stage databaseStage = new Stage();
            Scene databaseViewScene = new Scene(databaseParent);
            databaseStage.setScene(databaseViewScene);

            //Disable the background window when the rate stage is displayed.
            databaseStage.initModality(Modality.WINDOW_MODAL);
            databaseStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            databaseStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Invoked when removeButton is pressed
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
     * Invoked when continueButton is pressed
     */
    @FXML
    private void continueToWorkspace() {
        if (!playlist.getRecordingNames().isEmpty()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("workspace.fxml"));
                Parent createScene = fxmlLoader.load();
                WorkSpaceController controller = fxmlLoader.getController();
                if (randomiseBox.isSelected()) {
                    ObservableList<String> randomisedList = playlist.getRecordingNames();
                    Collections.shuffle(randomisedList);
                    controller.setWorkspaceRecordingsAndController(playlist, randomisedList);
                } else {
                    controller.setWorkspaceRecordingsAndController(playlist, playlist.getRecordingNames());
                }
                Stage stage = (Stage) continueButton.getScene().getWindow();
                stage.setScene(new Scene(createScene));
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

    public void addPlaylistRecordings(DatabaseList list) {
        List<String> keptRecordings = list.getRecordingNames();
        for (String recordingName : keptRecordings) {
            playlist.add(list.getRecording(recordingName));
        }
        selectedItems.clear();
        listView.setItems(playlist.getRecordingNames());
    }

    /**
     * Sets up the ComboBox to act as a textfield and search for results on input.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchView.setVisible(false);
        searchList = new DatabaseList();
        searchList.displayAll();
        inputField.setEditable(true);
        searchableItems = searchList.getRecordingNames();
        filteredList = new FilteredList<>(searchableItems, data -> true);
        setupSearch();
        searchView.setItems(filteredList);

        filteredList.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                int updatedListSize = filteredList.size() * SEARCHVIEW_CELL_HEIGHT + 2;
                if (updatedListSize > 216) {
                    searchView.setPrefHeight(216);
                } else {
                    searchView.setPrefHeight(updatedListSize);

                }
            }
        });

        setupShortcuts();

        selectedItems = FXCollections.observableSet();
        playlist = new DatabaseList();
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
