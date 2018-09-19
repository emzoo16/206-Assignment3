package namesayer;

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
     * Invoked when the user presses the practice button
     */
    @FXML
    private void beginPractice() {
        try {
            Stage stage = (Stage) practiceButton.getScene().getWindow();
            Parent createScene = FXMLLoader.load(getClass().getResource("WorkSpaceCreatorController.fxml"));
            stage.setScene(new Scene(createScene, 700, 500));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Invoked when the user presses the test microphone button
     */
    @FXML
    private void testMicrophone() {

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
