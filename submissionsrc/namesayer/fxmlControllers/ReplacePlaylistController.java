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
    private Button replaceButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ListView<String> playlistView;

    //Listview that displays the names of all the playlists the user currently has.
    private List<String> playlistNames = new ArrayList<>();


    /**
     * Upon initialization, list all the playlists the user currently has in a listview and select the first
     * entry in this list. This is to avoid a null pointer exception if the user forgets to select an entry.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listPlaylists();
        playlistView.getSelectionModel().select(0);
    }

    /**
     * This method is invoked when the user confirms that they want to replace an old playlist in order to save
     * a new one.
     */
    @FXML
    public void replaceButtonClicked(ActionEvent event) {

        //Show a confirmation to make sure the user actuallt wants to delete the selected playlist.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Playlist");
        alert.setHeaderText("Continuing will delete this playlist");
        alert.setContentText("Are you sure you want to continue?");
        Optional<ButtonType> action = alert.showAndWait();

        //If the user chooses to delete the playlist, delete the currently selected element in the listview and close the
        //stage.
        if (action.get() == ButtonType.OK) {
            String nameToDelete = playlistView.getSelectionModel().getSelectedItem();
            deletePlaylist(nameToDelete + ".txt");
            UIManager.changeScenes("fxmlFiles/SaveScreen.fxml", (Stage) replaceButton.getScene().getWindow());
        }
    }

    /**
     * If the user chooses to cancel, close the screen to bring them back to the workspace.
     */
    @FXML
    public void cancelButton(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }


    /**
     * Find the given playlist name and delete it.
     */
    public void deletePlaylist(String name) {
        File playlistFiles = new File("Resources/Playlists/");
        for (File fileEntry : playlistFiles.listFiles()) {
            String currentFile = fileEntry.getName();
            if (currentFile.equals(name)) {
                fileEntry.delete();
            }
        }
    }

    /**
     * Add all the playlists the user currently has in an observable array. The playlists are stored in text files.
     *  Then, use this array to set the listview upon initialization.
     */
    private void listPlaylists() {

        //Iterate through the Playlists folder which contains all the playlists the user has created.
        File playlistFiles = new File("Resources/Playlists/");
        for (final File fileEntry : playlistFiles.listFiles()) {
            String currentFile = fileEntry.getName();

            //Add all the text files in the folder to a list.
            if (currentFile.endsWith(".txt")) {
                String formattedName = currentFile.replaceAll(".txt","");
                playlistNames.add(formattedName);
            }

            //Use this list to set an observable list which is used to set the listview.
            ObservableList<String> playList = FXCollections.observableArrayList(playlistNames);
            playlistView.setItems(playList);
        }
    }
}