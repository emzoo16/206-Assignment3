package namesayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileUploadController implements Initializable{
	@FXML
	Button uploadButton;
	@FXML
	Button createButton;
	@FXML
	Button cancelButton;
	@FXML
	TextField playlistName;	
	
	File currentFile;
	String[] namesNotFound;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		uploadButton.setVisible(false);
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
			namesNotFound = scanFile();
			
		}else {
			
		}
		
	}
	@FXML
	public void createButtonClicked() {
		String newName = playlistName.getText();
		if (newName != null) {
			File nameFile = new File("Database/" + newName + "/");
			if (nameFile.exists()) {
				//Alert
			}else {
				nameFile.mkdirs();
				uploadButton.setVisible(true);
			}
		}
		
	}
	@FXML
	public void cancelButtonClicked() {
		 //Close the upload window
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.close();
	}
	
	/*
	 * This method scans the file the user chooses. It parses each line into the individual 
	 * names and searches if they are present in the database. If they are, the names are 
	 * added to the list to be played.
	 */
	public String[] scanFile() {
		
		//Two arrays of strings to store the names of the names that have been found and 
		//have not been found in the database respectively
		String[] notFound = {""};
		String[] foundFileNames = {""};
		
		//Indexers to keep track of current position in the arrays.
		int notFoundIndex=0;
		int foundIndex=0;
		
		//
		if (currentFile.exists()) {
			try {
				Scanner scanner = new Scanner(currentFile);
				
				//Iterate through each line in the text file.
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					
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
					if(notFound.length > 0) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("The following names were not found: ");
						alert.setContentText("Careful with the next step!");

						alert.showAndWait();
						
					}
					//If there are no names in the notFound list show a success alert to inform user of the successful
					//playlist load.
					else {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Success");
						alert.setHeaderText(null);
						alert.setContentText("Playlist successfully loaded");
						alert.showAndWait();
						//concatenate all the recordings
					}
		
				}
				return notFound;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			return notFound;
		}
		return notFound;
		
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
	/*
	 * This method checks whether the 
	 */
	public void notFoundCheck() {
		
	}
	
}
