package namesayer.fxmlControllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import namesayer.recordingTypes.PersonalRecording;
import namesayer.interfaces.PlayController;
import namesayer.recordingTypes.DemoRecording;

public class LoopSceneController implements Initializable, PlayController {

    @FXML
    ListView personalList;
    @FXML
    ComboBox loopCombo;
    @FXML
    Button startButton;
    @FXML
    Button cancelButton;
    @FXML
    Label recordingLabel;

    private DemoRecording recording;

    private PersonalRecording personalRecording;

    private int toPlay;

    private int numberPlayed;

    private double volume;

    private boolean stopped;

    private boolean hasPlayedOnce;

    @FXML
    private void playLoop() {
        if (startButton.getText().equals("Start")) {
            stopped = false;
            numberPlayed = 0;
            if (loopCombo.getSelectionModel().getSelectedItem() != null) {
                toPlay = (int)loopCombo.getSelectionModel().getSelectedItem();
                //Because it plays 2 recordings every loop
                toPlay = toPlay*2;
                personalRecording = recording.getUserRecording((String)personalList.getSelectionModel().getSelectedItem());
                hasPlayedOnce = true;
                recording.setController(this);
                personalRecording.setController(this);
                startButton.setText("Stop");
                recording.play(volume);
            }
        } else {
            stopped = true;
            if (recording.isPlaying()) {
                recording.stopPlaying();
            } else if (personalRecording.isPlaying()) {
                personalRecording.stopPlaying();
            }
        }
    }

    @FXML
    private void returnToWorkspace() {
        if (hasPlayedOnce) {
            stopped = true;
            recording.stopPlaying();
            personalRecording.stopPlaying();
        }
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.close();
    }

    public void passRecording(DemoRecording passedRecording, double volume) {
        this.volume = volume;
        this.recording = passedRecording;
        recordingLabel.setText(this.recording.getShortName());
        personalList.setItems(FXCollections.observableArrayList(recording.getUserAttempts()));
        personalList.getSelectionModel().select(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Integer> options = FXCollections.observableArrayList(1, 2, 3, 4, 5);
        loopCombo.setItems(options);
        hasPlayedOnce = false;
    }

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
                startButton.setText("Start");
            }
        } else {
            startButton.setText("Start");
        }
    }

}