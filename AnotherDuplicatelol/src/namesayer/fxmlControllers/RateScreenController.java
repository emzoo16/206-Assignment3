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

	//Reference to the workspace.
	WorkspaceController controller;

	//Stores the rating the user gives.
	int rating = 0;

	/*
	 * On initialising, creates a review folder if it doesn't yet exist to store all
	 * the reviews from users.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

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

		//Creating the new file if it doesn't already exist
		File file = new File("./Resources/Review/" + currentName + ".txt");

		if (!file.exists()) {
			try {
				PrintWriter writer = new PrintWriter(file);
				writer.println(rating);
				writer.println(1);
				writer.close();
				controller.updateRating(rating, currentName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			int[] ratingArray = new int[2];
			Scanner scanner = null;
			int count = 0;
			try {
				scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					ratingArray[count] = Integer.parseInt(scanner.nextLine());
					count++;
				}
				int ratingSum = ratingArray[0] + rating;
				double averageRating = (double) ratingSum / (ratingArray[1] + 1);
				controller.updateRating(averageRating, currentName);
				PrintWriter writer = new PrintWriter(file);
				writer.println(ratingSum);
				writer.println(ratingArray[1] + 1);
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		currentStage.close();
	}

	/*
	 * Used to pass the current recording name from the workspace controller to
	 * the recordController. This method is called from the workspace controller.
	 */
	public void setCurrentName(String name) {
		currentName = name;
	}

	/*
	 * Gets an instance of workspace controller so rateController can pass information to the workspace.
	 */
	public void setWorkSpaceController(WorkspaceController workSpaceController){
		controller = workSpaceController;
	}

	/*
	 * Refreshes the rating indicator in the workspace to show the updated rating.
	 */
	public void refreshRating(){
		controller.setRating(currentName);
	}

}