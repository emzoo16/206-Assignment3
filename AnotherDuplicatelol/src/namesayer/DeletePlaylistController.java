package namesayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DeletePlaylistController implements Initializable{
	
	@FXML
	Button replaceButton;
	
	@FXML
	Button cancelButton;
	
	@FXML
	ListView<String> playlistView;
	
	DatabaseList listOfRecordings;
	String inputName;
	Button workspaceButton;
	List<String> playlistNames = new ArrayList<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listPlaylists();
		
	}
	
	@FXML
	public void replaceButtonClicked(ActionEvent event) {
		String nameToDelete = playlistView.getSelectionModel().getSelectedItem();
		deletePlaylist(nameToDelete + ".txt");
		workspaceButton.setDisable(true);
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("saveScreen.fxml"));
			Parent rateSceneParent = fxmlLoader.load();
			SaveScreenController controller = fxmlLoader.getController();
			controller.setRecordingList(listOfRecordings);
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
		
	}
	
	@FXML
	public void cancelButton(ActionEvent event) {
		Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		currentStage.close();
	}
	
	public void setRecordingList(DatabaseList list) {
		this.listOfRecordings = list;
		
	}
	
	public void setInputName(String name) {
		this.inputName = name;
	}
	public void setButton(Button button) {
		this.workspaceButton = button;
	}
	
	public void createPlaylist(String name) {
		File file = new File("./Playlists/" + name + ".txt");
		try {
			PrintWriter writer = new PrintWriter(file);
			for (String currentName : listOfRecordings.getRecordingNames()) {
				writer.println(currentName);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void deletePlaylist(String name) {
		File playlistFiles = new File("Playlists/");

			for (File fileEntry : playlistFiles.listFiles()) {
				String currentFile = fileEntry.getName();
				if (currentFile.equals(name)) {
					fileEntry.delete();
				}
			}
		}
	
	public void listPlaylists() {
		File playlistFiles = new File("Playlists/");

		for (final File fileEntry : playlistFiles.listFiles()) {
			String currentFile = fileEntry.getName();
			if (currentFile.endsWith(".txt")) {
				String formattedName = currentFile.replaceAll(".txt","");
				playlistNames.add(formattedName);
		}
		ObservableList<String> playList = FXCollections.observableArrayList(playlistNames);
		playlistView.setItems(playList);
	}
	}
}

	


