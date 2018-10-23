package namesayer.fxmlControllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import namesayer.helperClasses.DatabaseList;
import namesayer.helperClasses.UIManager;
import namesayer.helperClasses.WorkspaceModel;

import java.io.IOException;

public class StartMenuController {
    @FXML
    private Button practiceButton;
    @FXML
    private Button testButton;
    @FXML
    private Button quitButton;

    /**
     * Invoked when the user presses the practice button and takes the user to the workspace
     * creator scene.
     */
    @FXML
    private void beginPractice() {
        UIManager.changeScenes("fxmlFiles/PlaylistScreen.fxml");
    }
    
    /**
     * Invoked when the user presses the test microphone button and takes the user to the test 
     * microphone screen
     */
    @FXML
    private void testMicrophone() {
        UIManager.changeScenes("fxmlFiles/TestMicrophone.fxml");
    }

    /**
     * Invoked when the user presses the quit button
     */
    @FXML
    private void quit() {
        Platform.exit();
        System.exit(0);
    }
}
