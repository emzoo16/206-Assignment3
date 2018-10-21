package namesayer.recordingTypes;

import java.io.File;
import java.util.HashMap;

public class DatabaseRecording extends DemoRecording {
    /*
     * This class represents a single database recording and its corresponding personal
     * recordings.
     */
    public DatabaseRecording(String fullyQualifiedName, String shortName) {
        userAttempts = new HashMap<>();
        fileName = fullyQualifiedName;
        this.shortName = shortName;
        File folder = new File("PersonalRecordings/");
        File[] ArrayOfFiles = folder.listFiles();
        if (ArrayOfFiles != null) {
            for (File file : ArrayOfFiles) {
                //Validates the file
                if (file.isFile() && (file.getName()).contains(shortName + "-")) {
                    String recordingFileName = file.getName();
                    Character digit = recordingFileName.charAt(recordingFileName.lastIndexOf("-") + 1);
                    String recordingNumber = digit.toString();
                    String recordingName = (this.shortName + "-" + recordingNumber);
                    userAttempts.put(recordingName, new PersonalRecording(recordingFileName, "PersonalRecordings/"));
                }
            }
        } else {
            //Cant find any personal recordings
        }
        path = "Database/";
    }
}
