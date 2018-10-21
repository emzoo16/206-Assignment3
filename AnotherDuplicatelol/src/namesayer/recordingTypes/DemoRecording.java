package namesayer.recordingTypes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.HashMap;

public abstract class DemoRecording extends Recording {

    //A map of all personal recording attempts
    protected HashMap<String, PersonalRecording> userAttempts;

    /*
     * Returns all personal recordings
     */
    public ObservableList<String> getUserAttempts() {
        ObservableList<String> personalRecordingsList = FXCollections.observableArrayList(userAttempts.keySet());
        Collections.sort(personalRecordingsList);
        return personalRecordingsList;
    }

    /*
     * Returns an unused number to be used in naming a new personal recording.
     */
    public int getUnusedAttemptsNumber() {
        int[] usedNumbers = new int[userAttempts.size()];
        int index = 0;
        for (String attempt : userAttempts.keySet()) {
            Recording recording = userAttempts.get(attempt);
            usedNumbers[index] = recording.getNumber();
            index++;
        }
        java.util.Arrays.sort(usedNumbers);
        for (int i = 0; i < usedNumbers.length; i++) {
            if (usedNumbers[i] != i + 1) {
                return i + 1;
            }
        }
        return usedNumbers.length + 1;
    }

    /**
     *returns the requested user attempt
     */
    public PersonalRecording getUserRecording(String name) {
        return userAttempts.get(name);
    }

    /**
     *Adds a a user attempt
     */
    public void addAttempt(PersonalRecording attempt) {
        userAttempts.put(attempt.getShortName() ,attempt);
    }

    /**
     *removes a user attempt
     */
    public void deleteAttempt(String attempt) {
        userAttempts.remove(attempt);
    }
}
