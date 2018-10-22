package namesayer.fxmlControllers;


import java.io.*;

import java.net.URL;
import java.util.*;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import namesayer.helperClasses.DatabaseList;
import namesayer.interfaces.PlayController;
import namesayer.recordingTypes.DemoRecording;
import namesayer.recordingTypes.Recording;

public class WorkspaceController implements Initializable, PlayController {


	//FXML variables
	@FXML
	Slider volumeSlider;
	@FXML
	Label playingLabel;
	@FXML
	Button nextButton;
	@FXML
	Button previousButton;
	@FXML
	Button backButton;
	@FXML
	Button rateButton;
	@FXML
	Label recordingNameLabel;
	@FXML
	Button recordButton;
	@FXML
	Label ratingLabel;
	@FXML
	Button toggleButton;
	@FXML
	Button returnButton;
	@FXML
	Button deleteButton;
	@FXML
	Button saveButton;
	@FXML
	Button playButton;
	@FXML
	ListView<String> dataListView;
	ObservableList<String> dataList = FXCollections.observableArrayList();
	@FXML
	ListView<String> ownListView;
	ObservableList<String> ownList = FXCollections.observableArrayList();
	@FXML
	TabPane tabPane;
	@FXML
	ImageView playStopImage;

	//Current index in the listView
	int currentIndex = 0;
	int ownCurrentIndex = 0;
	double volume;
	DatabaseList listOfRecordings;
	Boolean isOnDatabase = true;
	int recordClicked = 0;
	int playlistNum;


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//Set tooptips for buttons.
		toggleButton.setTooltip(new Tooltip("Change between demo and\npersonal recordings"));
		rateButton.setTooltip(new Tooltip("Rate the current demo\nrecording"));
		saveButton.setTooltip(new Tooltip("Save the current playlist"));
		recordButton.setTooltip(new Tooltip("Record a new personal recording for\nthe current demo recording"));


		//Initialize the number of playlists to zero prior to counting playlists.
		playlistNum = 0;

		//Set the volume to the max to begin with.
		volume = 1.0;
		volumeSlider.setValue(volume);


		//Listener to get the current name the user is clicking on.
		dataListView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
										String newValue) {
						//Sets the recording label to the current selected recording.
						recordingNameLabel.setText(newValue);

						currentIndex = dataListView.getSelectionModel().getSelectedIndex();
						setRating(newValue);
						refreshPersonalRecordings(newValue);
						ownCurrentIndex = 0;
					}
				});

		//Listener to get the current selected element in the personal recordings list.
		ownListView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
										String newValue) {
						ownCurrentIndex = ownListView.getSelectionModel().getSelectedIndex();
					}
				});

		//Listener to see which tab the user is currently on.
		tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (isOnDatabase) {
					isOnDatabase = false;
					deleteButton.setDisable(false);
					ownListView.getSelectionModel().select(ownCurrentIndex);
					ratingLabel.setVisible(false);
					rateButton.setDisable(true);
					playingLabel.setText("Now Playing: Personal Recording");
				} else {
					isOnDatabase = true;
					ratingLabel.setVisible(true);
					rateButton.setDisable(false);
					deleteButton.setDisable(true);
					playingLabel.setText("Now Playing: Demo Recording");
				}
			}
		});

		//Listener to observe changes in the volume slider. The volume field is set according to the value of the
		//of the slider.
		volumeSlider.valueProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				volume = volumeSlider.getValue();
			}

		});

		//Delete button is initially disabled prior to users selecting a recording.
		deleteButton.setDisable(true);
	}

	/*
	 * This method updates the list view with the list of personal recordings for a given recording
	 * object.
	 */
	public void refreshPersonalRecordings(String recordingName) {
		DemoRecording currentDatabaseRecording = listOfRecordings.getRecording(recordingName);
		ownList = currentDatabaseRecording.getUserAttempts();
		ownListView.setItems(ownList);
	}

	/*
	 * This method starts playing the current name.
	 */
	@FXML
	private void playButtonClicked(ActionEvent event) {

		if (isOnDatabase) {
			String currentRecordingName = dataListView.getSelectionModel().getSelectedItem();
			DemoRecording currentRecording = listOfRecordings.getRecording(currentRecordingName);
			setUpForPlaying(currentRecording, volume);
		} else {
			if (ownListView.getSelectionModel().getSelectedItem() != null) {
				String currentName = ownListView.getSelectionModel().getSelectedItem();
				String databaseName = dataListView.getSelectionModel().getSelectedItem();
				DemoRecording databaseRecording = listOfRecordings.getRecording(databaseName);
				setUpForPlaying(databaseRecording.getUserRecording(currentName), volume);
			}
		}

	}

	private void setUpForPlaying(Recording recording, double volume) {
		recording.setController(this);
		if (recording.isPlaying()) {
			recording.stopPlaying();
		} else {
			recording.play(volume);
			playStopImage.setImage(new Image("namesayer/imageResources/icons8-stop-filled-100.png"));
		}
	}

	public void playingFinished() {
		playStopImage.setImage(new Image("namesayer/imageResources/icons8-play-filled-100.png"));
	}

	/*
	 * This method takes the user to the next name on the play queue.
	 */
	@FXML
	private void nextButtonClicked(ActionEvent event) {
		if (isOnDatabase) {
			if (currentIndex < dataList.size() - 1) {

				currentIndex++;
				dataListView.scrollTo(currentIndex);
				dataListView.getSelectionModel().select(currentIndex);
				String currentName = dataListView.getSelectionModel().getSelectedItem();
				recordingNameLabel.setText(currentName);
			}
		} else {
			if (ownList != null) {
				if (ownCurrentIndex < ownList.size() - 1) {
					ownCurrentIndex++;
					ownListView.scrollTo(ownCurrentIndex);
					ownListView.getSelectionModel().select(ownCurrentIndex);
				}
			}
		}
	}

	/*
	 * This method takes the user to the previous name on the play queue.
	 */
	@FXML
	public void previousButtonClicked(ActionEvent event) {
		if (isOnDatabase) {
			if (currentIndex > 0) {
				currentIndex--;
				dataListView.scrollTo(currentIndex);
				dataListView.getSelectionModel().select(currentIndex);
				String currentName = dataListView.getSelectionModel().getSelectedItem();
				recordingNameLabel.setText(currentName);
			}
		} else {
			if (ownList != null) {
				if (ownCurrentIndex > 0) {
					ownCurrentIndex--;
					ownListView.scrollTo(ownCurrentIndex);
					ownListView.getSelectionModel().select(ownCurrentIndex);
				}
			}
		}
	}

	/*
	 * This method takes the user back to the workspace creator scene.
	 */
	@FXML
	public void backButtonClicked(ActionEvent event) throws Exception {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmlFiles/PlaylistCreator.fxml"));
			Parent createScene = fxmlLoader.load();
			PlaylistCreatorController controller = fxmlLoader.getController();
			controller.addPlaylistRecordings(listOfRecordings);
			Stage stage = (Stage) backButton.getScene().getWindow();
			stage.setScene(new Scene(createScene));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method takes the user to the rate screen.
	 */
	@FXML
	public void rateButtonClicked(ActionEvent event) throws Exception {
		String currentName = dataListView.getSelectionModel().getSelectedItem();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmlFiles/RateScreen.fxml"));
		Parent rateSceneParent = fxmlLoader.load();
		RateScreenController controller = fxmlLoader.getController();
		controller.setCurrentName(currentName);
		controller.setWorkSpaceController(this);
		Scene rateScene = new Scene(rateSceneParent);
		Stage rateStage = new Stage();
		rateStage.setScene(rateScene);

		//Disable the background window when the rate stage is displayed.
		rateStage.initModality(Modality.WINDOW_MODAL);
		rateStage.initOwner(((Node)event.getSource()).getScene().getWindow() );
		rateStage.show();
	}

	/*
	 * This method handles when the user wants to delete an own recording.
	 */
	@FXML
	public void creationDeleteButtonClicked(ActionEvent event) {
		String ownCurrentName = ownListView.getSelectionModel().getSelectedItem();

		if (ownCurrentName != null) {
			String currentName = dataListView.getSelectionModel().getSelectedItem();
			String ownCurrentFileName = listOfRecordings.getRecording(currentName).getUserRecording(ownCurrentName).getFileName();
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Delete Confirmation");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you want to delete this recording?");
			Optional<ButtonType> action = alert.showAndWait();

			if (action.get() == ButtonType.OK) {
				File creationFile = new File("Resources/PersonalRecordings/");

				for (final File fileEntry : creationFile.listFiles()) {

					String currentFile = fileEntry.getName();

					if (currentFile.equals(ownCurrentFileName)) {
						fileEntry.delete();
						listOfRecordings.getRecording(currentName).deleteAttempt(ownCurrentName);
						refreshPersonalRecordings(currentName);
						ownListView.getSelectionModel().select(0);
					}
				}
			}
		}

	}

	/*
	 * This method handles when the user wants to record their own recording.
	 */
	@FXML
	public void recordButtonClicked(ActionEvent event) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmlFiles/RecordScreen.fxml"));
			Parent recordSceneParent = fxmlLoader.load();
			RecordController controller = fxmlLoader.getController();
			DemoRecording currentDatabaseRecording = listOfRecordings.getRecording(dataListView.getSelectionModel().getSelectedItem());
			controller.passInformation(currentDatabaseRecording, this, volume);
			Scene recordScene = new Scene(recordSceneParent);
			Stage recordStage = new Stage();
			recordStage.setScene(recordScene);

			//Disable the background window when the record stage is displayed.
			recordStage.initModality(Modality.WINDOW_MODAL);
			recordStage.initOwner(((Node)event.getSource()).getScene().getWindow() );

			recordStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method is invoked when the user wants to save a playlist.
	 */
	@FXML
	public void saveButtonClicked(ActionEvent event) {
		playlistNum = 0;
		playlistCount();
		if(playlistNum < 6) {
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmlFiles/SaveScreen.fxml"));
				Parent rateSceneParent = fxmlLoader.load();
				SaveScreenController controller = fxmlLoader.getController();
				controller.setRecordingList(listOfRecordings);
				controller.setButton(saveButton);
				Scene rateScene = new Scene(rateSceneParent);
				Stage rateStage = new Stage();
				rateStage.setScene(rateScene);

				//Disable the background window when the rate stage is displayed.
				rateStage.initModality(Modality.WINDOW_MODAL);
				rateStage.initOwner(((Node)event.getSource()).getScene().getWindow() );
				rateStage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmlFiles/ReplacePlaylist.fxml"));
				Parent deleteSceneParent = fxmlLoader.load();
				DeletePlaylistController controller = fxmlLoader.getController();
				controller.setRecordingList(listOfRecordings);
				controller.setButton(saveButton);
				Scene deleteScene = new Scene(deleteSceneParent);
				Stage deleteStage = new Stage();
				deleteStage.setScene(deleteScene);

				//Disable the background window when the rate stage is displayed.
				deleteStage.initModality(Modality.WINDOW_MODAL);
				deleteStage.initOwner(((Node)event.getSource()).getScene().getWindow() );
				deleteStage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@FXML
	private void playLoop(ActionEvent event) {
		if (!ownListView.getItems().isEmpty()) {
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxmlFiles/LoopScene.fxml"));
				Parent loopParent = fxmlLoader.load();
				LoopSceneController controller = fxmlLoader.getController();
				controller.passRecording(listOfRecordings.getRecording(dataListView.getSelectionModel().getSelectedItem()), volume);
				Scene loopScene = new Scene(loopParent);
				Stage loopStage = new Stage();
				loopStage.setScene(loopScene);

				//Disable the background window when the rate stage is displayed.
				loopStage.initModality(Modality.WINDOW_MODAL);
				loopStage.initOwner(((Node) event.getSource()).getScene().getWindow());
				loopStage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("No Personal Recordings");
			alert.setHeaderText(null);
			alert.setContentText("There are no personal recordings for this demo to loop with");
			Optional<ButtonType> action = alert.showAndWait();
		}
	}

	/*
	 * When the toggle button is pressed, the view switches to either the personal recordings or the
	 * database recordings.
	 */
	@FXML
	private void toggleTab() {
		if (isOnDatabase) {
			tabPane.getSelectionModel().select(1);
		} else {
			tabPane.getSelectionModel().select(0);
		}
	}

	/*
	 * This method takes the user back to the menu scene.
	 */
	@FXML
	private void returnToStart() {
		if (!saveButton.isDisabled()) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Return to Start");
			alert.setHeaderText("Any unsaved playlist will be lost");
			alert.setContentText("Are you sure you want to continue?");
			Optional<ButtonType> action = alert.showAndWait();
			if (action.get() == ButtonType.OK) {
				transitionToStart();
			}
		} else {
			transitionToStart();
		}
	}

	private void transitionToStart() {
		try {
			Stage stage = (Stage) returnButton.getScene().getWindow();
			Parent createScene = FXMLLoader.load(getClass().getResource("fxmlFiles/StartMenu.fxml"));
			stage.setScene(new Scene(createScene));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 *
	 */
	public void setWorkspaceRecordingsAndController(DatabaseList recordings, ObservableList<String> recordingNames) {
		dataList = recordingNames;
		dataListView.setItems(dataList);
		listOfRecordings = recordings;

		dataListView.scrollTo(currentIndex);
		dataListView.getSelectionModel().select(currentIndex);
		String currentName = dataListView.getSelectionModel().getSelectedItem();
		recordingNameLabel.setText(currentName);
	}

	private void playlistCount() {
		File folder = new File("Resources/Playlists/");
		File[] listOfFiles = folder.listFiles();
		for(File file : listOfFiles) {
			if (file.isFile() && file.getName().endsWith(".txt")) {
				playlistNum ++;
			}
		}
	}

	/*
	 * This method writes the rating for a recording to a file.
	 */
	public void setRating(String currentName) {
		File file = new File("./Resources/Review" + currentName + ".txt");
		if (file.exists()) {
			int[] ratingArray = new int[2];
			Scanner scanner = null;
			int count = 0;
			try {
				scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					ratingArray[count] = Integer.parseInt(scanner.nextLine());
					count++;
				}

				//Calculates the average score of the recording by adding the ratings and
				//dividing by the total number of reviews.
				int ratingSum = ratingArray[0];
				double averageRating = (double) ratingSum / (ratingArray[1]);
				updateRating(averageRating, currentName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			updateRating(-1, currentName);
		}
	}

	/*
	 * This method updates the rating on the UI.
	 */
	public void updateRating(double rating, String name) {

		//If the rating is above 2.5, print the rating normally.
		if (rating > 2.5) {
			ratingLabel.setText(String.format("Average Rating: %.2f", rating));
			if (isBadRecording(name)) {
				removeFromBadFile(name);
			}
		}
		//If the rating is below or equal to 2.5, add a bad quality warning to warn the user.
		else if (rating >= 0) {
			ratingLabel.setText(String.format("Average Rating: %.2f *Poor Quality*", rating));
			if (!isBadRecording(name)) {
				addToBadFile(name);
			}
		} else {
			if (isBadRecording(name)) {
				removeFromBadFile(name);
			}
			ratingLabel.setText("Not Yet Rated");
		}
	}

	/*
	 * This method removes the name of a recording from the 'bad recording' list once it's
	 * rating exceeds 2.5.
	 */
	public void removeFromBadFile(String name) {

		File tmpFile = new File("./Resources/Reviewtemp.txt");
		File file = new File("./Resources/ReviewBadRecordings.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile));

			String lineToRemove = name;
			String currentLine;

			while((currentLine = reader.readLine()) != null) {
				String trimmedLine = currentLine.trim();
				if(trimmedLine.equals(lineToRemove)) continue;
				writer.write(currentLine + System.getProperty("line.separator"));
			}
			writer.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method adds the name of the recording to the bad recording list once its rating falls
	 * below 2.5
	 */
	public void addToBadFile(String name) {
		File file = new File("./Resources/ReviewBadRecordings.txt");

		//Append the given name to the BadRecordings file.
		if (file.exists()) {
			try {
				Writer output;
				output = new BufferedWriter(new FileWriter(file, true));
				output.append(name);
				((BufferedWriter) output).newLine();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				PrintWriter writer = new PrintWriter(file);
				writer.println(name);
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * This method checks if the recording of the name passed is in the BadRecordings file.
	 */
	public Boolean isBadRecording(String name) {
		File file = new File("./Resources/ReviewBadRecordings.txt");

		//Scans the file line by line to check if the given name is in the file.
		if (file.exists()) {
			try {
				Scanner scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					if (scanner.nextLine().contains(name)) {
						return true;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			return false;
		}
		return false;
	}
}