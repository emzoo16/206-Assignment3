package namesayer;

import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

public class Recording {
    protected String fileName;
    protected String shortName;
    protected String path;

    public Recording() {}

    public Recording(String name) {
        String subString = name.substring(name.lastIndexOf("_") + 1);
        shortName = subString.replaceAll(".wav", "");
        fileName = name;
        path = "PersonalRecordings/";
    }

    public void play() {
        try {
            URL url = Paths.get(path + fileName).toUri().toURL();
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

    public String getShortName() {
        return shortName;
    }

    public String getFileName() { return fileName; }

    public int getNumber() {
        Character digitString = fileName.charAt(fileName.lastIndexOf("-") + 1);
        return Integer.parseInt(digitString.toString());
    }
}
