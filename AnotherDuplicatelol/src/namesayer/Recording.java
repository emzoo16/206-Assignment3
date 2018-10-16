package namesayer;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Recording {

	//Variable storing the full name of the file and the trimmed name (only the actual name)
	//respectively.
    protected String fileName;
    protected String shortName;
    
    //Path to the audio file of the name.
    protected String path;
    
    //Clip object to play recordings.
    Clip clip;

    public Recording() {}

    /*
     * Plays the wav file corresponding to the recording object. A volume value is passed in to 
     *set the volume accordingly. A reference to the progessBar is also given so the recording object
     *can update the progress bar as it plays.
     **/
    public void play(double volume, ProgressBar progressBar) {
    
        try {
        	File file = new File(path + fileName);
            
        	//Sets up the dataline and clip to start playback of the audio.
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            setVolume(volume);
            clip.start();

            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * Sets the volume of the audio clip given a double value. The double is converted to decibels.
     */
    public void setVolume(double volume) {
    	
  
    	FloatControl gain = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        
    	//Converts the current volume to decibels and adjusts the gain in volume accordingly.
    	float decibel = 20f*(float)Math.log10(volume/0.65);
    	gain.setValue(decibel);
    }

    /*
     * Returns the name of the recording without the preceeding numbers.
     */
    public String getShortName() {
        return shortName;
    }

    /*
     * Returns the file name of the recording
     */
    public String getFileName() { return fileName; }

    /*
     * Returns the version number of the recording.
     */
    public int getNumber() {
        Character digitString = fileName.charAt(fileName.lastIndexOf("-") + 1);
        return Integer.parseInt(digitString.toString());
    }
}
