package namesayer;

import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;

public interface DemoRecording {

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
