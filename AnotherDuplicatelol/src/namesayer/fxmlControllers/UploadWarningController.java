package namesayer.fxmlControllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import namesayer.helperClasses.WorkspaceModel;


public class UploadWarningController implements Initializable{

	/*
	 * FXML variables
	 */
	
	@FXML 
	Button okButton;
	
	@FXML 
	ListView<String> notFoundListView;
	ObservableList<String> notFoundList;

	/**
	 * If the user confirms they have seen the not found recordings
	 * @param event
	 */
	@FXML
	public void okButtonClicked(ActionEvent event) {
		Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		currentStage.close();
	}

	/**
	 * Sets the not found recordings from the model.
	 * @param location
	 * @param resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		notFoundList = FXCollections.observableArrayList(WorkspaceModel.getInstance().getNotFoundNames());
		notFoundListView.setItems(notFoundList);
	}
}
