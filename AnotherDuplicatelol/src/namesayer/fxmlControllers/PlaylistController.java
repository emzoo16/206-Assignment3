package namesayer.fxmlControllers;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.util.*;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import namesayer.helperClasses.PlaylistLoader;
import namesayer.helperClasses.DatabaseList;
import namesayer.interfaces.ConcatenatedRecordingLoader;
import namesayer.recordingTypes.ConcatenatedRecording;
import namesayer.recordingTypes.DemoRecording;

public class PlaylistController implements Initializable, ConcatenatedRecordingLoader {

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
	Button delete1;
	@FXML
	Button delete2;
	@FXML
	Button delete3;
	@FXML
	Button delete4;
	@FXML
	Button delete5;
	@FXML
	Button delete6;

	@FXML
	Button uploadButton;
	@FXML
	Button backButton;
	@FXML
	Button practiceButton;

	@FXML
	CheckBox randomiseBox;

	@FXML
	Label noPlaylistText;

	//Place the buttons in a list for easier handling.
	List<Button> playlistButtons = new ArrayList<>();

	//Place the delete buttons in a list for easier handling.
	List<Button> deleteButtons = new ArrayList<>();

	//List of all the current playlist names.
	List<String> playlistNames = new ArrayList<>();


	//Number of playlists.
	int playlistNum = 0;

	//Reference to the current clicked playlistFile
	File currentPlaylistFile;

	//List of recording objects to be passed to the workspaceController.
	DatabaseList workspaceRecordings;

	//Concatenated recordings are created using multithreading which can cause issues if the user quickly moves to the
	//next scene. In order to alleviate this we keep this number of names to load and once the number of names in the
	//list matches this number the continue/upload button is re-enabled.
	private int namesToLoad;

	private boolean hasConcatenated;



	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//Set tooptips for buttons.
		uploadButton.setTooltip(new Tooltip("Upload a text file of names\nfrom your device"));
		practiceButton.setTooltip(new Tooltip("Practice a new playlist"));
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

		if(playlistNum == 0) {
			noPlaylistText.setVisible(true);
		}else {
			noPlaylistText.setVisible(false);
		}
	}

	/*
	 * This method would iterate through the file containing the list of playlists and
	 * count how many playlists there are. Or perhaps returns the names of all the playlists??.
	 */
	public void setplaylistButtons(){

		File folder = new File("Playlists/");
		File[] listOfFiles = folder.listFiles();

		//Start off with a clean slate by making all buttons invisible.
		for (int i=0; i<6; i++){
			playlistButtons.get(i).setVisible(false);
			deleteButtons.get(i).setVisible(false);
		}

		if (listOfFiles.length == 0) {
			//have a label saying there are no current playlists
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

	@FXML
	public void deleteButtonClicked(ActionEvent event) {

		//Get a reference to the current selected file and find the playlist text file that corresponds to it.
		Button clickedDeleteButton = (Button) event.getSource();
		int index = deleteButtons.indexOf(clickedDeleteButton);

		Button currentPlaylist = playlistButtons.get(index);

		String playlist = currentPlaylist.getText() + ".txt";

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Confirmation");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to delete?");
		Optional<ButtonType> action = alert.showAndWait();

		if (action.get() == ButtonType.OK) {
			File playlistFiles = new File("Playlists/");

			for (final File fileEntry : playlistFiles.listFiles()) {
				String currentFile = fileEntry.getName();
				if (currentFile.equals(playlist)) {
					fileEntry.delete();
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
	public void playlistClicked(ActionEvent event) {
		disableAllButtons();
		//Get a reference to the current selected file and find the playlist text file that corresponds to it.
		Button clickedPlaylist = (Button) event.getSource();
		String playlist = clickedPlaylist.getText() + ".txt";
		currentPlaylistFile = new File("Playlists/"+playlist);

		//Create a playlist loader to scan and upload the names in the playlist.
		PlaylistLoader loader = new PlaylistLoader((Stage) uploadButton.getScene().getWindow(), null);
		List<String> names = loader.loadPlaylist(currentPlaylistFile);
		addNames(names);
		if (!hasConcatenated) {
			moveToWorkspace();
		}
	}

	private void moveToWorkspace() {
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

			Stage stage = (Stage) playlist1.getScene().getWindow();
			stage.setScene(new Scene(playlistScene));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This button takes the user to the upload file scene where they can upload a text file of names
	 * to practice.
	 */
	@FXML
	public void uploadButtonClicked() {
		try {
			Stage stage = (Stage) practiceButton.getScene().getWindow();
			Parent createScene = FXMLLoader.load(getClass().getResource("fxmlFiles/UploadScreen.fxml"));
			stage.setScene(new Scene(createScene));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * This button takes the user to the workspace creator where they can select which names they want to practice.
	 */
	@FXML
	public void practiceButtonClicked() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmlFiles/PlaylistCreator.fxml"));
			Parent playlistScene = fxmlLoader.load();
			Stage stage = (Stage) practiceButton.getScene().getWindow();
			stage.setScene(new Scene(playlistScene));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * This button takes the user back to the menu scene.
	 */
	@FXML
	public void backButtonClicked() {
		try {
			Stage stage = (Stage) backButton.getScene().getWindow();
			Parent createScene = FXMLLoader.load(getClass().getResource("fxmlFiles/StartMenu.fxml"));
			stage.setScene(new Scene(createScene));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Set all playlist related buttons invisible.
	 */
	public void setButtons() {

		//Start off with a clean slate by making all buttons invisible.
		for (int i=0; i<6; i++){
			playlistButtons.get(i).setVisible(false);
			deleteButtons.get(i).setVisible(false);
		}
		//Make the buttons visible and set the text to the corresponding name.
		for (int i=0; i<playlistNum; i++){
			playlistButtons.get(i).setVisible(true);
			playlistButtons.get(i).setText(playlistNames.get(i));
			deleteButtons.get(i).setVisible(true);
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
				for (String recordingName : names) {
					updatedNames.add(recordingName.substring(0,1).toUpperCase() + recordingName.substring(1).toLowerCase());
				}
				List<String> fileNames = new ArrayList<>();
				//Gets the file names for all these names
				for (String recording : updatedNames) {
					fileNames.add(referenceList.getRecording(recording).getFileName());
				}
				//Creates the concatenated recording and adds it to the workspace
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

	@Override
	public void addPlaylistRecordings(DatabaseList list) {
		List<String> concatenatedRecording = list.getRecordingNames();
		for (String recordingName : concatenatedRecording) {
			workspaceRecordings.add(list.getRecording(recordingName));
		}
		if (workspaceRecordings.getRecordingNames().size() == namesToLoad) {
			moveToWorkspace();
		}
	}

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