package namesayer.fxmlControllers;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import namesayer.helperClasses.WorkspaceModel;
import namesayer.interfaces.PlayController;
import namesayer.recordingTypes.DemoRecording;
import namesayer.recordingTypes.PersonalRecording;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.*;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class RecordController implements Initializable, PlayController {
	
	
	@FXML
    private Button stopButton;
	@FXML
    private Label statusLabel;
    @FXML
    private Button recordButton;
    @FXML
    private Button redoButton;
    @FXML
    private Button playButton;
    @FXML
    private Button keepButton;
    @FXML
    private Button returnButton;
    @FXML
    private Button demoButton;
    @FXML
    private Label instructionLabel;
    @FXML
    private ImageView playOrStopImage;

    //Controls the recording process. Starts/stops recording
    private Boolean running;
    
    //Stores the current chosen volume.
    private Double volume;

    //Recording objects 
    private PersonalRecording recording;
    private DemoRecording databaseRecording;

    //Reference to the workspace controller
    private WorkspaceController parentController;

    private TargetDataLine line;

    private Clip clip;

    private WorkspaceModel model;

    private static final String STOP_IMAGE = "namesayer/imageResources/icons8-stop-filled-100.png";
    private static final String PLAY_IMAGE = "namesayer/imageResources/icons8-play-filled-100.png";
   

    /*
     * This method is invoked when the user chooses not to keep their recording and retry.
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
    	
    	//Make the stop button visible and update text to show user they are recording.
    	stopButton.setVisible(true);
    	statusLabel.setText("Recording...");

        //Disable the record button once the user starts the recording.
        recordButton.setVisible(false);

        //Variable used to stop the recording externally.
        running = true;

        //Record the audio from a dataline.
        AudioFormat format =  new AudioFormat(44000, 8, 1, true, true);
        try {
            //Initializing and opening the dataline.
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            //Starting the recording.
            line.start();

            //New thread to write the input to a file.
            Task<Void> task = new Task<Void>() {

                @Override
                protected Void call() throws Exception {

                    //Handles writing of audio into file.
                    AudioInputStream inputStream = new AudioInputStream(line);

                    //New file audio will be saved in.
                    File audioFile = new File("./Resources/audio.wav");

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
    private void stopButtonClicked() {
    	running = false;
        line.stop();
        line.close();
        setComponentsForProcessingRecording();
        statusLabel.setText("");
    }

    /*
     * This method is invoked when the user wants to hear back the recording they have just created.
     */
    @FXML
    private void playRecording() {
        if (clip != null && clip.isActive()) {
            clip.stop();
            playOrStopImage.setImage(new Image(PLAY_IMAGE));
        } else {
            try {
                //Gets the URL for the temporary recording.
                URL url = Paths.get("Resources/audio.wav").toUri().toURL();

                AudioInputStream stream = AudioSystem.getAudioInputStream(url);
                DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());
                //create a new clip to play the file on the dataline.
                clip = (Clip) AudioSystem.getLine(info);
                clip.open(stream);
                playOrStopImage.setImage(new Image(STOP_IMAGE));
                addClipListener(clip);
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
    }

    /*
     * Plays the database recording of the current name.
     */
    @FXML
    private void playDemo() {
        if (databaseRecording.isPlaying()) {
            databaseRecording.stopPlaying();
        } else {
            databaseRecording.setController(this);
            demoButton.setText("Stop Demo");
            databaseRecording.play(volume);
        }
    }

    /*
     * Method is called when the user chooses to keep their recording. The new recording is saved
     * and added to the personal recording listView.
     */
    @FXML
    private void keepRecording() {
        stopAllPlaying();
        //Builds the fileName for the new personal recording
        String databaseRecordingName = databaseRecording.getFileName();
        int recordingNumber = databaseRecording.getUnusedAttemptsNumber();
        String recordingFileName = databaseRecordingName.substring(0, databaseRecordingName.lastIndexOf("."));
        String version = "";
        String shortName = databaseRecording.getShortName();
        //If it is a concatenated name
        if (shortName.contains(" ")) {
            //Changes the temp audio.wav file to the new file name and creates the personal recording
            recording = new PersonalRecording(shortName + "-" + (recordingNumber) + ".wav", "Resources/ConcatenatedPersonalRecordings/");
            databaseRecording.addAttempt(recording);
            File originalFile = new File("Resources/audio.wav");
            File newFile = new File("Resources/ConcatenatedPersonalRecordings/" + recording.getFileName());
            originalFile.renameTo(newFile);
        } else { //If it is a single length name
            //Gets the version of the recording
            if (shortName.contains("(")) {
                version = shortName.substring(shortName.indexOf("("), shortName.lastIndexOf(")") + 1);
            }
            //Chenges the temp audio.wav file to the new file name and creates the personal recording
            recording = new PersonalRecording(recordingFileName + version + "-" + (recordingNumber) + ".wav", "Resources/PersonalRecordings/");
            databaseRecording.addAttempt(recording);
            File originalFile = new File("Resources/audio.wav");
            File newFile = new File("Resources/PersonalRecordings/" + recording.getFileName());
            originalFile.renameTo(newFile);

        }
        //Allows the workspace to update
        model.notifyOfStageClose();
        Stage currentStage = (Stage) returnButton.getScene().getWindow();
        currentStage.close();
    }

    /**
     * Stops both recordings from playing, this method is invoked when the stage is closed
     */
    private void stopAllPlaying() {
        if (clip != null && clip.isActive()) {
            clip.stop();
        }
        if (databaseRecording.isPlaying()) {
            databaseRecording.stopPlaying();
        }
    }

    /*
     * Takes the user back to the workspace.
     */
    @FXML
    private void returnToWorkspace() {
        stopAllPlaying();
        //Close the recording window
        Stage currentStage = (Stage) returnButton.getScene().getWindow();
        currentStage.close();
    }

    /*
     * Sets the UI layout for recording.
     */
    private void setComponentsForRecording() {
    	instructionLabel.setText("Press record to start recording");
        recordButton.setVisible(true);
        recordButton.setDisable(false);
        playButton.setVisible(false);
        redoButton.setVisible(false);
        keepButton.setVisible(false);
        demoButton.setVisible(false);
        stopButton.setVisible(false);
  
    }

    /*
     * Sets the UI for asking user if they want to keep their recording.
     */
    private void setComponentsForProcessingRecording() {
        instructionLabel.setText("What do you want to do with this recording");
        recordButton.setVisible(false);
        playButton.setVisible(true);
        redoButton.setVisible(true);
        keepButton.setVisible(true);
        demoButton.setVisible(true);
        stopButton.setVisible(false);    
    }


    /**
     * This checks the clip that is playing has finished and updates the button icon accordingly
     * @param currentClip
     */
    private void addClipListener(Clip currentClip) {
        currentClip.addLineListener(new LineListener() {
            @Override
            public void update(LineEvent event) {
                if (clip != null && !clip.isActive()) {
                    playOrStopImage.setImage(new Image(PLAY_IMAGE));
                }
            }
        });
    }

    /**
     * Initializes the buttons and the model variables
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setComponentsForRecording();
        model = WorkspaceModel.getInstance();
        this.databaseRecording = model.getCurrentDemoRecording();
        this.volume = model.getVolume();
    }

    /**
     *Changes the label on the demo button once playing has finished.
     */
    @Override
    public void playingFinished() {
        demoButton.setText("Play Demo");
    }
}
