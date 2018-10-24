package namesayer.fxmlControllers;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import namesayer.helperClasses.WorkspaceModel;
import namesayer.recordingTypes.PersonalRecording;
import namesayer.interfaces.PlayController;
import namesayer.recordingTypes.DemoRecording;

public class LoopSceneController implements Initializable, PlayController {

    /**
     * FXML variables.
     */
    @FXML
    private ListView<String> personalList;
    @FXML
    private ComboBox<Integer> loopCombo;
    @FXML
    private Button startButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label recordingLabel;

    //
    private DemoRecording recording;
    private PersonalRecording personalRecording;

    //
    private int toPlay;

    private int numberPlayed;

    //The double representing the volume that
    private double volume;

    private boolean stopped;

    private boolean hasPlayedOnce;

    /**
     * Upon initialization,
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Integer> options = FXCollections.observableArrayList(1, 2, 3, 4, 5);
        loopCombo.setItems(options);
        hasPlayedOnce = false;
        WorkspaceModel model = WorkspaceModel.getInstance();
        this.volume = model.getVolume();
        this.recording = model.getCurrentDemoRecording();
        recordingLabel.setText(this.recording.getShortName());
        personalList.setItems(FXCollections.observableArrayList(recording.getUserAttempts()));
        personalList.getSelectionModel().select(0);
    }

    /**
     * This method is invoked when the user clicks the button to start the playback of the loop.
     */
    @FXML
    private void playLoop() {

        //If the text on the button is 'Start', start the playback of the loop. The toPlay variable stores the number
        //of loops the user wants to play. The numberPlayed variable counts the number of recordings that have been played.
        //Once these two variables become equal, stop playback.

        if (startButton.getText().equals("Start")) {
            stopped = false;
            numberPlayed = 0;
            if (loopCombo.getSelectionModel().getSelectedItem() != null) {

                //Check how many times the user wants to loop through the personal and database names by checking their
                //input in the combobox.
                toPlay = (int)loopCombo.getSelectionModel().getSelectedItem();
                //Double this number as every loop will play two recordings.
                toPlay = toPlay*2;
                personalRecording = recording.getUserRecording((String)personalList.getSelectionModel().getSelectedItem());

                //checks if the recording has started playing.
                hasPlayedOnce = true;
                recording.setController(this);
                personalRecording.setController(this);
                startButton.setText("Stop");
                recording.play(volume);
            }
        }
        //If the button has 'Stop' as the text, check if the current recording being played is a personal recording or a database
        //recording. Stop playing the relevant recording.
        else {
            stopped = true;
            if (recording.isPlaying()) {
                recording.stopPlaying();
            } else if (personalRecording.isPlaying()) {
                personalRecording.stopPlaying();
            }
        }
    }

    /**
     * This method is invoked when the user clicks the cancel button to return to the workspace. It stops the currently playing
     * recording and closes the stage.
     */
    @FXML
    private void returnToWorkspace() {
        //If the looping playback has started, stop playback.
        if (hasPlayedOnce) {
            stopped = true;
            recording.stopPlaying();
            personalRecording.stopPlaying();
        }
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.close();
    }

    /**
     * Handles the playback of the loop. Firstly, it checks if the number of recordings that has been played is less than the
     * total number of recordings the user wanted to play. As looping starts on the database recording, any even numbers will
     * mean the database recordings are played and any odd means personal recordings are played.
     */
    public void playingFinished() {
        if (!stopped) {
            numberPlayed++;
            if (numberPlayed < toPlay) {
                if (numberPlayed % 2 == 0) {
                    recording.play(volume);
                } else {
                    personalRecording.play(volume);
                }
            } else {
                //If the number of recordings played equals the number the user was to play, set the button back to start.
                startButton.setText("Start");
            }
        } else {
            //If the looping has already finished, set the text of the button back to start so it can be played again.
            startButton.setText("Start");
        }
    }

}