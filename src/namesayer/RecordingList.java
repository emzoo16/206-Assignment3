package namesayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.HashMap;

public class RecordingList {
    protected HashMap<String, Recording> recordingsMap;

    public RecordingList() {
        recordingsMap = new HashMap<>();
    }

    public ObservableList<String> getRecordingNames() {
        ObservableList<String> recordingsList = FXCollections.observableArrayList(recordingsMap.keySet());
        Collections.sort(recordingsList);
        return recordingsList;
    }

    public void add(String name, Recording recording) {
        if (!recordingsMap.keySet().contains(name)) {
            recordingsMap.put(name, recording);
        }
    }

    public void remove(String name) {
        if (recordingsMap.keySet().contains(name)) {
            recordingsMap.remove(name);
        }
    }

    public Recording getRecording(String name) {
        return recordingsMap.get(name);
    }
}
