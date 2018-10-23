package namesayer.fxmlControllers;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import namesayer.helperClasses.DatabaseList;
import namesayer.helperClasses.WorkspaceModel;

public class SaveScreenController implements Initializable {

    DatabaseList listOfRecordings;
    int playlistNum;
    String name;
    Button workspaceSaveButton;

    @FXML
    Button saveButton;

    @FXML
    Button cancelButton;

    @FXML
    TextField nameText;

    String playlistName;

    @FXML
    public void saveButtonClicked(ActionEvent event) {
        if (nameText.getText().equals("")) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a name before continuing." );
            alert.showAndWait();
        }else{
            if(createPlaylist(nameText.getText())) {;
                Stage currentStage = (Stage) saveButton.getScene().getWindow();
                currentStage.close();
            }
        }
    }

    @FXML
    public void cancelButtonClicked(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    public boolean createPlaylist(String name) {
        File file = new File("./Resources/Playlists/" + name + ".txt");
        if(!file.exists()) {
            try {
                PrintWriter writer = new PrintWriter(file);
                for (String currentName : listOfRecordings.getRecordingNames()) {
                    writer.println(currentName);
                }
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }else{
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("This name already exists. Please choose another" );
            alert.showAndWait();
            return false;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listOfRecordings = WorkspaceModel.getInstance().getCurrentWorkspaceRecordings();
        nameText.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    saveButtonClicked(new ActionEvent());
                }
            }
        });
    }
}