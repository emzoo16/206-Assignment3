package namesayer.fxmlControllers;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import namesayer.helperClasses.WorkspaceModel;
import namesayer.recordingTypes.DemoRecording;

public class RateScreenController implements Initializable  {

	//FXML variables
	@FXML
	Label rateText;

	@FXML
	Button confirmButton;
	@FXML
	CheckBox check1,check2,check3,check4,check5;

	@FXML
	Slider rateSlider;

	//The folder containing all the reviews.
	String currentName;

	WorkspaceModel model;

	//Stores the rating the user gives.
	int rating = 0;

	/*
	 * On initialising, creates a review folder if it doesn't yet exist to store all
	 * the reviews from users.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = WorkspaceModel.getInstance();
		rating=3;
		rateSlider.setValue(3.00);
		rateText.setText(rating + ". Average");

		rateSlider.valueProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				rating = (int)rateSlider.getValue();

				if (rating == 1) {
					rateText.setText(rating + ". Very Poor");

				}else if (rating == 2) {
					rateText.setText(rating + ". Poor");

				}else if (rating == 3) {
					rateText.setText(rating + ". Average");

				}else if (rating == 4) {
					rateText.setText(rating + ". Good");

				}else if (rating == 5) {
					rateText.setText(rating + ". Very Good");

				}
			}

		});

	}

	/*
	 * This method writes the users review to the file with the corresponding name.
	 */
	@FXML
	public void confirmButtonClicked(ActionEvent event){
		model.getCurrentDemoRecording().rate(rating);
		model.notifyOfStageClose();
		Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		currentStage.close();
	}

}