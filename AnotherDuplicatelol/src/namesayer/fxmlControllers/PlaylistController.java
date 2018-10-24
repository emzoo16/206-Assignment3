package namesayer.fxmlControllers;

import java.io.File;

import java.net.URL;
import java.util.*;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import namesayer.helperClasses.PlaylistLoader;
import namesayer.helperClasses.DatabaseList;
import namesayer.helperClasses.UIManager;
import namesayer.helperClasses.WorkspaceModel;
import namesayer.interfaces.ConcatenatedRecordingLoader;
import namesayer.recordingTypes.ConcatenatedRecording;
import namesayer.recordingTypes.DemoRecording;

public class PlaylistController implements Initializable, ConcatenatedRecordingLoader {
	//All the buttons representing playlists
	@FXML
	private Button playlist1;
	@FXML
	private Button playlist2;
	@FXML
	private Button playlist3;
	@FXML
	private Button playlist4;
	@FXML
	private Button playlist5;
	@FXML
	private Button playlist6;
	//All the buttons used to delete playlists
	@FXML
	private Button delete1;
	@FXML
	private Button delete2;
	@FXML
	private Button delete3;
	@FXML
	private Button delete4;
	@FXML
	private Button delete5;
	@FXML
	private Button delete6;

	@FXML
	private Button uploadButton;
	@FXML
	private Button backButton;
	@FXML
	private Button practiceButton;
	@FXML
	private CheckBox randomiseBox;
	//Displayed if there are no playlists available
	@FXML
	private Label noPlaylistText;
	//Indicates a playlist is loading in
	@FXML
	private ProgressIndicator loadingIndicator;

	//Place the buttons in a list for easier handling.
	private List<Button> playlistButtons = new ArrayList<>();

	//Place the delete buttons in a list for easier handling.
	private List<Button> deleteButtons = new ArrayList<>();

	//List of all the current playlist names.
	private List<String> playlistNames = new ArrayList<>();


	//Number of playlists.
	private int playlistNum = 0;

	//Reference to the current clicked playlistFile
	private File currentPlaylistFile;

	//List of recording objects to be passed to the workspaceController.
	private DatabaseList workspaceRecordings;

	//Concatenated recordings are created using multithreading which can cause issues if the user quickly moves to the
	//next scene. In order to alleviate this we keep this number of names to load and once the number of names in the
	//list matches this number the continue/upload button is re-enabled.
	private int namesToLoad;
	//True if the playlist contains a concatenated recording, indicates it should wait for the recording to finish
	//processing
	private boolean hasConcatenated;
	//model to pass information
	private WorkspaceModel model;



	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = WorkspaceModel.getInstance();
		model.setLoadingController(this);
		//empties the workspacerecordings in the model
		model.setCurrentWorkspaceRecordings(new DatabaseList());

		loadingIndicator.setVisible(false);

		//Set tooptips for buttons.
		uploadButton.setTooltip(new Tooltip("Upload a text file of names\nfrom your device"));
		practiceButton.setTooltip(new Tooltip("Create a new playlist"));
		randomiseBox.setTooltip(new Tooltip("Randomise the plyaing order of \nnames in the selected playlist"));


		namesToLoad = 0;
		workspaceRecordings = new DatabaseList();
		hasConcatenated = false;


		//Add the playlist buttons to a list for ease of handling.
		playlistButtons.add(playlist1);
		playlistButtons.add(playlist2);
		playlistButtons.add(playlist3);
		playlistButtons.add(playlist4);
		playlistButtons.add(playlist5);
		playlistButtons.add(playlist6);

		//Add the delete buttons to a list for ease of handling.
		deleteButtons.add(delete1);
		deleteButtons.add(delete2);
		deleteButtons.add(delete3);
		deleteButtons.add(delete4);
		deleteButtons.add(delete5);
		deleteButtons.add(delete6);

		//Handles which buttons are visible/invisible depending on how many playlists there
		//currently are.
		setplaylistButtons();

		//Displays the label is no playlists are available
		if(playlistNum == 0) {
			noPlaylistText.setVisible(true);
		}else {
			noPlaylistText.setVisible(false);
		}
	}

	/*
	 * This method iterates through the file containing the list of playlists and
	 * count how many playlists there are and sets the buttons accordingly
	 */
	private void setplaylistButtons(){
		//Get all files in the playlists folder
		File folder = new File("Resources/Playlists/");
		File[] listOfFiles = folder.listFiles();

		//Start off with a clean slate by making all buttons invisible.
		for (int i=0; i<6; i++){
			playlistButtons.get(i).setVisible(false);
			deleteButtons.get(i).setVisible(false);
		}

		if (listOfFiles.length == 0) {
			//label saying there are no current playlists visible
		}else {
			for(File file : listOfFiles) {

				//Get all the playlist files by finding all the text files. Set the buttons
				//accordingly.
				if (file.isFile() && file.getName().endsWith(".txt")) {
					String formattedName = file.getName().replaceAll(".txt","");
					playlistNames.add(formattedName);
					playlistButtons.get(playlistNum).setVisible(true);
					playlistButtons.get(playlistNum).setText(formattedName);
					deleteButtons.get(playlistNum).setVisible(true);
					playlistNum ++;
				}

			}
		}

	}

	/**
	 * This method is called when any of the delete buttons are clicked and deletes the corresponding playlist
	 * @param event
	 */
	@FXML
	private void deleteButtonClicked(ActionEvent event) {

		//Get a reference to the current selected file and find the playlist text file that corresponds to it.
		Button clickedDeleteButton = (Button) event.getSource();
		int index = deleteButtons.indexOf(clickedDeleteButton);
		//Gets the names of the playlist to delete.
		Button currentPlaylist = playlistButtons.get(index);
		String playlist = currentPlaylist.getText() + ".txt";
		//Confirms with the user they want to delete the playlist
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Confirmation");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to delete this playlist?");
		Optional<ButtonType> action = alert.showAndWait();

		if (action.get() == ButtonType.OK) {
			File playlistFiles = new File("Resources/Playlists/");
			//Iterates through the playlist folder and deletes the corresponding playlist
			for (final File fileEntry : playlistFiles.listFiles()) {
				String currentFile = fileEntry.getName();
				if (currentFile.equals(playlist)) {
					fileEntry.delete();
					//makes the button invisible again
					currentPlaylist.setVisible(false);
					clickedDeleteButton.setVisible(false);
					playlistNum--;
					if(playlistNum == 0) {
						noPlaylistText.setVisible(true);
					}
				}
			}
		}

	}
	/*
	 * This method is invoked whenever a user clicks any playlist button. Loads the recordings of the playlist
	 * that was clicked in the workspace.
	 */
	@FXML
	private void playlistClicked(ActionEvent event) {
		disableAllButtons();
		loadingIndicator.setVisible(true);
		//Get a reference to the current selected file and find the playlist text file that corresponds to it.
		Button clickedPlaylist = (Button) event.getSource();
		String playlist = clickedPlaylist.getText() + ".txt";
		currentPlaylistFile = new File("Resources/Playlists/"+playlist);

		//Create a playlist loader to scan and upload the names in the playlist.
		PlaylistLoader loader = new PlaylistLoader();
		List<String> names = loader.loadPlaylist(currentPlaylistFile);
		addNames(names);
		model.addToCurrentWorkspaceRecordings(workspaceRecordings);
		//If there are no concatenated names loading
		if (!hasConcatenated) {
			moveToWorkspace();
		}
	}

	/**
	 * Changes scenes to the workspace, first checks if the randomise box is checked and sets the recording display
	 * accordingly
	 */
	private void moveToWorkspace() {
		WorkspaceModel.getInstance().setCurrentWorkspaceRecordings(workspaceRecordings);
		if (randomiseBox.isSelected()) {
			ObservableList<String> randomisedList = workspaceRecordings.getRecordingNames();
			Collections.shuffle(randomisedList);
			WorkspaceModel.getInstance().setCurrentRecordingsDisplay(randomisedList);
		} else {
			WorkspaceModel.getInstance().setCurrentRecordingsDisplay(workspaceRecordings.getRecordingNames());
		}
		UIManager.changeScenes("fxmlFiles/Workspace.fxml");
	}

	/*
	 * This button takes the user to the upload file scene where they can upload a text file of names
	 * to practice.
	 */
	@FXML
	private void uploadButtonClicked() {
		UIManager.changeScenes("fxmlFiles/UploadScreen.fxml");
	}

	/*
	 * This button takes the user to the workspace creator where they can select which names they want to practice.
	 */
	@FXML
	private void practiceButtonClicked() {
		UIManager.changeScenes("fxmlFiles/PlaylistCreator.fxml");
	}

	/*
	 * This button takes the user back to the menu scene.
	 */
	@FXML
	private void backButtonClicked() {
		UIManager.changeScenes("fxmlFiles/StartMenu.fxml");
	}

	/*
	 * This method will add all the names in the playlist to the workspacemodel.
	 */
	private void addNames(List<String> namesList) {

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
				for (String recordingName : names) {
					updatedNames.add(recordingName.substring(0,1).toUpperCase() + recordingName.substring(1).toLowerCase());
				}
				List<String> fileNames = new ArrayList<>();
				//Gets the file names for all these names
				for (String recording : updatedNames) {
					fileNames.add(referenceList.getRecording(recording).getFileName());
				}
				//Creates the concatenated recording
				DemoRecording concatenatedRecording = new ConcatenatedRecording(fileNames, name, this);
				namesToLoad++;
				hasConcatenated = true;
			} else {
				name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
				//If the name is a single name, it simply gets added
				workspaceRecordings.add(name);
				namesToLoad++;
			}
		}
	}

	/**
	 * When a concatenated recording has finished processing, it checks if the model contains all the recordings it needs
	 * to load and if it does changes to the workspace scene
	 */
	@Override
	public void concatenationComplete() {
		workspaceRecordings = model.getCurrentWorkspaceRecordings();
		if (workspaceRecordings.getRecordingNames().size() == namesToLoad) {
			moveToWorkspace();
		}
	}

	/**
	 * disables all buttons for once a playlist has been selected, and is being loaded in.
	 */
	private void disableAllButtons() {
		for (Button button : playlistButtons) {
			button.setDisable(true);
		}
		for (Button button : deleteButtons) {
			button.setDisable(true);
		}
		uploadButton.setDisable(true);
		practiceButton.setDisable(true);
		backButton.setDisable(true);
	}
}