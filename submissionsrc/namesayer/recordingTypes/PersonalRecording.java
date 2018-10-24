package namesayer.recordingTypes;

public class PersonalRecording extends Recording {
    /*
     * Constructor for the recording class. Takes in the full name of the file and the path
     * to the file.
     */
    public PersonalRecording(String name, String path) {
        String subString = name.substring(name.lastIndexOf("_") + 1);
        shortName = subString.replaceAll(".wav", "");
        fileName = name.replaceAll(" ", "");
        this.path = path;
    }
}
