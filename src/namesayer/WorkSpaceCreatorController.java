package namesayer;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;

public class WorkSpaceCreatorController implements Initializable {
    @FXML
    TextField textField;
    @FXML
    ListView<String> databaseRecordings;
    @FXML
    ListView<String> workspaceRecordings;
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
    CheckBox selectAllBox;

    ObservableList<String> databaseList;
    ObservableList<String> selectedDatabaseItems;

    @FXML
    private void addToWorkspace() {

    }

    @FXML
    private void removeFromWorkspace() {

    }

    @FXML
    private void continueToPractice() {

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
        databaseList = FXCollections.observableArrayList();
        //Gets the files in an array
        File folder = new File("Database/");
        File[] ArrayOfFiles = folder.listFiles();
        int count = 1;
        if (ArrayOfFiles.length != 0) {
            for (File file : ArrayOfFiles) {
                //Validates the file
                if (file.isFile() && !file.toString().startsWith("Database/.")) {
                    String fileString = file.getName();
                    //Removes extension and codes.
                    fileString = fileString.substring(fileString.lastIndexOf("_") + 1, fileString.indexOf("."));
                    String updatedFileString = fileString;
                    while (databaseList.contains(updatedFileString)) {
                        updatedFileString = fileString + "(" + count + ")";
                        count += 1;
                    }
                    count = 1;
                    databaseList.add(updatedFileString);
                }
            }
        }
        //Allow the selection of multiple items
        databaseRecordings.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        databaseRecordings.setOnMouseClicked(event ->
                selectedDatabaseItems = databaseRecordings.getSelectionModel().getSelectedItems());
        databaseRecordings.setItems(databaseList);
    }
}
