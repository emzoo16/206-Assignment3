package namesayer;


import java.io.*;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.stage.Stage;

public class WorkSpaceController implements Initializable {

	//These are stubs to be replaced with the custom list passed in from the workspaceCreator
	//sample would be replaced with the list and sampleCreations with the folder containing all
	//user recordings.

	//Current index in the listView 
	int currentIndex = 0;
	int ownCurrentIndex = 0;
	double volume;
	//FXML variables
	@FXML
	ProgressBar progressBar;
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
	ListView<String> dataListView;
	ObservableList<String> dataList = FXCollections.observableArrayList();
	@FXML
	ListView<String> ownListView;
	ObservableList<String> ownList = FXCollections.observableArrayList();
	@FXML
	TabPane tabPane;

	DatabaseList listOfRecordings;
	WorkSpaceController selfController;
	Boolean isOnDatabase = true;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//Listener to get the current name the user is clicking on.
		dataListView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
										String newValue) {
						//Sets the recording label to the current selected recording.
						recordingNameLabel.setText(newValue);
						//Resets the progressBar.
						progressBar.setProgress(0.0);
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
					playingLabel.setText("Now Playing: Personal Recording");
				} else {
					isOnDatabase = true;
					deleteButton.setDisable(true);
					playingLabel.setText("Now Playing: Demo Recording (Database)");
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
		DatabaseRecording currentDatabaseRecording = listOfRecordings.getRecording(recordingName);
		ownList = currentDatabaseRecording.getUserAttempts();
		ownListView.setItems(ownList);
	}

	//This method starts playing the current name.
	@FXML
	public void playButtonClicked(ActionEvent event) {
		if (isOnDatabase) {
			String currentRecordingName = dataListView.getSelectionModel().getSelectedItem();
			Recording currentRecording = listOfRecordings.getRecording(currentRecordingName);
			currentRecording.play(volume, progressBar);
		} else {
			String currentName = ownListView.getSelectionModel().getSelectedItem();
			String databaseName = dataListView.getSelectionModel().getSelectedItem();
			DatabaseRecording databaseRecording = listOfRecordings.getRecording(databaseName);
			databaseRecording.getUserRecording(currentName).play(volume, progressBar);
		}

	}

	//This method takes the user to the next name on the play queue.
	@FXML
	public void nextButtonClicked(ActionEvent event) {
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

	//This method takes the user to the previous name on the play queue.
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

	//This method takes the user back to the workspace creator scene.
	@FXML
	public void backButtonClicked(ActionEvent event) throws Exception {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("workSpaceCreator.fxml"));
			Parent createScene = fxmlLoader.load();
			WorkSpaceCreatorController controller = fxmlLoader.getController();
			controller.setWorkspaceRecordings(dataList);
			Stage stage = (Stage) backButton.getScene().getWindow();
			stage.setScene(new Scene(createScene, 700, 500));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//This method takes the user to the rate screen.
	@FXML
	public void rateButtonClicked(ActionEvent event) throws Exception {
		String currentName = dataListView.getSelectionModel().getSelectedItem();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rateScreen.fxml"));
		Parent createSceneParent = fxmlLoader.load();
		RateScreenController controller = fxmlLoader.getController();
		controller.setCurrentName(currentName);
		controller.setWorkSpaceController(this);
		Scene createScene = new Scene(createSceneParent);
		Stage createStage = new Stage();
		createStage.setScene(createScene);
		createStage.show();
	}

	//This method handles when the user wants to delete an own recording.
	@FXML
	public void creationDeleteButtonClicked(ActionEvent event) {
		String ownCurrentName = ownListView.getSelectionModel().getSelectedItem();
		String currentName = dataListView.getSelectionModel().getSelectedItem();
		String ownCurrentFileName = listOfRecordings.getRecording(currentName).getUserRecording(ownCurrentName).getFileName();
		if (ownCurrentName != null) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Delete Confirmation");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you want to delete?");
			Optional<ButtonType> action = alert.showAndWait();

			if (action.get() == ButtonType.OK) {
				File creationFile = new File("PersonalRecordings/");

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

	//This method handles when the user wants to record their own recording.
	@FXML
	public void recordButtonClicked(ActionEvent event) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("record.fxml"));
			Parent createSceneParent = fxmlLoader.load();
			RecordController controller = fxmlLoader.getController();
			DatabaseRecording currentDatabaseRecording = listOfRecordings.getRecording(dataListView.getSelectionModel().getSelectedItem());
			controller.passInformation(currentDatabaseRecording, selfController);			
			Scene createScene = new Scene(createSceneParent);
			Stage createStage = new Stage();
			createStage.setScene(createScene);
			createStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
		try {
			Stage stage = (Stage) returnButton.getScene().getWindow();
			Parent createScene = FXMLLoader.load(getClass().getResource("startMenu.fxml"));
			stage.setScene(new Scene(createScene, 700, 500));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setWorkspaceRecordingsAndController(DatabaseList recordings, ObservableList<String> recordingNames, WorkSpaceController controller) {
		dataList = recordingNames;
		dataListView.setItems(dataList);
		listOfRecordings = recordings;

		dataListView.scrollTo(currentIndex);
		dataListView.getSelectionModel().select(currentIndex);
		String currentName = dataListView.getSelectionModel().getSelectedItem();
		recordingNameLabel.setText(currentName);

		selfController = controller;
		refreshPersonalRecordings(dataListView.getSelectionModel().getSelectedItem());
	}

	/*
	 * This method writes the rating for a recording to a file.
	 */
	public void setRating(String currentName) {
		File file = new File("./Review/" + currentName + ".txt");
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
	 * This method updates the rating shown on the UI.
	 */
	public void updateRating(double rating, String name) {
		if (rating > 2.5) {
			ratingLabel.setText(String.format("Average Rating: %.2f", rating));
			if (isBadRecording(name)) {
				removeFromBadFile(name);
			}
		} else if (rating >= 0) {
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
		File tmpFile = new File("./Review/temp.txt");
		File file = new File("./Review/BadRecordings.txt");
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
			boolean successful = tmpFile.renameTo(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method adds the name of the recording to the bad recording list once its rating falls
	 * below 2.5
	 */
	public void addToBadFile(String name) {
		File file = new File("./Review/BadRecordings.txt");
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
	 * This method checks if the recording is of bad quality.
	 */
	public Boolean isBadRecording(String name) {
		File file = new File("./Review/BadRecordings.txt");
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
	
	public double getVolume() {
		return volume;
	}
	
	

}
