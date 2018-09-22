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

    @FXML
    private void redoRecording() {
        setComponentsForRecording();
    }

    @FXML
    private void record() {
        if (!nameTextField.getText().isEmpty()) {
            recording = new Recording(nameTextField.getText());
            recordButton.setDisable(true);
            nameTextField.setVisible(false);
            nameLabel.setVisible(false);
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
        playButton.setVisible(false);
        redoButton.setVisible(false);
        keepButton.setVisible(false);
        nameTextField.setVisible(true);
        nameLabel.setVisible(true);
        progressIndicator.setProgress(0);
    }

    private void setComponentsForProcessingRecording() {
        instructionLabel.setText("What do you want to do with this recording: " + nameTextField.getText());
        recordButton.setVisible(false);
        playButton.setVisible(true);
        redoButton.setVisible(true);
        keepButton.setVisible(true);
        nameTextField.setVisible(false);
        nameLabel.setVisible(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setComponentsForRecording();
    }
}
