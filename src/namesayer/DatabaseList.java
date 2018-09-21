package namesayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DatabaseList {
    private HashMap<String, Recording> recordingsMap;
    private HashMap<String, Recording> unselectedRecordingsMap;

    public DatabaseList() {
        recordingsMap = new HashMap<>();
        //Gets the files in an array
        File folder = new File("Database/");
        File[] ArrayOfFiles = folder.listFiles();
        int count = 1;
        if (ArrayOfFiles.length != 0) {
            for (File file : ArrayOfFiles) {
                //Validates the file
                if (file.isFile() && !file.toString().startsWith("Database/.")) {
                    String fileString = file.getName();
                    Recording recording = new Recording(fileString);
                    //Removes extension and codes.
                    fileString = fileString.substring(fileString.lastIndexOf("_") + 1, fileString.indexOf("."));
                    String updatedFileString = fileString;
                    while (recordingsMap.containsKey(updatedFileString)) {
                        updatedFileString = fileString + "(" + count + ")";
                        count += 1;
                    }
                    count = 1;
                    recordingsMap.put(updatedFileString, recording);
                }
            }
        }
        unselectedRecordingsMap = new HashMap<>(recordingsMap);
    }

    public ObservableList<String> getRecordingNames() {
        ObservableList<String> recordingsList = FXCollections.observableArrayList(unselectedRecordingsMap.keySet());
        Collections.sort(recordingsList);
        return recordingsList;
    }

    public Recording getRecording(String name) {
        return unselectedRecordingsMap.get(name);
    }

    public void remove(String name) {
        if (unselectedRecordingsMap.keySet().contains(name)) {
            unselectedRecordingsMap.remove(name);
        }
    }

    public void add(String name) {
        if (!unselectedRecordingsMap.keySet().contains(name)) {
            unselectedRecordingsMap.put(name, recordingsMap.get(name));
        }
    }
}
