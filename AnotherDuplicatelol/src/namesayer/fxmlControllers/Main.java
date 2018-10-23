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
        File file1 = new File("Resources/PersonalRecordings/");
        if (!file1.exists()) {
            file1.mkdirs();
        }
        //Removing everything from the concatenated recordings file if it exists, otherwise creating it
        File file2 = new File("Resources/ConcatenatedRecordings/");
        if (file2.exists()) {
            File[] files = file2.listFiles();
            for (File file : files) {
                file.delete();
            }
        } else {
            file2.mkdirs();
        }
        //Making a folder to hold personal recordings for concatenated recordings
        File file3 = new File("Resources/ConcatenatedPersonalRecordings/");
        if (!file3.exists()) {
            file3.mkdirs();
        }
        //Making a folder to hold playlists
        File file4 = new File("Resources/Playlists/");
        if (!file4.exists()) {
            file4.mkdirs();
        }
        //Making a folder to store ratings
        File file5 = new File("Resources/Review/");
        if (!file5.exists()) {
            file5.mkdirs();
        }

        WorkspaceModel model = WorkspaceModel.getInstance();
        model.setPrimaryStage(primaryStage);
        UIManager.setPrimaryStage(primaryStage);

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
