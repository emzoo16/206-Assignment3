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

	/**
	 * FXML variables.
	 */
    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField nameText;

    
    //The name the user inputs into the textfield for the new playlist.
    String playlistName;

    //The list of recordings that are currently in the workspace list.
    DatabaseList listOfRecordings;
    
    //A reference to the save button in the workspace.
    Button workspaceSaveButton;
    
    /**
     * 
     */
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

    /**
     * This method is invoked when the user clicks the save button in the save screen.
     */
    @FXML
    private void saveButtonClicked(ActionEvent event) {
    	
    	//If no name has been entered, show a warning prompting the user to enter a name.
        if (nameText.getText().equals("")) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a name before continuing." );
            alert.showAndWait();
        }
        //If the user has entered a name, try to create a playlist with that name. If the
        //playlist was successfully created, close the stage.
        else{
            if(createPlaylist(nameText.getText())) {;
                Stage currentStage = (Stage) saveButton.getScene().getWindow();
                currentStage.close();
            }
        }
    }
    
    
	/**
	 * If the user chooses to cancel saving the playlist, close the save screen stage and return them to the workspace.
	 */
    @FXML
    private void cancelButtonClicked(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
    
    /**
     * This method is responsible for creating a new playlist with the provided name. It returns a boolean value. True if
   	 * a new playlist was created succesfully, false if it wasn't.
     */
    private boolean createPlaylist(String name) {
    	
        File file = new File("./Resources/Playlists/" + name + ".txt");
        
        //If a playlist does not already exist with the given name, create a new text file. Add all the current names in the 
        //Listview in the workspace to this file.
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
        }
        //If a playlist already exists with the name, show a warning telling the user to choose another name.
        else{
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("This name already exists. Please choose another" );
            alert.showAndWait();
            return false;
        }
    }

}