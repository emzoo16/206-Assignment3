package namesayer.fxmlControllers;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import namesayer.helperClasses.UIManager;

public class TestMicrophoneController implements Initializable{


	@FXML
	Button stopButton;

	@FXML
	Button backButton;

	@FXML
	Button testStopButton;


	@FXML
	Label testLabel;

	@FXML
	ProgressBar progressBar;

	//Variable that controls if the test microphone bar is running.
	Boolean running = true;

	//Reference to the dataline the audio is being read from.
	TargetDataLine line;

	/*
	 * Initializes the UI to test microphone mode.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		testStopButton.setText("Test");
	}

	/*
	 * This method is invoked when the back button is clicked. It takes the user back to
	 * the menu screen.
	 */
	@FXML
	public void backButtonClicked() {
		UIManager.changeScenes("fxmlFiles/StartMenu.fxml");
	}

	/*
	 * This method is invoked when the test/stop button is clicked. If the button is currently
	 *showing test, change the text to stop and vice versa. If the text is show is text, start running
	 *the bar for the microphone.
	 */
	@FXML
	public void testStopButtonClicked() {
		if(testStopButton.getText().equals("Test")) {
			testStopButton.setText("Stop");
			running = true;
			startMicBar();
		}else {
			testStopButton.setText("Test");
			running = false;
			line.stop();
			line.close();
			progressBar.setProgress(0);
		}
	}

	/*
	 * This method is responsible for making the progress bar react to the users input into
	 * the microphone. The user can then gauge if their microphone is working.
	 */
	public void startMicBar() {

		AudioFormat format =  new AudioFormat(8000, 8, 1, true, true);

		try {
			//Create and open a new dataline that the audio will be read from.
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();
			//New thread to handle reading of the users audio input.
			Task<Void> task = new Task<Void>() {

				@Override
				protected Void call() throws Exception {

					//Create an array of integers to store the audio information from the dataline and
					//start the line.
					int bytesRead;
					byte[] data = new byte[line.getBufferSize()/5];

					//The data from the line is read and converted to a double.
					//This double is then used to set the progress bar to move according
					//to the users voice.

					while(running) {
						bytesRead = line.read(data, 0, data.length);
						double i= (double)Math.abs(data[0])/15;
						progressBar.setProgress(i);
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
}
