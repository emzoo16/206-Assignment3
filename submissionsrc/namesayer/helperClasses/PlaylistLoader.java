package namesayer.helperClasses;

import javafx.stage.Stage;
import namesayer.fxmlControllers.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlaylistLoader {
    private File file;
    private Stage stage;
    private UploadController controller;

    public PlaylistLoader() {
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
                    List<String> notFound = checkName(line);

                    //If all the names are in the database, add the line (the composite name) to the namesList.
                    if(notFound.isEmpty()) {
                        if (!namesList.contains(line.toLowerCase())) {
                            namesList.add(line.toLowerCase());
                        }
                    }else {
                        //Format the string to surround the name not found in brackets. This is the format they will be
                        //displayed in the warning screen.
                        for (String s: notFound) {
                            line = line.replace(s, "(" + s + ")");
                        }
                        notFoundDisplay.add(line);
                    }
                }
                if (notFoundDisplay.isEmpty() == false && WorkspaceModel.getInstance().getLoadingController() instanceof UploadController) {
                    // != to null is because only the upload screen will show the incorrect names (playlist screen isn't
                    // suppose to have incorrect names)
                    WorkspaceModel.getInstance().setNotFoundNames(notFoundDisplay);
                }

                return namesList;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Given a string (a name), this method parses the string and checks if each name in the sting
     * is in the database.
     */
    public List<String> checkName(String line) {

        //An array containing all the names in the current line that could not be found in the database.
        List<String> notFound = new ArrayList<>();

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
            //Logic getting proper names of found names.
            else {
                formattedLine = line.replace(splitLine[i], inDatabase(splitLine[i]));
            }
        }
        return notFound;
    }

    /*
     * This method checks if the provided filename is a name in the database directory.
     */
    public String inDatabase(String inputName) {

        //List all the files in the database.

        DatabaseList referenceList = new DatabaseList();
        referenceList.displayAll();

        List<String> database = referenceList.getRecordingNames();
        if (inputName.length() != 0 && inputName.length() != 1) {
            inputName = inputName.substring(0,1).toUpperCase() + inputName.substring(1).toLowerCase();
        }
        if (database.contains(inputName)) {
            return inputName;
        }
        return "";
    }
}