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

public class Recording {

	//
    protected String fileName;
    protected String shortName;
    protected String path;
    Clip clip;

    public Recording() {}

    /*
     * Constructor for the recording class that 
     */
    public Recording(String name, String path) {
        String subString = name.substring(name.lastIndexOf("_") + 1);
        shortName = subString.replaceAll(".wav", "");
        fileName = name;
        this.path = path;
    }

    /*
     * Plays the wav file corresponding to the recording object. A volume value is passed in to 
     *set the volume accordingly. A reference to the progessBar is also given so the recording object
     *can update the progress bar as it plays.
     **/
    public void play(double volume, ProgressBar progressBar) {
    
        try {
        	File file = new File(path + fileName);
            
        	//
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            setVolume(volume);
            clip.start();
            
            //Timer object responsible for updating the progress bar
            Timer timer = new Timer("Play Timer");
            
            //Timer task that updates the progress bar as the recording plays.
            TimerTask timerTask = new TimerTask() {

				@Override
				public void run() {
					double time = (double)clip.getFramePosition()
							/stream.getFrameLength();
					progressBar.setProgress(time);
					if (time >= 1.00) {
						timer.cancel();
					}
				}
				
            };
            
            //Set the timer to run the timerTask to update the progress bar every 30 miliseconds.
            timer.scheduleAtFixedRate(timerTask, 30, 30);
            
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
     * 
     */
    public int getNumber() {
        Character digitString = fileName.charAt(fileName.lastIndexOf("-") + 1);
        return Integer.parseInt(digitString.toString());
    }
}
