package namesayer;

import java.io.ByteArrayOutputStream;
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
	
	TargetDataLine dataLine;
	boolean stopped = false;
	
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
                    stopped = true;
                }
        };
        
        Task<Void> dataTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
            	//writeToTargetLine();
        		//readFromTargetLine();
        		//return null;
         //  }
        //public void writeToTargetLine() {
    		float sampleRate = 8000;
    		int sampleSizeInBits = 8;
    		int channels = 1;
    		boolean signed = true;
    		boolean bigEndian = true;
    		AudioFormat format =  new AudioFormat(sampleRate, 
    		  sampleSizeInBits, channels, signed, bigEndian);
    		
    		DataLine.Info info = new DataLine.Info(TargetDataLine.class, 
    		    format); // format is an AudioFormat object
    		if (!AudioSystem.isLineSupported(info)) {
    		    // Handle the error ... 

    		}
    		// Obtain and open the line.
    		try {
    		    dataLine = (TargetDataLine) AudioSystem.getLine(info);
    		    dataLine.open(format);
    		} catch (LineUnavailableException ex) {
    		    // Handle the error ... 
    		}
    	//}
    	
    	//public void readFromTargetLine() {
    		// Assume that the TargetDataLine, line, has already
    		// been obtained and opened.
    		ByteArrayOutputStream out  = new ByteArrayOutputStream();
    		int numBytesRead;
    		byte[] data = new byte[dataLine.getBufferSize() / 5];

    		// Begin audio capture.
    		dataLine.start();

    		// Here, stopped is a global boolean set by another thread.
    		while (!stopped) {
    		   // Read the next chunk of data from the TargetDataLine.
    		   numBytesRead =  dataLine.read(data, 0, data.length);
    		   // Save this chunk of data.
    		   out.write(data, 0, numBytesRead);
    		   double i=data[0];
    		   progressBar.setProgress((double)(Math.abs(i/10)));
    		   
    		}     
    	//} 
			return null;
            }
        };
        
        Task<Void> micTestTask = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				// TODO Auto-generated method stub
				return null;
			}
//          @Override
//          protected Void call() throws Exception {
//          return null;
//          }
        };
//         
		
		Thread thread = new Thread(recordTask);
		Thread dataThread = new Thread(recordTask);
		
		thread.setDaemon(true);
		thread.start();
		dataThread.setDaemon(true);
		dataThread.start();
		//runProgressBar();
		//writeToTargetLine();
		//readFromTargetLine();
		
	
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


