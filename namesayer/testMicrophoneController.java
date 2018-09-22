package namesayer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import javafx.scene.control.*;
import javafx.util.Duration;

public class testMicrophoneController implements Initializable{
	
	@FXML
	Button backButton;
	
	@FXML
	Button testButton;
	
	@FXML
	Button playButton;
	
	@FXML
	Label testLabel;
	
	@FXML
	ProgressBar progressBar;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	@FXML
	public void backButtonClicked() {
		try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            Parent createScene = FXMLLoader.load(getClass().getResource("startMenu.fxml"));
            stage.setScene(new Scene(createScene, 700, 500));
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	@FXML
	public void testButtonClicked() {
		testButton.setVisible(true);
		playButton.setVisible(false);
		recordAudio();
		testButton.setVisible(false);
		
	}
	
	@FXML
	public void playButtonClicked() {
		testButton.setVisible(false);
		playButton.setVisible(true);
		playAudio();
	}
	
	public void recordAudio(){
		
		Task<Void> task = new Task<Void>() {
		
			@Override
			protected Void call() throws Exception{
				String command = "ffmpeg -f pulse -loglevel quiet -i default -t 5 test.wav";
				ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
				Process recordingProcess = builder.start();
				recordingProcess.waitFor();
				return null;
			}
		};
	
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
		runProgressBar();
		
	}
	public void playAudio() {
		AudioClip audioFile = new AudioClip(new File("test.wav").toURI().toString());
		audioFile.play();
	}
	
	public void runProgressBar() {
		KeyFrame keyFrameStart = new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0));
        KeyFrame keyFrameEnd = new KeyFrame(Duration.seconds(5), new KeyValue(progressBar.progressProperty(), 1));
        Timeline timeLine = new Timeline(keyFrameStart, keyFrameEnd);
        timeLine.play();
	}
}

