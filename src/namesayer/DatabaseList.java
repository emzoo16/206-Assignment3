package namesayer;

import java.io.File;
import java.util.HashMap;

public class DatabaseList extends RecordingList {
    private HashMap<String, Recording> databaseRecordings;

    public DatabaseList() {
        super();
        databaseRecordings = new HashMap<>();
        //Gets the files in an array
        File folder = new File("Database/");
        File[] ArrayOfFiles = folder.listFiles();
        int count = 1;
        if (ArrayOfFiles != null) {
            for (File file : ArrayOfFiles) {
                //Validates the file
                if (file.isFile() && !file.toString().startsWith("Database/.")) {
                    String fileString = file.getName();
                    Recording recording = new Recording(fileString);
                    //Removes extension and codes.
                    fileString = fileString.substring(fileString.lastIndexOf("_") + 1, fileString.indexOf("."));
                    String updatedFileString = fileString;
                    while (databaseRecordings.containsKey(updatedFileString)) {
                        updatedFileString = fileString + "(" + count + ")";
                        count += 1;
                    }
                    count = 1;
                    databaseRecordings.put(updatedFileString, recording);
                }
            }
        } else {
            //Cant find the database/nothing in it
        }
        recordingsMap = new HashMap<>(databaseRecordings);
    }

    public void add(String name) {
        recordingsMap.put(name, databaseRecordings.get(name));
    }
}
