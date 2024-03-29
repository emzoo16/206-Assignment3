package namesayer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
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

    DatabaseList databaseList;
    DatabaseList workspaceList;
    ObservableSet<String> selectedDatabaseItems;
    ObservableSet<String> selectedWorkspaceItems;

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
            workspaceList.remove(recordingName);
            databaseList.add(recordingName);
        }
        selectedWorkspaceItems.clear();
        workspaceRecordingsView.setItems(workspaceList.getRecordingNames());
        databaseRecordingsView.setItems(databaseList.getRecordingNames());
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Sets up the two list views to work with checkboxes
        selectedDatabaseItems = FXCollections.observableSet();
        databaseList = new DatabaseList();
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
    }
}
