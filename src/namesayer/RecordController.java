package namesayer;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import javafx.util.Duration;
import java.util.ResourceBundle;

public class RecordController implements Initializable {
    @FXML
    TextField nameTextField;
    @FXML
    Label instructionLabel;
    @FXML
    ProgressIndicator progressIndicator;
    @FXML
    Button recordButton;
    @FXML
    Button redoButton;
    @FXML
    Button playButton;
    @FXML
    Button keepButton;
    @FXML
    Button returnButton;
    @FXML
    Label nameLabel;

    Recording recording;
    DatabaseRecording databaseRecording;
    WorkSpaceController parentController;

    @FXML
    private void redoRecording() {
        setComponentsForRecording();
    }

    @FXML
    private void record() {
        String databaseRecordingName = databaseRecording.getShortName();
        int databaseRecordingAttempts = databaseRecording.getAttemptsNumber();
        recording = new Recording(databaseRecordingName + "-" + (databaseRecordingAttempts + 1));
        recordButton.setDisable(true);
        Task<Void> recordTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                //Record Bash process
                String recordCMD = "ffmpeg -y -f alsa -loglevel quiet -t 5 -i default audio.wav";
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", recordCMD);
                Process process = builder.start();
                process.waitFor();
                return null;
            }
            protected void succeeded() {
                    setComponentsForProcessingRecording();
                }
        };
        Thread thread = new Thread(recordTask);
        thread.setDaemon(true);
        startIndicator();
        thread.start();
    }

    private void startIndicator() {
        KeyFrame keyFrameStart = new KeyFrame(Duration.ZERO, new KeyValue(progressIndicator.progressProperty(), 0));
        KeyFrame keyFrameEnd = new KeyFrame(Duration.seconds(5), new KeyValue(progressIndicator.progressProperty(), 1));
        Timeline timeLine = new Timeline(keyFrameStart, keyFrameEnd);
        timeLine.play();
    }

    @FXML
    private void playRecording() {
        recording.play();
    }

    @FXML
    private void keepRecording() {
        parentController.ownList.add(recording.getShortName());
        String selectedRecording = parentController.dataListView.getSelectionModel().getSelectedItem();
        parentController.listOfRecordings.getRecording(selectedRecording).addAttempt(recording);
        File originalFile = new File("audio.wav");
        File newFile = new File("PersonalRecordings/" + recording.getShortName() + ".wav");
        originalFile.renameTo(newFile);
        Stage currentStage = (Stage) returnButton.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void returnToWorkspace() {
        //Close the recording window
        Stage currentStage = (Stage) returnButton.getScene().getWindow();
        currentStage.close();
    }

    private void setComponentsForRecording() {
        instructionLabel.setText("Press to begin recording for five seconds");
        recordButton.setVisible(true);
        recordButton.setDisable(false);
        playButton.setVisible(false);
        redoButton.setVisible(false);
        keepButton.setVisible(false);
        progressIndicator.setProgress(0);
    }

    private void setComponentsForProcessingRecording() {
        instructionLabel.setText("What do you want to do with this recording");
        recordButton.setVisible(false);
        playButton.setVisible(true);
        redoButton.setVisible(true);
        keepButton.setVisible(true);
    }

    public void passInformation(DatabaseRecording databaseRecording, WorkSpaceController controller) {
        this.databaseRecording = databaseRecording;
        parentController = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setComponentsForRecording();
    }
}
