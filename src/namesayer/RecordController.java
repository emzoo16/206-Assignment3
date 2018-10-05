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
	
	Double volume;
	
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

    Recording recording;
    DatabaseRecording databaseRecording;
    WorkSpaceController parentController;

    @FXML
    private void redoRecording() {
        setComponentsForRecording();
    }

    @FXML
    private void record() {
    	recordButton.setDisable(true);
    	running = true;
    	AudioFormat format =  new AudioFormat(8000, 8, 1, true, true);
 		
        try {
        	DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			
			Task<Void> task = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					AudioInputStream inputStream = new AudioInputStream(line);
					
					File audioFile = new File("./audio.wav");
					
					line.start();
					
					while(running) {
						
						
						AudioSystem.write(inputStream,AudioFileFormat.Type.WAVE, audioFile);
						
					}
					return null;
				}
		
			};
			Thread thread = new Thread(task);
			thread.setDaemon(true);
			thread.start();
			
			
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
    	
   
    	
//        recordButton.setDisable(true);
//        Task<Void> recordTask = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//          	
////Record Bash process
//String recordCMD = "ffmpeg -y -f alsa -loglevel quiet -t 5 -i default audio.wav";
//ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", recordCMD);
//Process process = builder.start();
// process.waitFor();
//              return null;
//            }
//            protected void succeeded() {
//                    setComponentsForProcessingRecording();
//                }
//        };
//        Thread thread = new Thread(recordTask);
//        thread.setDaemon(true);
//        startIndicator();
//        thread.start();
    }
    
    @FXML
    public void stopButtonClicked() {
    	setComponentsForProcessingRecording();
    	running = false;
    }

    private void startIndicator() {
        KeyFrame keyFrameStart = new KeyFrame(Duration.ZERO, new KeyValue(progressIndicator.progressProperty(), 0));
        KeyFrame keyFrameEnd = new KeyFrame(Duration.seconds(5), new KeyValue(progressIndicator.progressProperty(), 1));
        Timeline timeLine = new Timeline(keyFrameStart, keyFrameEnd);
        timeLine.play();
    }

    @FXML
    private void playRecording() {
        try {
            URL url = Paths.get("audio.wav").toUri().toURL();
            AudioInputStream stream = AudioSystem.getAudioInputStream(url);
            DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());
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
        recording = new Recording(recordingFileName + version + "-" + (recordingNumber) + ".wav");
        databaseRecording.addAttempt(recording);
        File originalFile = new File("audio.wav");
        File newFile = new File("PersonalRecordings/" + recording.getFileName());
        originalFile.renameTo(newFile);
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
    public void passInformation(DatabaseRecording databaseRecording, WorkSpaceController controller) {
        this.databaseRecording = databaseRecording;
        parentController = controller;
    }
 
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setComponentsForRecording();
    }
}
