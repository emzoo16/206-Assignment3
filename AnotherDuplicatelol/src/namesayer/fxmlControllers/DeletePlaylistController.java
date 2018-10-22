package namesayer.fxmlControllers;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import namesayer.helperClasses.DatabaseList;

public class DeletePlaylistController implements Initializable{

    @FXML
    Button replaceButton;

    @FXML
    Button cancelButton;

    @FXML
    ListView<String> playlistView;

    DatabaseList listOfRecordings;
    String inputName;
    Button workspaceButton;
    List<String> playlistNames = new ArrayList<>();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listPlaylists();
        playlistView.getSelectionModel().select(0);
    }

    @FXML
    public void replaceButtonClicked(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Playlist");
        alert.setHeaderText("Continuing will delete this playlist");
        alert.setContentText("Are you sure you want to continue?");
        Optional<ButtonType> action = alert.showAndWait();

        if (action.get() == ButtonType.OK) {
            String nameToDelete = playlistView.getSelectionModel().getSelectedItem();
            deletePlaylist(nameToDelete + ".txt");
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmlFiles/SaveScreen.fxml"));
                Parent rateSceneParent = fxmlLoader.load();
                SaveScreenController controller = fxmlLoader.getController();
                controller.setRecordingList(listOfRecordings);
                controller.setButton(workspaceButton);
                Scene rateScene = new Scene(rateSceneParent);
                Stage rateStage = (Stage) replaceButton.getScene().getWindow();
                ;
                rateStage.setScene(rateScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void cancelButton(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    public void setRecordingList(DatabaseList list) {
        this.listOfRecordings = list;

    }

    public void setInputName(String name) {
        this.inputName = name;
    }
    public void setButton(Button button) {
        this.workspaceButton = button;
    }

    public void createPlaylist(String name) {
        File file = new File("./Resources/Playlists/" + name + ".txt");
        try {
            PrintWriter writer = new PrintWriter(file);
            for (String currentName : listOfRecordings.getRecordingNames()) {
                writer.println(currentName);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void deletePlaylist(String name) {
        File playlistFiles = new File("Resources/Playlists/");
        for (File fileEntry : playlistFiles.listFiles()) {
            String currentFile = fileEntry.getName();
            if (currentFile.equals(name)) {
                fileEntry.delete();
            }
        }
    }

    public void listPlaylists() {
        File playlistFiles = new File("Resources/Playlists/");
        for (final File fileEntry : playlistFiles.listFiles()) {
            String currentFile = fileEntry.getName();
            if (currentFile.endsWith(".txt")) {
                String formattedName = currentFile.replaceAll(".txt","");
                playlistNames.add(formattedName);
            }
            ObservableList<String> playList = FXCollections.observableArrayList(playlistNames);
            playlistView.setItems(playList);
        }
    }
}