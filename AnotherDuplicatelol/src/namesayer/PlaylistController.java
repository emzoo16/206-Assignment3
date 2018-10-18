package namesayer;

import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PlaylistController implements Initializable{
	
	@FXML
	Button playlist1;
	@FXML
	Button playlist2;
	@FXML
	Button playlist3;
	@FXML
	Button playlist4;
	@FXML
	Button playlist5;
	@FXML
	Button playlist6;

	@FXML
	Button uploadButton;
	@FXML
	Button backButton;
	@FXML
	Button practiceButton;
	
	//Place the buttons in a list for easier handling.
	List<Button> playlistButton = new ArrayList<>();

	
	//List of all the current playlist names. Hard coded for now to test functionality.
	List<String> playlistNames = Arrays.asList("Class1", "Class2");
	
	
	//Hardcoded at the moment, will contain the number of playlists that has been created.
	int playlistNum=2;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//Add the playlist buttons to a list for ease of handling.
		playlistButton.add(playlist1);
		playlistButton.add(playlist2);
		playlistButton.add(playlist3);
		playlistButton.add(playlist4);
		playlistButton.add(playlist5);
		playlistButton.add(playlist6);
		
		//Handles which buttons are visible/invisible depending on how many playlists there
		//currently are.
		setButtons();
	}
	
	@FXML
	public void uploadButtonClicked() {
		try {
            Stage stage = (Stage) practiceButton.getScene().getWindow();
            Parent createScene = FXMLLoader.load(getClass().getResource("uploadScreen.fxml"));
            stage.setScene(new Scene(createScene));

        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
	@FXML
	public void practiceButtonClicked() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("workSpaceCreator.fxml"));
			Parent playlistScene = fxmlLoader.load();
			Stage stage = (Stage) practiceButton.getScene().getWindow();
			stage.setScene(new Scene(playlistScene));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
	public void playlistClicked() {
		//Search through for the list of recordings in the playlist. Use a playlist button?
	}
	
	/*
	 * Set all playlist related buttons invisible.
	 */
	public void setButtons() {
		//Start off with a clean slate by making all buttons invisible.
		for (int i=0; i<6; i++){
			playlistButton.get(i).setVisible(false);
		}
		//Make the buttons visible and set the text to the corresponding name.
		for (int i=0; i<playlistNum; i++){
			playlistButton.get(i).setVisible(true);
			playlistButton.get(i).setText(playlistNames.get(i));
		}
	}
}
