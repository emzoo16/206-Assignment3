package namesayer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub

    }

    @FXML
    public void saveButtonClicked(ActionEvent event) {
        if (nameText.getText().equals("")) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a name before continuing." );
            alert.showAndWait();
        }else{
            createPlaylist(nameText.getText());
            System.out.println(saveButton.getText());
            workspaceSaveButton.setDisable(true);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        }
    }

    @FXML
    public void cancelButtonClicked(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    public void setRecordingList(DatabaseList list) {
        this.listOfRecordings = list;
    }
    public void setButton(Button button) {
        this.workspaceSaveButton = button;
    }

    public void createPlaylist(String name) {
        File file = new File("./Playlists/" + name + ".txt");
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
        }else{
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("This name already exists" );
            alert.showAndWait();
        }
    }

}