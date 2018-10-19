package namesayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class DatabaseList {
	
	//A map of all recordings in the database
    private HashMap<String, DemoRecording> dataBaseRecordings;
	//A map of all recordings which are being used from the database
    private HashMap<String, DemoRecording> recordingsMap;
    //A list of all names of recordings which are being used from the database, with the first letter captialized.
    private List<String> recordingNames;

    /*
     * This class represents the list of database recordings.
     */
    public DatabaseList() {
        dataBaseRecordings = new HashMap<>();
        recordingsMap = new HashMap<>();
        recordingNames = new ArrayList<>();
        //Gets the files in an array
        File folder = new File("Database/");
        File[] ArrayOfFiles = folder.listFiles();
        int count = 1;
        if (ArrayOfFiles != null) {
            for (File file : ArrayOfFiles) {
                //Validates the file
                if (file.isFile() && !file.toString().startsWith("Database/.")) {
                    String fileString = file.getName();
                    //Removes extension and codes.
                    String trimFileString = fileString.substring(fileString.lastIndexOf("_")+1,fileString.indexOf("."));
                    String updatedFileString = trimFileString;
                    while (dataBaseRecordings.containsKey(updatedFileString)) {
                        updatedFileString = trimFileString + "(" + count + ")";
                        count += 1;
                    }
                    count = 1;
                    String processedRecordingName = updatedFileString.substring(0,1).toUpperCase()
                            + updatedFileString.substring(1).toLowerCase();
                    DatabaseRecording recording = new DatabaseRecording(fileString, processedRecordingName);
                    dataBaseRecordings.put(processedRecordingName, recording);
                }
            }
        } else {
            //Cant find the database/nothing in it
        }
    }

	//Makes the recording part of the map of used recordings
    public void add(String name) {
        recordingsMap.put(name, dataBaseRecordings.get(name));
    }
	
	//Used for adding concatenated recordings, because they aren't in the database
    public void add(DemoRecording recording){ recordingsMap.put(recording.getShortName(), recording); }

	//returns the recording
    public DemoRecording getRecording(String name) {
        return recordingsMap.get(name);
    }

    //Gets a list of all recordings currently being used
    public ObservableList<String> getRecordingNames() {
        ObservableList<String> recordingsList = FXCollections.observableArrayList(recordingsMap.keySet());
        Collections.sort(recordingsList);
        return recordingsList;
    }

	//removes a recording form the recordings being used
    public void remove(String name) {
        if (recordingsMap.keySet().contains(name)) {
            recordingsMap.remove(name);
        }
        String updatedName = name.substring(0,1).toUpperCase() + name.substring(1);
        if (recordingNames.contains(updatedName)) {
            recordingNames.remove(updatedName);
        }
    }
	
	//Makes it so all recordings in the database are being used
    public void displayAll() {
        recordingsMap = new HashMap<>(dataBaseRecordings);
    }
}
