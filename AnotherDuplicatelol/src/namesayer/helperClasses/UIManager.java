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

    public static void setPrimaryStage(Stage primaryStage) {
        UIManager.primaryStage = primaryStage;
    }

    public static void changeScenes(String fxmlFile) {
        try {
            Parent sceneParent = FXMLLoader.load(Main.class.getResource(fxmlFile));
            primaryStage.setScene(new Scene(sceneParent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void changeScenes(String fxmlFile, Stage stage) {
        try {
            Parent sceneParent = FXMLLoader.load(Main.class.getResource(fxmlFile));
            stage.setScene(new Scene(sceneParent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
