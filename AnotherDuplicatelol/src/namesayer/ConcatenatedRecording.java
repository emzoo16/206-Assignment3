package namesayer;

import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;

import java.util.HashMap;

/**
 * This is just a few notes about this class and how I think I can get it to work with everything.
 * It needs all the functionality of a database recording so:
 * fileName: The name of the file created when concatenating
 * shortName: the full name etc George Edwin Mcerlean
 * For this part maybe create a new constructor in recording for personal recordings of concatenations.
 * userAttempts file names: fileName with a number
 * Als
 */
public class ConcatenatedRecording extends Recording implements DemoRecording {
    private HashMap<String, Recording> userAttempts;

    public ConcatenatedRecording(String fileName, String displayName) {
        this.fileName = fileName;
        this.shortName = displayName;

        //Need to implement getting all past attempts.
    }

    public ObservableList<String> getUserAttempts();

    public int getUnusedAttemptsNumber();

    public Recording getUserRecording(String attempt);

    public void addAttempt(Recording attempt);

    public void deleteAttempt(String attempt);

    public String getFileName();

    public void play(double volume, ProgressBar bar);

    public void setVolume(double volume);

    public String getShortName();
}
