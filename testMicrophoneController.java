package namesayer;

import java.io.File;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
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
	
	Boolean running = true;
	TargetDataLine line;
	
	@FXML
	Button stopButton;

	@FXML
	Button backButton;

	@FXML
	Button testButton;


	@FXML
	Label testLabel;

	@FXML
	ProgressBar progressBar;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		testButton.setText("Test");
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
		if(testButton.getText().equals("Test")) {
			testButton.setText("Stop");
			running = true;
			startMicBar();
		}else {
			testButton.setText("Test");
			running = false;
		}
	}
	
	public void startMicBar() {
		
 		AudioFormat format =  new AudioFormat(8000, 8, 1, true, true);
 		
        try {
        	DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			
			Task<Void> task = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					int bytesRead;
					byte[] data = new byte[line.getBufferSize()/5];
					line.start();
					
					while(running) {
						bytesRead = line.read(data, 0, data.length);
						double i= (double)Math.abs(data[0])/15;
						progressBar.setProgress(i);
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
	}
}

