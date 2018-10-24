package namesayer.interfaces;

/**
 * This interface is used by controllers which play recordings, it allows the recording to update the controller once
 * playing has finished.
 */
public interface PlayController {
    public void playingFinished();
}
