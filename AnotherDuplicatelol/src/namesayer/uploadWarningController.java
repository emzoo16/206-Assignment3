package namesayer;

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


public class uploadWarningController implements Initializable{

	/*
	 * FXML variables
	 */
	
	@FXML 
	Button okButton;
	
	@FXML 
	ListView<String> notFoundListView;
	ObservableList<String> notFoundList = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	@FXML
	public void okButtonClicked(ActionEvent event) {
		Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		currentStage.close();
	}
	public void setNotFoundList(List<String> notFound) {
		notFoundList = FXCollections.observableArrayList(notFound);
		notFoundListView.setItems(notFoundList);
	}
}
