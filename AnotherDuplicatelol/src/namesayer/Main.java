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
    	/*
    	 * Making a new Personal Recording folder it if does not already exist to
    	 * store all the users personal recordings.
    	 */
        File file1 = new File("PersonalRecordings/");
        if (!file1.exists()) {
            file1.mkdirs();
        }
        /*
         * Making a new Concatenated Recordings folder if it does not already exist
         * to store all the users concatenated recordings.
         */
        File file2 = new File("ConcatenatedRecordings/");
        if (!file2.exists()) {
            file2.mkdirs();
        }
        File file3 = new File("Playlists/");
        if (!file3.exists()) {
            file3.mkdirs();
        }
        Parent root = FXMLLoader.load(getClass().getResource("startMenu.fxml"));
        primaryStage.setTitle("Name Sayer");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
