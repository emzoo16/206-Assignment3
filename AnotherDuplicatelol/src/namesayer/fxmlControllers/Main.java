package namesayer.fxmlControllers;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import namesayer.helperClasses.WorkspaceModel;
import namesayer.helperClasses.UIManager;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Making a personal recordings folder if it doesn't exist to hold user recordings
        File personalRecordingsFile = new File("Resources/PersonalRecordings/");
        if (!personalRecordingsFile.exists()) {
            personalRecordingsFile.mkdirs();
        }
        //Removing everything from the concatenated recordings file if it exists, otherwise creating it
        File catRecordingsFile = new File("Resources/ConcatenatedRecordings/");
        if (catRecordingsFile.exists()) {
            File[] files = catRecordingsFile.listFiles();
            for (File file : files) {
                file.delete();
            }
        } else {
            catRecordingsFile.mkdirs();
        }
        //Making a folder to hold personal recordings for concatenated recordings
        File catPersonalFile = new File("Resources/ConcatenatedPersonalRecordings/");
        if (!catPersonalFile.exists()) {
            catPersonalFile.mkdirs();
        }
        //Making a folder to hold playlists
        File playlistsFile = new File("Resources/Playlists/");
        if (!playlistsFile.exists()) {
            playlistsFile.mkdirs();
        }
        //Making a folder to store ratings
        File reviewFile = new File("Resources/Review/");
        if (!reviewFile.exists()) {
            reviewFile.mkdirs();
        }

        //Initializes the model
        WorkspaceModel model = WorkspaceModel.getInstance();
        model.setPrimaryStage(primaryStage);
        //Sets the UIManager primary stage to allow it to swap scenes easily
        UIManager.setPrimaryStage(primaryStage);

        //Loads the start menu
        Parent root = FXMLLoader.load(getClass().getResource("fxmlFiles/StartMenu.fxml"));
        primaryStage.setTitle("Name Sayer");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
