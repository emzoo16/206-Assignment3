package namesayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PlaylistController implements Initializable {
	
	/*
	 * FXML variables 
	 */
	@FXML
	Button playButton;
	@FXML
	Button nextButton;
	@FXML
	Button previousButton;
	@FXML
	Button uploadButton;
	@FXML
	Button backButton;
	@FXML
	ProgressBar progressbar;	
	@FXML
	Button continueButton;
	@FXML 
	ListView<String> nameListView;
	ObservableList<String> nameList = FXCollections.observableArrayList();
	
	/*
	 * Keeping track of the number in the listViews the user has currently selected
	 * in the playListView and nameListView respectively.
	 */
	int playlistIndex;
	int nameIndex;
	
	File currentFile;
	String[] namesNotFound;

	DatabaseList workspaceRecordings;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Listener to get the current name the user is clicking on in the playListView.
				
		workspaceRecordings = new DatabaseList();
	}

	@FXML
	public void backButtonClicked() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("startMenu.fxml"));
			Parent playlistScene = fxmlLoader.load();
			Stage stage = (Stage) backButton.getScene().getWindow();
			stage.setScene(new Scene(playlistScene, 700, 500));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void continueButtonClicked() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("workspace.fxml"));
			Parent playlistScene = fxmlLoader.load();
			WorkSpaceController controller = fxmlLoader.getController();
			controller.setWorkspaceRecordingsAndController(workspaceRecordings, workspaceRecordings.getRecordingNames());
			Stage stage = (Stage) continueButton.getScene().getWindow();
			stage.setScene(new Scene(playlistScene, 700, 500));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void uploadButtonClicked() {
		//Creating a new FileChooser and creating a filter so it only accepts .txt files.
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
				fileChooser.getExtensionFilters().add(extFilter);
				
				File selectedFile = fileChooser.showOpenDialog(null);
				
				if (selectedFile != null) {
					currentFile = selectedFile;
					scanFile();
				}else {
					
				}

	}
	
	/*
	 * This method scans the file the user chooses. It parses each line into the individual 
	 * names and searches if they are present in the database. If they are, the names are 
	 * added to the list to be played.
	 */
	public void scanFile() {
		
		//Indexers to keep track of current position in the arrays.
		int notFoundIndex=0;
		int foundIndex=0;
		List<String> namesList = new ArrayList<>();
		//
		if (currentFile.exists()) {
			try {
				Scanner scanner = new Scanner(currentFile);
				
				//Iterate through each line in the text file.
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					
					//Two arrays of strings to store the names of the names that have been found and 
					//have not been found in the database respectively
					String[] foundFileNames = {""};
					String[] notFound = {""};

					//Seperate the line by spaces or underscores to create an array of single names.
					String[] splitLine = line.trim().split("[\\s+,_]");
					
					//Iterate through each single name in the line to check if the name is in the
					//database.
					for (int i=0; i<splitLine.length; i++ ) {
						
						//If the name is not in the database, add the name to the not found list.
						if (inDatabase(splitLine[i]) == "") {
							notFound[notFoundIndex] = splitLine[i];
							
						//If the name is in the database, add its fully qualified name to the found names
						//array
						}else {
							foundFileNames[foundIndex] = inDatabase(splitLine[i]);
						}
					}
					
					//If there are names in the notFound list, show a warning to the user to ask them if they want to concatenate
					//the found names anyway or cancel.
					if(notFound.length > 0 && notFound[0] != "") {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("The following names were not found: ");
						
						String errorText = "";
						for(int i=0; i<notFound.length; i++) {
							errorText=errorText+ notFound[i];
						}
						alert.setContentText(errorText + "\n" + "The name "+ line + " was not created." );
						alert.showAndWait();
					}else {
						namesList.add(line);
					}
				}

				//A list to get the file names of all recordings to create a concatenated recording
				DatabaseList referenceList = new DatabaseList();
				referenceList.displayAll();

				//Loops through all names in the list adding them to the workspace
				for (String name : namesList) {
					//If the name is a concatenated one
					if (name.trim().contains(" ")) {
						//Gets the list of the names
						List<String> names = Arrays.asList(name.split(" "));
						List<String> fileNames = new ArrayList<>();
						//Gets the file names for all these names
						for (String recording : names) {
							fileNames.add(referenceList.getRecording(recording).getFileName());
						}
						//Creates the concatenated recording and adds it to the workspace
						DemoRecording concatenatedRecording = new ConcatenatedRecording(fileNames, name);
						workspaceRecordings.add(concatenatedRecording);
					} else {
						//If the name is a single name, it simply gets added
						workspaceRecordings.add(name);
					}
				}
				nameList = FXCollections.observableArrayList(namesList);
				nameListView.setItems(nameList);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} 
	}
	
	/*
	 * This method checks if the provided filename is a name in the database directory.
	 * Returns a string of the full name of the file if it exists. Otherwise return an empty string.
	 */
	public String inDatabase(String fileName) {
		File database = new File("Database/");
		File[] databaseFiles = database.listFiles();
		String fullFileName = "";
		
		for (int i=0; i< databaseFiles.length; i++) {
			
			//Parsing the actual name from the long file name.
			String name = databaseFiles[i].getName();
			String shortName = name.substring(name.lastIndexOf("_") + 1).replaceAll(".wav", "");
			
			//Checks if the given file name and the current file in the database directory
			//match.
			if (shortName.equals(fileName)) {
				fullFileName = name;
				return fullFileName;
			}
		}		
		return fullFileName;
		
	}
	
	
	

}
