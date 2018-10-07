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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class RecordController implements Initializable {
	
	Boolean running;
	@FXML
	Button stop;
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
    Button demoButton;

    //Stores the current chosen volume.
    Double volume;


    Recording recording;
    DemoRecording databaseRecording;

    //Reference to the workspace controller
    WorkSpaceController parentController;

    /*
     * This method is invoked when the user chooses not to keep their recording and retry.
     * The UI is set up again for recording.
     */
    @FXML
    private void redoRecording() {
        setComponentsForRecording();
    }

    /*
     * This method is invoked when the user chooses to record audio for a name.
     */
    @FXML
    private void record() {

        //Every time the record button is clicked, increment the recordClicked variable to
        //move the user closer to their reward.
        parentController.incrementIndicator();

        //Disable the record button once the user starts the recording.
        recordButton.setDisable(true);

        //Variable used to stop the recording externally.
        running = true;

        //Record the audio from a dataline.
        AudioFormat format =  new AudioFormat(8000, 8, 1, true, true);
        try {
            //Initializing and opening the dataline.
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);

            //New thread to write the input to a file.
            Task<Void> task = new Task<Void>() {

                @Override
                protected Void call() throws Exception {

                    //Handles writing of audio into file.
                    AudioInputStream inputStream = new AudioInputStream(line);

                    //New file audio will be saved in.
                    File audioFile = new File("./audio.wav");

                    //Starting the recording.
                    line.start();

                    //Write the input from the microphone to a file named 'audioFile'.
                    while(running) {
                        AudioSystem.write(inputStream,AudioFileFormat.Type.WAVE, audioFile);
                    }
                    return null;
                }

            };

            //Start the thread.
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();


        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /*
     * This method is invoked when the user wants to stop their recording.
     */
    @FXML
    public void stopButtonClicked() {
        setComponentsForProcessingRecording();

        //Stop the recording by stopping the while loop writes the audio information to the audiofile
        running = false;
    }

    /*
     * This method is invoked when the user wants to hear back the recording they have just created.
     */
    @FXML
    private void playRecording() {
        try {
            //Gets the URL for the temporary recording.
            URL url = Paths.get("audio.wav").toUri().toURL();

            AudioInputStream stream = AudioSystem.getAudioInputStream(url);
            DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());

            //create a new clip to play the file on the dataline.
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /*
     * Plays the database recording of the current name.
     */
    @FXML
    private void playDemo() {
        //databaseRecording.play(volume);
    }

    /*
     * Method is called when the user chooses to keep their recording. The new recording is saved
     * and added to the personal recording listView.
     */
    @FXML
    private void keepRecording() {
        String databaseRecordingName = databaseRecording.getFileName();
        int recordingNumber = databaseRecording.getUnusedAttemptsNumber();
        String recordingFileName = databaseRecordingName.substring(0, databaseRecordingName.lastIndexOf("."));
        String version = "";
        String shortName = databaseRecording.getShortName();
        if (shortName.contains("(")) {
            version = shortName.substring(shortName.indexOf("("), shortName.lastIndexOf(")") + 1);
        }
        if (shortName.contains(" ")) {
            recording = new Recording(recordingFileName + version + "-" + (recordingNumber) + ".wav", "ConcatenatedPersonalRecordings/");
            databaseRecording.addAttempt(recording);
            File originalFile = new File("audio.wav");
            File newFile = new File("ConcatenatedPersonalRecordings/" + recording.getFileName());
            originalFile.renameTo(newFile);
        } else {
            recording = new Recording(recordingFileName + version + "-" + (recordingNumber) + ".wav", "PersonalRecordings/");
            databaseRecording.addAttempt(recording);
            File originalFile = new File("audio.wav");
            File newFile = new File("PersonalRecordings/" + recording.getFileName());
            originalFile.renameTo(newFile);

        }
        parentController.refreshPersonalRecordings(databaseRecording.getShortName());
        Stage currentStage = (Stage) returnButton.getScene().getWindow();
        currentStage.close();
    }

    /*
     * Takes the user back to the workspace.
     */
    @FXML
    private void returnToWorkspace() {
        //Close the recording window
        Stage currentStage = (Stage) returnButton.getScene().getWindow();
        currentStage.close();
    }

    /*
     * Sets the UI layout for recording.
     */
    private void setComponentsForRecording() {
        instructionLabel.setText("Press to begin recording for five seconds");
        recordButton.setVisible(true);
        recordButton.setDisable(false);
        playButton.setVisible(false);
        redoButton.setVisible(false);
        keepButton.setVisible(false);
        demoButton.setVisible(false);
        progressIndicator.setProgress(0);
    }

    /*
     * Sets the UI for asking user if they want ot keep their recording.
     */
    private void setComponentsForProcessingRecording() {
        instructionLabel.setText("What do you want to do with this recording");
        recordButton.setVisible(false);
        playButton.setVisible(true);
        redoButton.setVisible(true);
        keepButton.setVisible(true);
        demoButton.setVisible(true);
    }

    /*
     * Passes information between recordController and WorkSpaceController
     */
    public void passInformation(DemoRecording databaseRecording, WorkSpaceController controller) {
        this.databaseRecording = databaseRecording;
        parentController = controller;
    }
 
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setComponentsForRecording();
    }
}
