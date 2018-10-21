package namesayer.fxmlControllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import namesayer.helperClasses.DatabaseList;
import namesayer.helperClasses.PlaylistLoader;
import namesayer.interfaces.ConcatenatedRecordingLoader;
import namesayer.recordingTypes.ConcatenatedRecording;
import namesayer.recordingTypes.DemoRecording;
import namesayer.recordingTypes.Recording;

public class UploadController implements Initializable, ConcatenatedRecordingLoader {
	
	/*
	 * FXML variables 
	 */
	@FXML
	Button uploadButton;
	@FXML
	Button browseButton;
	@FXML
	TextField fileText;
	@FXML
	Button backButton;
	@FXML
	CheckBox randomiseBox;
	@FXML
	Button continueButton;
	@FXML 
	ListView<String> nameListView;
	
	
	 //Keeping track of the number in the listViews the user has currently selected
	 //in the playListView and nameListView respectively. 
	int playlistIndex;
	int nameIndex;
	
	//Stores a reference to the file the user chooses from the file chooser.
	File currentFile;

	//List of recording objects to be passed to the workspaceController.
	DatabaseList workspaceRecordings;

	//Concatenated recordings are created using multithreading which can cause issues if the user quickly moves to the
	//next scene. In order to alleviate this we keep this number of names to load and once the number of names in the
	//list matches this number the continue/upload button is re-enabled.
	private int namesToLoad;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		namesToLoad = 0;
				
		workspaceRecordings = new DatabaseList();
		
		//Make the textfield uneditable.
		fileText.setEditable(false);
	}

	/*
	 * This method is invoked when the user clicks the back button. Takes the user back to the start
	 * menu.
	 */
	@FXML
	public void backButtonClicked() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmlFiles/PlaylistScreen.fxml"));
			Parent playlistScene = fxmlLoader.load();
			Stage stage = (Stage) backButton.getScene().getWindow();
			stage.setScene(new Scene(playlistScene));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This button is invoked when the user is ready to continue to the workspace, where they will play the 
	 * selected recordings.
	 */
	@FXML
	public void continueButtonClicked() {
		if(nameListView.getItems().isEmpty()) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("Please load a name before continuing." );
			alert.showAndWait();
		}else {
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmlFiles/Workspace.fxml"));
				Parent playlistScene = fxmlLoader.load();
				WorkspaceController controller = fxmlLoader.getController();
				//Passing the selected recordings to the workspace so they can be shown in the listviews in the
				//workspace.
				if (randomiseBox.isSelected()) {
					ObservableList<String> randomisedList = workspaceRecordings.getRecordingNames();
					Collections.shuffle(randomisedList);
					controller.setWorkspaceRecordingsAndController(workspaceRecordings, randomisedList);
				} else {
					controller.setWorkspaceRecordingsAndController(workspaceRecordings, workspaceRecordings.getRecordingNames());
				}
				
				Stage stage = (Stage) continueButton.getScene().getWindow();
				stage.setScene(new Scene(playlistScene));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * This method brings up a file chooser where the user is prompted to choose a .txt file to upload.
	 */
	@FXML
	public void browseButtonClicked() {
		//Creating a new FileChooser with a filter so it only accepts .txt files.
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		
		//Get a reference to the selected file.
		File selectedFile = fileChooser.showOpenDialog(null);
		
		//If the user successfully chooses a file, scan the file.
		if (selectedFile != null) {
			currentFile = selectedFile;
			fileText.setText(currentFile.getName());
		}
	}
	
	/*
	 * This method is invoked when the user chooses to upload the chosen file from the fileChooser.
	 */
	@FXML
	public void uploadButtonClicked() {
		
		//If no file is chosen, show a warning alert.
		if(currentFile == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("Please choose a file" );
			alert.showAndWait();
			
		}else {
			uploadButton.setDisable(true);
			continueButton.setDisable(true);
			scanFile();
		}
	}
	
	/*
	 * This method scans the file the user chooses. It parses each line into the individual 
	 * names and searches if they are present in the database. If they are, the names are 
	 * added to the list to be played.
	 */
	public void scanFile() {
		PlaylistLoader loader = new PlaylistLoader((Stage) uploadButton.getScene().getWindow(), this);
		List<String> names = loader.loadPlaylist(currentFile);
		addNames(names);
		if (workspaceRecordings.getRecordingNames().size() == namesToLoad) {
			namesToLoad = 0;
			nameListView.setItems(workspaceRecordings.getRecordingNames());
			continueButton.setDisable(false);
			uploadButton.setDisable(false);
		}
	}

	/*
	 * This method will add all the names in the list of found names to the workspace.
	 */
	public void addNames(List<String> namesList) {
		
		//A list to get the file names of all recordings to create a concatenated recording
		DatabaseList referenceList = new DatabaseList();
		referenceList.displayAll();

		//Loops through all names in the list adding them to the workspace
		for (String name : namesList) {
			//If the name is a concatenated one
			if (name.trim().contains(" ")) {
				//Gets the list of the names
				List<String> names = Arrays.asList(name.split(" "));
				List<String> updatedNames = new ArrayList<>();
				for(String splitName : names) {
					splitName = splitName.substring(0,1).toUpperCase() + splitName.substring(1).toLowerCase();
					updatedNames.add(splitName);
				}
				List<String> fileNames = new ArrayList<>();
				//Gets the file names for all these names
				for (String recording : updatedNames) {
					Recording obj = referenceList.getRecording(recording);
					fileNames.add(obj.getFileName());
				}
				//Creates the concatenated recording and adds it to the workspace
				DemoRecording concatenatedRecording = new ConcatenatedRecording(fileNames, name, this);
				namesToLoad++;
			} else {
				name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
				//If the name is a single name, it simply gets added
				workspaceRecordings.add(name);
				namesToLoad++;
			}
		}
	}

	public void showUploadWarning(List<String> notFoundDisplay) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmlFiles/UploadWarning.fxml"));
			Parent warningSceneParent = fxmlLoader.load();
			Scene warningScene = new Scene(warningSceneParent);

			//Getting the instance of the warning controller and passing the
			UploadWarningController controller = fxmlLoader.getController();
			controller.setNotFoundList(notFoundDisplay);

			Stage warningStage = new Stage();
			warningStage.setScene(warningScene);

			//Disable the background window when the warning stage is displayed.
			warningStage.initModality(Modality.WINDOW_MODAL);
			warningStage.initOwner(uploadButton.getScene().getWindow());

			warningStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void addPlaylistRecordings(DatabaseList list) {
		List<String> concatenatedRecording = list.getRecordingNames();
		for (String recordingName : concatenatedRecording) {
			workspaceRecordings.add(list.getRecording(recordingName));
		}
		if (workspaceRecordings.getRecordingNames().size() == namesToLoad) {
			namesToLoad = 0;
			nameListView.setItems(workspaceRecordings.getRecordingNames());
			continueButton.setDisable(false);
			uploadButton.setDisable(false);
		}

	}
}