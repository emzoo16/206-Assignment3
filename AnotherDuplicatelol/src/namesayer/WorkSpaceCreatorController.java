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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

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
        if (searchedItem.contains(" ")) {
            List<String> names = Arrays.asList(searchedItem.split(" "));
            boolean invalid = false;
            for (String name : names) {
                if (!InputExists(name)) {
                    invalid = true;
                    break;
                }
            }
            createAndAddConcatenatedRecording(names, searchedItem);
        } else {
            if (InputExists(searchedItem)) {
                workspaceList.add(searchedItem);
                databaseList.remove(searchedItem);
            }
            databaseRecordingsView.setItems(databaseList.getRecordingNames());
            workspaceRecordingsView.setItems(workspaceList.getRecordingNames());
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

    private void createAndAddConcatenatedRecording(List<String> names, String fullItem) {
        String tmpFileName = fullItem.replaceAll(" ", "");
        List<String> fileNames;
        for (String recording : names) {
            fileNames.add(searchableList.getRecording(recording).getFileName());
        }
        //remove silence either side for all recordings and create temp files for all
        //Temp file names are of the form name1name2name31, where 1 is the index of the temp file
        //Checked and works
        Task<Void> removeSilenceTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int index = 1;
                for (String file : fileNames) {
                    String tmpFileName = tmpFileName + index + ".wav";
                    String cmd = "ffmpeg -i " + file + " -af silenceremove=1:0:-50dB " + tmpFileName;
                    ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
                    Process process = builder.start();
                    process.waitFor();
                    index++;
                }
                return null;
            }
        }
        Thread thread = new Thread(removeSilenceTask);
        thread.setDaemon(true);
        thread.start();
        thread.join();
        //regulate volume for all, have checked it changes volume but not that it equalises volume of multiple recordings
        Task<Void> regulateVolumeTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int index = 1;
                for (String file : fileNames) {
                    String tmpFileName = tmpFileName + index + ".wav";
                    String cmd = "ffmpeg -i "+tmpFileName+" -filter:a \"volume=0.5\" " + tmpFileName;
                    ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
                    Process process = builder.start();
                    process.waitFor();
                    index++;
                }
                return null;
            }
        }
        Thread thread = new Thread(regulateVolumeTask);
        thread.setDaemon(true);
        thread.start();
        thread.join();
        //concatenates the temp recordings then deletes them
        String catInput = "";
        number = 0;
        for (String file : fileNames) {
            catInput = catInput + "-i " + file;
            number += 1;
        }

        Task<Void> concatenateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                //
                //THIS LINE IS HARDCODED FOR 2 INPUTS TO THE CONCATENATION MAKE SURE THIS IS CHANGED
                //
                String inputStreams = "[0:0][0:1]"
                //
                //
                //
                String cmd = "ffmpeg " + catInput +
                        "-filter_complex '"+inputStreams+"concat=n="+number+":v=0:a=1[out]' " +
                        "-map '[out]' " + tmpFileName + ".wav";
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
                Process process = builder.start();
                process.waitFor();
                return null;
            }
        }
        Thread thread = new Thread(concatenateTask);
        thread.setDaemon(true);
        thread.start();
        thread.join();
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
                    controller.setWorkspaceRecordingsAndController(workspaceList, randomisedList, controller);
                } else {
                    controller.setWorkspaceRecordingsAndController(workspaceList, workspaceList.getRecordingNames(), controller);
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

    
    public void setWorkspaceRecordings(ObservableList<String> keptRecordings) {
        for (String recordingName : keptRecordings) {
            workspaceList.add(recordingName);
            databaseList.remove(recordingName);
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
        workspaceList = searchableList;
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
