package namesayer.helperClasses;

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import namesayer.interfaces.ConcatenatedRecordingLoader;
import namesayer.interfaces.ParentStageController;
import namesayer.recordingTypes.DemoRecording;
import namesayer.fxmlControllers.UploadController;

import java.util.List;

public class WorkspaceModel {

    private static WorkspaceModel instance;

    private DatabaseList currentWorkspaceRecordings;
    private ObservableList<String> currentRecordingsDisplay;
    private DemoRecording currentDemoRecording;
    private Double volume;
    private ParentStageController stageController;
    private ConcatenatedRecordingLoader loadingController;
    private UploadController uploadController;
    private Stage primaryStage;

    private List<String> notFoundNames;

    private WorkspaceModel() {
        currentWorkspaceRecordings = new DatabaseList();
    }

    public static WorkspaceModel getInstance() {
        if (instance == null) {
            instance = new WorkspaceModel();

        }
        return instance;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public DatabaseList getCurrentWorkspaceRecordings() {
        return currentWorkspaceRecordings;
    }

    public void setCurrentWorkspaceRecordings(DatabaseList currentWorkspaceRecordings) {
        this.currentWorkspaceRecordings = currentWorkspaceRecordings;
        this.currentRecordingsDisplay = currentWorkspaceRecordings.getRecordingNames();
    }

    public void addToCurrentWorkspaceRecordings(DatabaseList addedRecordings) {
        List<String> keptRecordings = addedRecordings.getRecordingNames();
        for (String recordingName : keptRecordings) {
            currentWorkspaceRecordings.add(addedRecordings.getRecording(recordingName));
        }
        this.currentRecordingsDisplay = currentWorkspaceRecordings.getRecordingNames();
    }

    public ObservableList<String> getCurrentRecordingsDisplay() {
        return currentRecordingsDisplay;
    }

    public void setCurrentRecordingsDisplay(ObservableList<String> currentRecordingsDisplay) {
        this.currentRecordingsDisplay = currentRecordingsDisplay;
    }

    public DemoRecording getCurrentDemoRecording() {
        return currentDemoRecording;
    }

    public void setCurrentDemoRecording(DemoRecording currentDemoRecording) {
        this.currentDemoRecording = currentDemoRecording;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public List<String> getNotFoundNames() {
        return notFoundNames;
    }

    public void setNotFoundNames(List<String> notFoundNames) {
        this.notFoundNames = notFoundNames;
        uploadController.showUploadWarning();
    }

    public void setStageController(ParentStageController stageController) {
        this.stageController = stageController;
    }

    public void setLoadingController(ConcatenatedRecordingLoader loadingController) {
        this.loadingController = loadingController;
    }

    public void notifyOfConcatenateCompletion() {
        loadingController.concatenationComplete();
    }

    public void notifyOfStageClose() {
        stageController.stageHasClosed();
    }
}
