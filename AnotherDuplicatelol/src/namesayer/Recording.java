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
    
    private PlayController controller;

    public Recording() {}

    /*
     * Plays the wav file corresponding to the recording object. A volume value is passed in to 
     *set the volume accordingly. A reference to the progessBar is also given so the recording object
     *can update the progress bar as it plays.
     **/
    public void play(double volume, ProgressBar progressBar) {
        AudioPlayer player = new AudioPlayer(volume);
        Task<Void> playTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                player.play(path + fileName);
                return null;
            }
        };
        Thread thread = new Thread(playTask);
        thread.setDaemon(true);
        thread.start();
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

    /**
     * To get the reference
     */
    private class AudioPlayer {

        // size of the buffer
        private static final int BUFFER_SIZE = 4096;
        //Volume of the playback
        private double volume;

        private AudioPlayer(double volume) {
            this.volume = volume;
        }

        void play(String audioPath) {
            File file = new File(audioPath);
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = audioStream.getFormat();
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
                audioLine.open(format);
                setVolume(volume, audioLine);
                audioLine.start();

                byte[] bytesBuffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;

                while ((bytesRead = audioStream.read(bytesBuffer)) != -1) {
                    audioLine.write(bytesBuffer, 0, bytesRead);
                }

                audioLine.drain();
                audioLine.close();
                audioStream.close();
                playingCompleted();

            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*
         * Sets the volume of the audio clip given a double value. The double is converted to decibels.
         */
        void setVolume(double volume, SourceDataLine audioLine) {
            FloatControl gain = (FloatControl)audioLine.getControl(FloatControl.Type.MASTER_GAIN);

            //Converts the current volume to decibels and adjusts the gain in volume accordingly.
            float decibel = 20f*(float)Math.log10(volume/0.65);
            gain.setValue(decibel);
        }
    }

    private void playingCompleted() {
        controller.playingFinished();
    }
}
