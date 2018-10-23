package namesayer.fxmlControllers;

import java.io.File;
import java.net.URL;
import java.util.*;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import namesayer.helperClasses.WorkspaceModel;
import namesayer.helperClasses.DatabaseList;
import namesayer.helperClasses.PlaylistLoader;
import namesayer.helperClasses.UIManager;
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
	@FXML
	ProgressIndicator loadingIndicator;
	
	
	 //Keeping track of the number in the listViews the user has currently selected
	 //in the playListView and nameListView respectively. 
	int playlistIndex;
	int nameIndex;
	
	//Stores a reference to the file the user chooses from the file chooser.
	File currentFile;

	//List of recording objects to be passed to the workspaceController.
	DatabaseList workspaceRecordings;

	WorkspaceModel model;
	//Concatenated recordings are created using multithreading which can cause issues if the user quickly moves to the
	//next scene. In order to alleviate this we keep this number of names to load and once the number of names in the
	//list matches this number the continue/upload button is re-enabled.
	private int namesToLoad;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = WorkspaceModel.getInstance();
		model.setLoadingController(this);
		namesToLoad = 0;
		loadingIndicator.setVisible(false);
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
		UIManager.changeScenes("fxmlFiles/PlaylistScreen.fxml");
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
			alert.setContentText("Please load a name before continuing.");
			alert.showAndWait();
		}else {
			WorkspaceModel model = WorkspaceModel.getInstance();
			model.setCurrentWorkspaceRecordings(workspaceRecordings);
			if (randomiseBox.isSelected()) {
				ObservableList<String> randomisedList = workspaceRecordings.getRecordingNames();
				Collections.shuffle(randomisedList);
				model.setCurrentRecordingsDisplay(randomisedList);
			} else {
				model.setCurrentRecordingsDisplay(workspaceRecordings.getRecordingNames());
			}
			UIManager.changeScenes("fxmlFiles/Workspace.fxml");
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
			workspaceRecordings = new DatabaseList();
			nameListView.getItems().clear();
			uploadButton.setDisable(true);
			continueButton.setDisable(true);
			loadingIndicator.setVisible(true);
			scanFile();
		}
	}
	
	/*
	 * This method scans the file the user chooses. It parses each line into the individual 
	 * names and searches if they are present in the database. If they are, the names are 
	 * added to the list to be played.
	 */
	public void scanFile() {
		PlaylistLoader loader = new PlaylistLoader();
		List<String> names = loader.loadPlaylist(currentFile);
		addNames(names);
		model.addToCurrentWorkspaceRecordings(workspaceRecordings);
		System.out.println(namesToLoad);
		System.out.println(workspaceRecordings.getRecordingNames().size());
		if (workspaceRecordings.getRecordingNames().size() == namesToLoad) {
			namesToLoad = 0;
			nameListView.setItems(workspaceRecordings.getRecordingNames());
			continueButton.setDisable(false);
			uploadButton.setDisable(false);
			loadingIndicator.setVisible(false);
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
				//Creates the concatenated recording
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

	public void showUploadWarning() {
		UIManager.openStage("fxmlFiles/UploadWarning.fxml");
	}

	public void concatenationComplete() {
		System.out.println(model.getCurrentWorkspaceRecordings().getRecordingNames().size());
		if (model.getCurrentWorkspaceRecordings().getRecordingNames().size() == namesToLoad) {
			workspaceRecordings = model.getCurrentWorkspaceRecordings();
			namesToLoad = 0;
			nameListView.setItems(workspaceRecordings.getRecordingNames());
			continueButton.setDisable(false);
			uploadButton.setDisable(false);
			loadingIndicator.setVisible(false);
		}
	}
}
