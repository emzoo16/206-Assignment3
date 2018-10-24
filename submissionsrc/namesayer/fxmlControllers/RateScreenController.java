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
	private Label rateText;

	@FXML
	private Button confirmButton;

	@FXML
	private Slider rateSlider;

	//The name of the recording being rated.
	private String currentName;

	//Model to pass values between controllers.
	private WorkspaceModel model;

	//Stores the rating the user gives. Initially set at zero.
	private int rating = 0;

	/**
	 * Initializes the rating slider to 3 for an average score.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//get an instance of the workspace.
		model = WorkspaceModel.getInstance();

		//Upon opening the screen, the slider will be set to '3'
		rating=3;
		rateSlider.setValue(3.00);
		rateText.setText(rating + ". Average");


		//Add a listener to change the text and position of the slider whenever the user moves the slider.
		//The slider has been coded to snap to certain positions in the slider indicating the related scores.
		rateSlider.valueProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				//Get the value of the slider and use it to adjust the text on top of the slider.
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

	/**
	 * This method is invoked when users confirm the rating. It sets the users chosen rating to
	 * the demo recording and closes the stage.
	 */
	@FXML
	private void confirmButtonClicked(ActionEvent event) {
		model.getCurrentDemoRecording().rate(rating);
		model.notifyOfStageClose();
		Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		currentStage.close();
	}

}