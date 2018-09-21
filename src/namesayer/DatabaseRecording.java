package namesayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DatabaseRecording extends Recording {
    private String shortName;
    private HashMap<String, Recording> userAttempts;

    public DatabaseRecording(String fullyQualifiedName, String shortName) {
        fileName = fullyQualifiedName;
        this.shortName = shortName;
        File folder = new File("PersonalRecordings/" + shortName + "/");
        File[] ArrayOfFiles = folder.listFiles();
        if (ArrayOfFiles != null) {
            for (File file : ArrayOfFiles) {
                //Validates the file
                if (file.isFile()) {
                    userAttempts.put(file.getName(), new Recording(this.shortName + (userAttempts.size() + 1)));
                }
            }
        } else {
            //Cant find the database/nothing in it
        }
        path = "Database/";
    }

    public List<String> getUserAttempts() {
        ObservableList<String> personalRecordingsList = FXCollections.observableArrayList(userAttempts.keySet());
        Collections.sort(personalRecordingsList);
        return personalRecordingsList;
    }

    public Recording getUserRecording(String name) {
        return userAttempts.get(name);
    }

}
