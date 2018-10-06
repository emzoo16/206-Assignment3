package namesayer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PlaylistController implements Initializable {
	
	/*
	 * FXML variables 
	 */
	@FXML
	Button playButton;
	@FXML
	Button nextButton;
	@FXML
	Button previousButton;
	@FXML
	Button uploadButton;
	@FXML
	Button backButton;
	@FXML
	ProgressBar progressbar;	
	@FXML
	ListView<String> playListView;
	ObservableList<String> playList = FXCollections.observableArrayList();
	@FXML 
	ListView<String> nameListView;
	ObservableList<String> nameList = FXCollections.observableArrayList();
	
	/*
	 * Keeping track of the number in the listViews the user has currently selected
	 * in the playListView and nameListView respectively.
	 */
	int playlistIndex;
	int nameIndex;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Listener to get the current name the user is clicking on in the playListView.
				playListView.getSelectionModel().selectedItemProperty().addListener(
						new ChangeListener<String>() {
							@Override
							public void changed(ObservableValue<? extends String> observable, String oldValue,
												String newValue) {
								
								//Resets the progressBar.
								progressbar.setProgress(0.0);
								playlistIndex = playListView.getSelectionModel().getSelectedIndex();
								//refreshPersonalRecordings(newValue);
								nameIndex = 0;
							}
						});
				//Listener to get the current selected element in the personal recordings list.
				nameListView.getSelectionModel().selectedItemProperty().addListener(
						new ChangeListener<String>() {
							@Override
							public void changed(ObservableValue<? extends String> observable, String oldValue,
												String newValue) {
								nameIndex = nameListView.getSelectionModel().getSelectedIndex();
							}
						});
		
	}
	
	@FXML
	public void playButtonClicked() {
		
	}
	@FXML
	public void previousButtonClicked() {
		
	}
	@FXML
	public void backButtonClicked() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("startMenu.fxml"));
			Parent playlistScene = fxmlLoader.load();
			Stage stage = (Stage) backButton.getScene().getWindow();
			stage.setScene(new Scene(playlistScene, 700, 500));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@FXML
	public void nextButtonClicked() {
		
	}

	@FXML
	public void uploadButtonClicked() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fileUploadScreen.fxml"));
			Parent playlistParent = fxmlLoader.load();
			Scene playlistScene = new Scene(playlistParent);
			Stage playlistStage = new Stage();
			playlistStage.setScene(playlistScene);
			playlistStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	

}
