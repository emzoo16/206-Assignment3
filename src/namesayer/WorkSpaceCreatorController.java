package namesayer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import java.util.ResourceBundle;

public class WorkSpaceCreatorController implements Initializable {
    @FXML
    TextField textField;
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
    WorkspaceList workspaceList;
    ObservableSet<String> selectedDatabaseItems;
    ObservableSet<String> selectedWorkspaceItems;

    @FXML
    private void addToWorkspace() {
        for (String recordingName : selectedDatabaseItems) {
            Recording recording = databaseList.getRecording(recordingName);
            workspaceList.add(recordingName, recording);
            databaseList.remove(recordingName);
        }
        selectedDatabaseItems.clear();
        databaseRecordingsView.setItems(databaseList.getRecordingNames());
        workspaceRecordingsView.setItems(workspaceList.getRecordingNames());
    }

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

    @FXML
    private void continueToPractice() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("workspace.fxml"));
            Parent createScene = fxmlLoader.load();
            WorkspaceController controller = fxmlLoader.getController();
            //controller.setWorkspaceRecordings(workspaceList);
            Stage stage = (Stage) continueButton.getScene().getWindow();
            stage.setScene(new Scene(createScene, 700, 500));
        } catch (IOException e) {
            e.printStackTrace();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Sets up the two list views to work with checkboxes
        selectedDatabaseItems = FXCollections.observableSet();
        databaseList = new DatabaseList();
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
        workspaceList = new WorkspaceList();
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
