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
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import namesayer.helperClasses.DatabaseList;
import namesayer.helperClasses.UIManager;

public class ReplacePlaylistController implements Initializable{

    @FXML
    Button replaceButton;

    @FXML
    Button cancelButton;

    @FXML
    ListView<String> playlistView;

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
            UIManager.changeScenes("fxmlFiles/SaveScreen.fxml", (Stage) replaceButton.getScene().getWindow());
        }
    }

    @FXML
    public void cancelButton(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
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