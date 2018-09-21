package namesayer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class testMicrophoneController implements Initializable{
	
	@FXML
	Button backButton;
	
	@FXML
	Button testButton;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	@FXML
	public void backButtonClicked() {
		try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            Parent createScene = FXMLLoader.load(getClass().getResource("startMenu.fxml"));
            stage.setScene(new Scene(createScene, 700, 500));
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	@FXML
	public void testButtonClicked() {
		
	}

}
