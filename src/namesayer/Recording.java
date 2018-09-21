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
    protected String path;

    public Recording() {}

    public Recording(String name) {
        fileName = name;
        String pathSegment = name.substring(1, name.lastIndexOf(")"));
        path = "PersonalRecordings/" + pathSegment + "/";
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
}
