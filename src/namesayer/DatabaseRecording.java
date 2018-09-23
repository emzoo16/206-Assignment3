package namesayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DatabaseRecording extends Recording {
    private HashMap<String, Recording> userAttempts;

    public DatabaseRecording(String fullyQualifiedName, String shortName) {
        userAttempts = new HashMap<>();
        fileName = fullyQualifiedName;
        this.shortName = shortName;
        File folder = new File("PersonalRecordings/");
        File[] ArrayOfFiles = folder.listFiles();
        if (ArrayOfFiles != null) {
            for (File file : ArrayOfFiles) {
                //Validates the file
                if (file.isFile() && file.getName().contains(shortName)) {
                    String recordingName = (this.shortName + "-" + (userAttempts.size() + 1));
                    userAttempts.put(recordingName, new Recording(recordingName));
                }
            }
        } else {
            //Cant find the database/nothing in it
        }
        path = "Database/";
    }

    public ObservableList<String> getUserAttempts() {
        ObservableList<String> personalRecordingsList = FXCollections.observableArrayList(userAttempts.keySet());
        Collections.sort(personalRecordingsList);
        return personalRecordingsList;
    }

    public int getAttemptsNumber() {
        return userAttempts.size();
    }

    public Recording getUserRecording(String name) {
        return userAttempts.get(name);
    }

    public void addAttempt(Recording attempt) {
        userAttempts.put(attempt.getShortName() ,attempt);
    }

    public void deleteAttempt(String attempt) {
        userAttempts.remove(attempt);
    }
}
