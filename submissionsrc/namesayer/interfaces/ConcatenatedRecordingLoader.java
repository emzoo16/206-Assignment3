package namesayer.interfaces;

/**
 * This interface is used by controllers which load concatenated recordings, it allows the controller to be updated once
 * the recording has finished loading
 */
public interface ConcatenatedRecordingLoader {
    public void concatenationComplete();
}
