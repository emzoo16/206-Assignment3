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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class WorkSpaceController implements Initializable{
	
	//These are stubs to be replaced with the custom list passed in from the workspaceCreator
	//sample would be replaced with the list and sampleCreations with the folder containing all
	//user recordings.
	
	public File creationDir = new File("./DatabaseRecordings");
	
	
	String currentName;
	
	//Current index in the listView 
	int currentIndex = 0;
	int ownCurrentIndex = 0;

	//FXML variables
	@FXML
	ProgressBar progressBar;
	@FXML
	Button startButton;
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
	Button creationPlayButton;
	@FXML
	Button creationDeleteButton;
	@FXML
	Button creationNextButton;
	@FXML
	Button creationPreviousButton;
	@FXML
	Button recordButton;
	@FXML
	Label ratingLabel;
	@FXML
	ListView<String> dataListView;
	ObservableList<String> dataList = FXCollections.observableArrayList();
	@FXML
	ListView<String> ownListView;
	ObservableList<String> ownList = FXCollections.observableArrayList();

	RecordingList listOfRecordings;


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//Listener to get the current name the user is clicking on.
		dataListView.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						currentName = dataListView.getSelectionModel().getSelectedItem();
						findOwnRecording(newValue);
						recordingNameLabel.setText(newValue);
						currentIndex = dataListView.getSelectionModel().getSelectedIndex();
						setRating(currentName);
						
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
		
		
	}
	
	//This method starts playing the current name.
	@FXML
	public void playButtonClicked(ActionEvent event) {
		
		String currentRecordingName = dataListView.getSelectionModel().getSelectedItem();
		if(recordingNameLabel.getText() != currentRecordingName) {
			recordingNameLabel.setText(currentRecordingName);
		}
		
		Recording currentRecording = listOfRecordings.getRecording(currentRecordingName);
		currentRecording.play();
		recordingNameLabel.setText(currentRecordingName);
		
	}
	
	//This method takes the user to the next name on the play queue.
	@FXML
	public void nextButtonClicked(ActionEvent event) {
	
		if (currentIndex < dataList.size()-1) {
		currentIndex++;
		dataListView.scrollTo(currentIndex);
		dataListView.getSelectionModel().select(currentIndex);
		String currentName = dataListView.getSelectionModel().getSelectedItem();
		recordingNameLabel.setText(currentName);
		}
		
	}
	
	//This method takes the user to the previous name on the play queue.
	@FXML
	public void previousButtonClicked(ActionEvent event) {

		if (currentIndex > 0) {
		currentIndex--;
		dataListView.scrollTo(currentIndex);
		dataListView.getSelectionModel().select(currentIndex);
		String currentName = dataListView.getSelectionModel().getSelectedItem();
		recordingNameLabel.setText(currentName);
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
		 
		 currentName = dataListView.getSelectionModel().getSelectedItem();
		 System.out.println(currentName);
		 FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rateScreen.fxml"));
         Parent createSceneParent = fxmlLoader.load();
         RateScreenController controller = fxmlLoader.getController();
         controller.setCurrentName(currentName);
         Scene createScene = new Scene(createSceneParent);

         Stage createStage = new Stage();
         createStage.setScene(createScene);
         createStage.show();
	}
	
	@FXML
	public void creationPreviousButtonClicked() {
		if(ownList != null) {
			
			if (ownCurrentIndex > 0) {
				ownCurrentIndex--;
				ownListView.scrollTo(ownCurrentIndex);
				ownListView.getSelectionModel().select(ownCurrentIndex);
				String currentName = ownListView.getSelectionModel().getSelectedItem();
				}
		}
	}
	
	@FXML
	public void creationNextButtonClicked() {
		if(ownList != null) {
			
			if (ownCurrentIndex < ownList.size()-1) {
				ownCurrentIndex++;
				ownListView.scrollTo(ownCurrentIndex);
				ownListView.getSelectionModel().select(ownCurrentIndex);
				String currentName = ownListView.getSelectionModel().getSelectedItem();
				}
		}
	}
	
	//This method handles when the user wants to play an own recording.
	@FXML
	public void creationPlayButtonClicked(ActionEvent event) {
		String currentName = ownListView.getSelectionModel().getSelectedItem();
		recordingNameLabel.setText(currentName);
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
				File creationFile = new File("./DatabaseRecordings/"+currentName);
				
				for (final File fileEntry : creationFile.listFiles()) {
		
					String currentFile = fileEntry.getName();
					System.out.println(currentFile);
					String pathSegment = currentFile.substring(currentFile.lastIndexOf("_")+1,
							currentFile.lastIndexOf("."));
					
					if (pathSegment.equals(ownCurrentName)) {
						fileEntry.delete();
						findOwnRecording(currentName);
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
			Scene createScene = new Scene(createSceneParent);

			Stage createStage = new Stage();
			createStage.setScene(createScene);
			createStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//This method iterates through the own recordings folder and adds any recordings of
	//the given name to the right listView.
	public void findOwnRecording(String name) {
		
		File ownDir = new File("./DatabaseRecordings/"+name);
		List<String> list = new ArrayList<String>();
		if (ownDir.exists()) {
		
			for (final File fileEntry : ownDir.listFiles()) {
				if (fileEntry.getName().contains(name)) {
					String listName = fileEntry.getName();
					String pathSegment = listName.substring(listName.lastIndexOf("_")+1,
							listName.lastIndexOf("."));
					list.add(pathSegment);
				}
			}
		}
			ownList = FXCollections.observableArrayList(list);
			ownListView.setItems(ownList);
			
			if (list.size()>0) {
				ownCurrentIndex = 0;
				ownListView.scrollTo(ownCurrentIndex);
				ownListView.getSelectionModel().select(currentIndex);
			}
		
	}

	public void setWorkspaceRecordings(RecordingList recordings) {
		dataList = FXCollections.observableArrayList(recordings.getRecordingNames());
		dataListView.setItems(recordings.getRecordingNames());
		listOfRecordings = recordings;
		
		dataListView.scrollTo(currentIndex);
		dataListView.getSelectionModel().select(currentIndex);
		String currentName = dataListView.getSelectionModel().getSelectedItem();
		recordingNameLabel.setText(currentName);
	
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
