package namesayer;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Making a personal recordings folder if it doesn't exist to hold user recordings
        File file1 = new File("PersonalRecordings/");
        if (!file1.exists()) {
            file1.mkdirs();
        }
        //Removing everything from the concatenated recordings file if it exists, otherwise creating it
        File file2 = new File("ConcatenatedRecordings/");
        if (file2.exists()) {
            File[] files = file2.listFiles();
            for (File file : files) {
                file.delete();
            }
        } else {
            file2.mkdirs();
        }
        //Making a folder to hold personal recordings for concatenated recordings
        File file3 = new File("ConcatenatedPersonalRecordings/");
        if (!file3.exists()) {
            file3.mkdirs();
        }

        Parent root = FXMLLoader.load(getClass().getResource("startMenu.fxml"));
        primaryStage.setTitle("Name Sayer");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
