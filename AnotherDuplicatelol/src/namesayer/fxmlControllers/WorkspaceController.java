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
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import namesayer.helperClasses.DatabaseList;
import namesayer.helperClasses.UIManager;
import namesayer.helperClasses.WorkspaceModel;
import namesayer.interfaces.ParentStageController;
import namesayer.interfaces.PlayController;
import namesayer.recordingTypes.DemoRecording;
import namesayer.recordingTypes.Recording;

public class WorkspaceController implements Initializable, PlayController, ParentStageController {


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
	private WorkspaceModel model;
	private boolean playing;
	Recording playingRecording;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = WorkspaceModel.getInstance();
		initializeModelValues();
		refreshPersonalRecordings(dataListView.getSelectionModel().getSelectedItem());
		model.setCurrentDemoRecording(listOfRecordings.getRecording(dataListView.getSelectionModel().getSelectedItem()));
		model.setStageController(this);
		//Set tooptips for buttons.
		toggleButton.setTooltip(new Tooltip("Change between demo and\npersonal recordings"));
		rateButton.setTooltip(new Tooltip("Rate the current demo\nrecording"));
		saveButton.setTooltip(new Tooltip("Save the current playlist"));
		recordButton.setTooltip(new Tooltip("Record a new personal recording for\nthe current demo recording"));

		//Set the volume to the max to begin with.
		volume = 1.0;
		model.setVolume(volume);
		volumeSlider.setValue(volume);


		//Listener to get the current name the user is clicking on.
		dataListView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
										String newValue) {
						//Sets the recording label to the current selected recording.
						recordingNameLabel.setText(newValue);
						model.setCurrentDemoRecording(listOfRecordings.getRecording(newValue));
						currentIndex = dataListView.getSelectionModel().getSelectedIndex();
						refreshRating();
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
				model.setVolume(volume);
			}

		});

		//Delete button is initially disabled prior to users selecting a recording.
		deleteButton.setDisable(true);
	}

	public void stageHasClosed() {
		refreshPersonalRecordings(dataListView.getSelectionModel().getSelectedItem());
		refreshRating();
	}

	public void initializeModelValues() {
		dataList = model.getCurrentRecordingsDisplay();
		dataListView.setItems(dataList);
		listOfRecordings = model.getCurrentWorkspaceRecordings();
		dataListView.scrollTo(currentIndex);
		dataListView.getSelectionModel().select(currentIndex);
		String currentName = dataListView.getSelectionModel().getSelectedItem();
		recordingNameLabel.setText(currentName);
		playing = false;
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
		if (playing) {
			playingRecording.stopPlaying();
		} else {
			recording.play(volume);
			playingRecording = recording;
			playing = true;
			playStopImage.setImage(new Image("namesayer/imageResources/icons8-stop-filled-100.png"));
		}
	}

	public void playingFinished() {
		playing = false;
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
	public void backButtonClicked(ActionEvent event) {
		UIManager.changeScenes("fxmlFiles/PlaylistCreator.fxml");
	}

	/*
	 * This method takes the user to the rate screen.
	 */
	@FXML
	public void rateButtonClicked() {
		UIManager.openStage("fxmlFiles/RateScreen.fxml");
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
	public void recordButtonClicked() {
		UIManager.openStage("fxmlFiles/RecordScreen.fxml");
	}

	/*
	 * This method is invoked when the user wants to save a playlist.
	 */
	@FXML
	public void saveButtonClicked() {
		if(playlistCount() < 6) {
			UIManager.openStage("fxmlFiles/SaveScreen.fxml");
		} else {
			UIManager.openStage("fxmlFiles/ReplacePlaylist.fxml");
		}

	}

	@FXML
	private void playLoop() {
		if (!ownListView.getItems().isEmpty()) {
			UIManager.openStage("fxmlFiles/LoopScene.fxml");
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
		UIManager.changeScenes("fxmlFiles/StartMenu.fxml");
	}

	private int playlistCount() {
		int playlistNum = 0;
		File folder = new File("Resources/Playlists/");
		File[] listOfFiles = folder.listFiles();
		for(File file : listOfFiles) {
			if (file.isFile() && file.getName().endsWith(".txt")) {
				playlistNum ++;
			}
		}
		return playlistNum;
	}

	/*
	 * This method updates the rating on the UI.
	 */
	public void refreshRating() {
		double rating = listOfRecordings.getRecording(dataListView.getSelectionModel().getSelectedItem()).getRating();
		//If the rating is above 2.5, print the rating normally.
		if (rating > 2.5) {
			ratingLabel.setText(String.format("Average Rating: %.2f", rating));
		}
		//If the rating is below or equal to 2.5, add a bad quality warning to warn the user.
		else if (rating >= 0) {
			ratingLabel.setText(String.format("Average Rating: %.2f *Poor Quality*", rating));
		} else {
			ratingLabel.setText("Not Yet Rated");
		}
	}
}