package namesayer;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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

public class WorkSpaceController implements Initializable{
	
	//These are stubs to be replaced with the custom list passed in from the workspaceCreator
	//sample would be replaced with the list and sampleCreations with the folder containing all
	//user recordings.
	
	//Current index in the listView 
	int currentIndex = 0;
	int ownCurrentIndex = 0;

	//FXML variables
	@FXML
	ProgressBar progressBar;
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
						recordingNameLabel.setText(newValue);
						currentIndex = dataListView.getSelectionModel().getSelectedIndex();
						setRating(newValue);
						refreshPersonalRecordings(newValue);
						ownCurrentIndex = 0;
					}
	        });
		
		ownListView.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						ownCurrentIndex = ownListView.getSelectionModel().getSelectedIndex();
					}
	        });
		tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (isOnDatabase) {
					System.out.println("Switched to personal");
					isOnDatabase = false;
					deleteButton.setDisable(false);
					ownListView.getSelectionModel().select(ownCurrentIndex);
				} else {
					System.out.println("Switched to database");
					isOnDatabase = true;
					deleteButton.setDisable(true);
				}
			}
		});
	}

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
			currentRecording.play();
		} else {
			String currentName = ownListView.getSelectionModel().getSelectedItem();
			String databaseName = dataListView.getSelectionModel().getSelectedItem();
			DatabaseRecording databaseRecording = listOfRecordings.getRecording(databaseName);
			databaseRecording.getUserRecording(currentName).play();
		}
		
	}
	
	//This method takes the user to the next name on the play queue.
	@FXML
	public void nextButtonClicked(ActionEvent event) {
		if (isOnDatabase) {
			System.out.println("Wrong place");
			if (currentIndex < dataList.size() - 1) {

				currentIndex++;
				dataListView.scrollTo(currentIndex);
				dataListView.getSelectionModel().select(currentIndex);
				String currentName = dataListView.getSelectionModel().getSelectedItem();
				recordingNameLabel.setText(currentName);
			}
		} else {
			if(ownList != null) {
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
			if(ownList != null) {
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
	public void backButtonClicked(ActionEvent event) throws Exception{
		Parent root = FXMLLoader.load(getClass().getResource("workSpaceCreator.fxml"));
		Scene recordingScene = new Scene(root);
		Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		currentStage.setScene(recordingScene);
		currentStage.show();
	}
	
	//This method takes the user to the rate screen.
	@FXML
	public void rateButtonClicked(ActionEvent event) throws Exception{
		 String currentName = dataListView.getSelectionModel().getSelectedItem();
		 FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rateScreen.fxml"));
         Parent createSceneParent = fxmlLoader.load();
         RateScreenController controller = fxmlLoader.getController();
         controller.setCurrentName(currentName);
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
					System.out.println(currentFile);
					String pathSegment = currentFile.substring(currentFile.lastIndexOf("_")+1,
							currentFile.lastIndexOf("."));
					
					if (pathSegment.equals(ownCurrentName)) {
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

	@FXML
	private void returnToStart() {
		try {
			Stage stage = (Stage) returnButton.getScene().getWindow();
			Parent createScene = FXMLLoader.load(getClass().getResource("starMenu.fxml"));
			stage.setScene(new Scene(createScene, 700, 500));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setWorkspaceRecordingsAndController(DatabaseList recordings, WorkSpaceController controller) {
		dataList = recordings.getRecordingNames();
		dataListView.setItems(dataList);
		listOfRecordings = recordings;
		
		dataListView.scrollTo(currentIndex);
		dataListView.getSelectionModel().select(currentIndex);
		String currentName = dataListView.getSelectionModel().getSelectedItem();
		recordingNameLabel.setText(currentName);

		selfController = controller;
		refreshPersonalRecordings(dataListView.getSelectionModel().getSelectedItem());
	}
	
	public void setRating(String currentName) {
		
		double ratingNumber = getAverageRating(currentName);
		if (ratingNumber >= 0) {
			ratingLabel.setText(String.format("Average Rating: %.2f",ratingNumber));
		}else {
			ratingLabel.setText("Not Yet Rated");
		}
	}
	
	public double getAverageRating(String currentName) {
		int count = 0;
		int total = 0;
		
		File file = new File("./Review/" + currentName + ".txt");
		if (file.exists()) {
		BufferedReader reader = null;

		try {
		    reader = new BufferedReader(new FileReader(file));
		    String text = null;

		    while ((text = reader.readLine()) != null) {
		    	count++;
		        total = Integer.parseInt(text) + total;
		    }
		    
		    if (count > 0) {
		    	return (double)total/count;
		    }
		    	return -1;
		    	
		}catch(FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}
		}
		return -1;
	}
	
}
