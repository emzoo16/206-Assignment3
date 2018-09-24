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
                    String recordingFileName = file.getName();
                    String recordingNumber = recordingFileName.substring(recordingFileName.length() - 1);
                    String recordingName = (this.shortName + "-" + recordingNumber);
                    userAttempts.put(recordingName, new Recording(recordingFileName));
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

    public int getUnusedAttemptsNumber() {
        int[] usedNumbers = new int[userAttempts.size()];
        int index = 0;
        for (String attempt : userAttempts.keySet()) {
            Recording recording = userAttempts.get(attempt);
            usedNumbers[index] = recording.getNumber();
        }
        java.util.Arrays.sort(usedNumbers);
        for (int i = 0; i < usedNumbers.length - 1; i++) {
            if (usedNumbers[i] != i + 1) {
                System.out.println("Reaches here");
                return i + 1;
            }
        }
        return usedNumbers.length + 1;
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

    public String getFileName() {
        return fileName;
    }
}
