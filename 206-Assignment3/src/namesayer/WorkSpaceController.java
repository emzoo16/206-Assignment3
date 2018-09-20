package namesayer;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

public class WorkSpaceController implements Initializable{
	
	//These are stubs to be replaced with the custom list passed in from the workspaceCreator
	//sample would be replaced with the list and sampleCreations with the folder containing all
	//user recordings.
	
	public File sampleDir = new File("./sample");
	public File creationDir = new File("./sampleCreations");
	
	
	String currentCreation;
	
	//Current index in the listView 
	int currentIndex=0;
	
	//FXML variables
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
	Button recordButton;
	@FXML
	ListView<String> dataListView;
	ObservableList<String> dataList = FXCollections.observableArrayList();
	@FXML
	ListView<String> ownListView;
	ObservableList<String> ownList = FXCollections.observableArrayList();


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Initialises the listview. 
		loadList();
		dataListView.scrollTo(currentIndex);
		dataListView.getSelectionModel().select(currentIndex);
		
		//Listener to get the current name the user is clicking on.
		dataListView.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						findOwnRecording(newValue);
					}
	        });
	}
	
	//This method starts playing the current name.
	@FXML
	public void playButtonClicked(ActionEvent event) {
		
		
	}
	
	//This method takes the user to the next name on the play queue.
	@FXML
	public void nextButtonClicked(ActionEvent event) {
		currentIndex++;
		if (currentIndex < dataList.size()) {
		dataListView.scrollTo(currentIndex);
		dataListView.getSelectionModel().select(currentIndex);
		String currentName = dataListView.getSelectionModel().getSelectedItem();
		recordingNameLabel.setText(currentName);
		}
		
	}
	
	//This method takes the user to the previous name on the play queue.
	@FXML
	public void previousButtonClicked(ActionEvent event) {
		currentIndex--;
		if (currentIndex >= 0) {
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
		
		Parent createSceneParent = FXMLLoader.load(getClass().getResource("rateScreen.fxml"));
		Scene createScene = new Scene(createSceneParent);

		Stage createStage = new Stage();
		createStage.setScene(createScene);
		createStage.show();
	}
	
	//This method handles when the user wants to play an own recording.
	@FXML
	public void creationPlayButtonClicked(ActionEvent event) {
		
	}
	
	//This method handles when the user wants to delete an own recording.
	@FXML
	public void creationDeleteButtonClicked(ActionEvent event) {
		
	}
	
	//This method handles when the user wants to record their own recording.
	@FXML
	public void recordButtonClicked(ActionEvent event) {
		
	}
	
	//This method would be replaced with one that takes the list passed through workspaceCreator
	//and loads it onto the left listView.
	public void loadList() {
		List<String> list = new ArrayList<String>();
		for (final File fileEntry : sampleDir.listFiles()) {
			if (fileEntry.getName().endsWith(".txt")) {
				String listName = fileEntry.getName().substring(0, fileEntry.getName().length() - 4);
				list.add(listName.replaceAll("@", " "));
			}
		}
		dataList = FXCollections.observableArrayList(list);
		dataListView.setItems(dataList);
	}
	
	//This method iterates through the own recordings folder and adds any recordings of
	//the given name to the right listView.
	public void findOwnRecording(String name) {
		List<String> list = new ArrayList<String>();
		for (final File fileEntry : creationDir.listFiles()) {
			if (fileEntry.getName().contains(name)) {
				String listName = fileEntry.getName().substring(0, fileEntry.getName().length() - 4);
				list.add(listName);
			}
		}
		ownList = FXCollections.observableArrayList(list);
		//if (ownList.isEmpty()) {
			//ownListView.setPlaceholder(new Label("No creations to show"));
		//}else {
			ownListView.setItems(ownList);
		//}
	}
	
}
