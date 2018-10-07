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
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PlaylistController implements Initializable {
	
	/*
	 * FXML variables 
	 */
	@FXML
	Button uploadButton;
	@FXML
	Button browseButton;
	@FXML
	TextField fileText;
	@FXML
	Button backButton;
	@FXML
	ProgressBar progressbar;	
	@FXML
	Button continueButton;
	@FXML 
	ListView<String> nameListView;
	ObservableList<String> nameList = FXCollections.observableArrayList();
	
	
	 //Keeping track of the number in the listViews the user has currently selected
	 //in the playListView and nameListView respectively. 
	int playlistIndex;
	int nameIndex;
	
	//Stores a reference to the file the user chooses from the file chooser.
	File currentFile;

	//List of recording objects to be passed to the workspaceController.
	DatabaseList workspaceRecordings;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Listener to get the current name the user is clicking on in the playListView.
				
		workspaceRecordings = new DatabaseList();
	}

	/*
	 * This method is invoked when the user clicks the back button. Takes the user back to the start
	 * menu.
	 */
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

	/*
	 * This button is invoked when the user is ready to continue to the workspace, where they will play the 
	 * selected recordings.
	 */
	@FXML
	public void continueButtonClicked() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("workspace.fxml"));
			Parent playlistScene = fxmlLoader.load();
			WorkSpaceController controller = fxmlLoader.getController();
			
			//Passing the selected recordings to the workspace so they can be shown in the listviews in the
			//workspace.
			controller.setWorkspaceRecordingsAndController(workspaceRecordings, workspaceRecordings.getRecordingNames());
			
			Stage stage = (Stage) continueButton.getScene().getWindow();
			stage.setScene(new Scene(playlistScene, 700, 500));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method brings up a file chooser where the user is prompted to choose a .txt file to upload.
	 */
	@FXML
	public void browseButtonClicked() {
		//Creating a new FileChooser with a filter so it only accepts .txt files.
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		
		//Get a reference to the selected file.
		File selectedFile = fileChooser.showOpenDialog(null);
		
		//If the user successfully chooses a file, scan the file.
		if (selectedFile != null) {
			currentFile = selectedFile;
			fileText.setText(currentFile.getName());
		}

	}
	
	/*
	 * This method is invoked when the user chooses to upload the chosen file from the fileChooser.
	 */
	@FXML
	public void uploadButtonClicked() {
		
		//If no file is chose, show a warning alert.
		if(currentFile == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("Please choose a file" );
			alert.showAndWait();
			
		}else {
			scanFile();
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
		
		//An array that will contain all the valid names from the file.
		List<String> namesList = new ArrayList<>();

		if (currentFile.exists()) {
			try {
				Scanner scanner = new Scanner(currentFile);
				
				//Iterate through each line in the text file.
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					
					//An array containing all the names in the current line that could not be found in the database.
					String[] notFound = {""};

					//Seperate the line by spaces or underscores to create an array of single names.
					String[] splitLine = line.trim().split("[\\s+,_]");
					
					//Iterate through each single name in the line to check if the name is in the
					//database.
					for (int i=0; i<splitLine.length; i++ ) {
						
						//If the name is not in the database, add the name to the not found list.
						if (!inDatabase(splitLine[i])) {
							notFound[notFoundIndex] = splitLine[i];
							
						//If the name is in the database, add its fully qualified name to the found names
						//array
						}
					}
					
					//If there are names in the notFound list, show a warning to the user to ask them if they want to concatenate
					//the found names anyway or cancel.
					if(notFound[0] != "") {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("The following names were not found: ");
						
						//Creates an error message telling reader which one of the names was not found.
						String errorText = "";
						for(int i=0; i<notFound.length; i++) {
							errorText=errorText+ notFound[i];
						}
						
						alert.setContentText(errorText + "\n" + "The name "+ line + " was not created." );
						alert.showAndWait();
					}else {
						//If all the names are in the database, add the line (the composite name) to the namesList
						//Array.
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
	public boolean inDatabase(String fileName) {
		
		//List all the files in the database.
		File database = new File("Database/");
		File[] databaseFiles = database.listFiles();
		
		//Iterate through the database to check if the current filename matches a name in the
		//database.
		for (int i=0; i< databaseFiles.length; i++) {
			
			//Parsing the actual name from the long file name.
			String name = databaseFiles[i].getName();
			String shortName = name.substring(name.lastIndexOf("_") + 1).replaceAll(".wav", "");
			
			//Checks if the given file name and the current file in the database directory
			//match. If they are, return true.
			if (shortName.equals(fileName)) {
				return true;
			}
		}		
		return false;
		
	}
	
	
	

}
