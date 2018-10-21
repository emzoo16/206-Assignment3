package namesayer.fxmlControllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

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
        try {
            Stage stage = (Stage) practiceButton.getScene().getWindow();
            Parent createScene = FXMLLoader.load(getClass().getResource("fxmlFiles/PlaylistScreen.fxml"));
            stage.setScene(new Scene(createScene));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Invoked when the user presses the test microphone button and takes the user to the test 
     * microphone screen
     */
    @FXML
    private void testMicrophone() {
    	try {
            Stage stage = (Stage) testButton.getScene().getWindow();
            Parent createScene = FXMLLoader.load(getClass().getResource("fxmlFiles/TestMicrophone.fxml"));
            stage.setScene(new Scene(createScene));
        } catch (IOException e) {
            e.printStackTrace();
        }
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