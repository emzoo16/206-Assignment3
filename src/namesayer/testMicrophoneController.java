package namesayer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

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
		recordLayout();
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
		recordAudio();
	}
	
	@FXML
	public void playButtonClicked() {
		
		playAudio();
		recordLayout();
		
	}
	
	public void recordAudio(){
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
                    playLayout();
                }
        };
		
		Thread thread = new Thread(recordTask);
		thread.setDaemon(true);
		thread.start();
		runProgressBar();
	
	}
	
	public void playAudio() {
	   try { URL url = Paths.get("test.wav").toUri().toURL();
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
	
	public void runProgressBar() {
		KeyFrame keyFrameStart = new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0));
        KeyFrame keyFrameEnd = new KeyFrame(Duration.seconds(5), new KeyValue(progressBar.progressProperty(), 1));
        Timeline timeLine = new Timeline(keyFrameStart, keyFrameEnd);
        timeLine.play();
	}
	public void playLayout() {
		testButton.setVisible(false);
		playButton.setVisible(true);
	}
	public void recordLayout() {
		testButton.setVisible(true);
		playButton.setVisible(false);
	}
}


