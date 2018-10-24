package namesayer.helperClasses;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import namesayer.fxmlControllers.DatabaseViewController;
import namesayer.fxmlControllers.Main;

import java.io.IOException;

public class UIManager {
    private static Stage primaryStage;

    private UIManager() {
    }

    /**
     * This method sets the primary stage. This is used in main.
     */
    public static void setPrimaryStage(Stage primaryStage) {
        UIManager.primaryStage = primaryStage;
    }

    /**
     * This method handles changing scenes. It is given an input of an fxml file and changes to
     * the corresponding screen.
     */
    public static void changeScenes(String fxmlFile) {
        try {
            Parent sceneParent = FXMLLoader.load(Main.class.getResource(fxmlFile));
            primaryStage.setScene(new Scene(sceneParent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method handles changing scenes when a pop up screen is needed. It is given an input of an fxml file 
     * and changes to the correspoding scene.
     */
    public static void changeScenes(String fxmlFile, Stage stage) {
        try {
            Parent sceneParent = FXMLLoader.load(Main.class.getResource(fxmlFile));
            stage.setScene(new Scene(sceneParent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method opens a pop up stage.
     */
    public static void openStage(String fxmlFile) {
        try {
            Parent sceneParent = FXMLLoader.load(Main.class.getResource(fxmlFile));
            Stage newStage = new Stage();
            Scene scene = new Scene(sceneParent);
            newStage.setScene(scene);

            //Disable the background window when the rate stage is displayed.
            newStage.initModality(Modality.WINDOW_MODAL);
            newStage.initOwner(primaryStage);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
