package namesayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;

public class DatabaseList {
    private HashMap<String, DatabaseRecording> dataBaseRecordings;
    private HashMap<String, DatabaseRecording> recordingsMap;

    public DatabaseList() {
        dataBaseRecordings = new HashMap<>();
        recordingsMap = new HashMap<>();
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
                    DatabaseRecording recording = new DatabaseRecording(fileString, updatedFileString);
                    dataBaseRecordings.put(updatedFileString, recording);
                }
            }
        } else {
            //Cant find the database/nothing in it
        }
    }

    public void add(String name) {
        recordingsMap.put(name, dataBaseRecordings.get(name));
    }

    public DatabaseRecording getRecording(String name) {
        return dataBaseRecordings.get(name);
    }

    public ObservableList<String> getRecordingNames() {
        ObservableList<String> recordingsList = FXCollections.observableArrayList(recordingsMap.keySet());
        Collections.sort(recordingsList);
        return recordingsList;
    }

    public void remove(String name) {
        if (recordingsMap.keySet().contains(name)) {
            recordingsMap.remove(name);
        }
    }

    public void displayAll() {
        recordingsMap = new HashMap<>(dataBaseRecordings);
    }
}
