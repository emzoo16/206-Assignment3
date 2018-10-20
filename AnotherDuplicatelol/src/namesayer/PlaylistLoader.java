package namesayer;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlaylistLoader {
    private File file;
    private Stage stage;

    public PlaylistLoader(Stage stage) {
        this.stage = stage;
    }

    /*
     * This method scans the file the user chooses. It parses each line into the individual
     * names and searches if they are present in the database. If they are, the names are
     * added to the list to be played.
     */
    public List<String> loadPlaylist(File file) {
        //An array of all full names with a name/names missing. Missing names bracketed. This will be
        //passed into the warning controller.
        List<String> notFoundDisplay = new ArrayList<>();

        //An array that will contain all the valid names from the file.
        List<String> namesList = new ArrayList<>();

        if (file.exists()) {
            try{
                Scanner scanner = new Scanner(file);

                //Iterate through each line in the text file.
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();

                    //List containing any names that were not found in the database.
                    List<List<String>> returnedList = checkName(line);
                    
                    //Lists containing names that were not found and successfully created names respectively.
                    List<String> notFound = returnedList.get(0);
                    List<String> formattedName = returnedList.get(1);

                    //If all the names are in the database and the name has, not already been added to the playlist,
                    //add the line (the composite name) to the namesList.
                    if(notFound.isEmpty() && !namesList.contains(formattedName.get(0))) {
                        namesList.add(formattedName.get(0));
                    }else{
                        //Format the string to surround the name not found in brackets. This is the format they will be
                        //displayed in the warning screen.
                        for (String s: notFound) {
                            line = line.replace(s, "(" + s + ")");
                        }
                        notFoundDisplay.add(line);
                    }
                }
                if (notFoundDisplay.isEmpty() == false) {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("uploadWarning.fxml"));
                        Parent warningSceneParent = fxmlLoader.load();
                        Scene warningScene = new Scene(warningSceneParent);

                        //Getting the instance of the warning controller and passing the
                        UploadWarningController controller = fxmlLoader.getController();
                        controller.setNotFoundList(notFoundDisplay);

                        Stage warningStage = new Stage();
                        warningStage.setScene(warningScene);

                        //Disable the background window when the warning stage is displayed.
                        warningStage.initModality(Modality.WINDOW_MODAL);
                        warningStage.initOwner(stage);

                        warningStage.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return namesList;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Given a string (a line containing a name), this method parses the string and checks if each name in the sting
     * is in the database.
     */
    public List<List<String>> checkName(String line) {

    	//Initilaises a 2d list.
    	
    	List<List<String>> returnList = new ArrayList<List<String>>();
    	
        //A list containing all the names in the current line that could not be found in the database.
        List<String> notFound = new ArrayList<>();
        
        //A list containing the proper formatted name.
        List <String> formattedNames = new ArrayList<>();
        
        //Add both lists to the return list.
        returnList.add(notFound);
        returnList.add(formattedNames);

        //String of the line that has the proper formatting for names in the database.
        String formattedLine;

        //Seperate the line by spaces, underscores or hyphens to create an array of single names.
        String[] splitLine = line.trim().split("[\\s+,_,-]");

        //Iterate through each single name in the line to check if the name is in the
        //database.
        for (int i=0; i<splitLine.length; i++ ) {

            //If the name is not in the database, add the name to the not found list.
            if (inDatabase(splitLine[i])=="") {
                notFound.add(splitLine[i]);
            }
            //Logic getting formatted names of found names.(First letter upper case and all hyphens replaced with spaces.
            else {
            	formattedLine = line.replace(splitLine[i], inDatabase(splitLine[i]));
            	formattedLine = formattedLine.replaceAll("[-,_]", " ");  
            	formattedNames.add(formattedLine);
            }           
        }
        return returnList;
    }

    /*
     * This method checks if the provided filename is a name in the database directory.
     */
    public String inDatabase(String inputName) {

        //List all the files in the database.

        DatabaseList referenceList = new DatabaseList();
        referenceList.displayAll();

        List<String> database = referenceList.getRecordingNames();
        inputName = inputName.substring(0,1).toUpperCase() + inputName.substring(1).toLowerCase();
        if (database.contains(inputName)) {
            return inputName;
        }
        return "";
    }
    
}