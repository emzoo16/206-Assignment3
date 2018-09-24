package namesayer;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class RateScreenController implements Initializable  {
	
	int rating = 0;
	
	//The folder containing all the reviews.
	String currentName;
	WorkSpaceController controller;

	//FXML variables
	@FXML
	Label rateText;
	
	@FXML
	Button confirmButton;
	@FXML
	CheckBox check1,check2,check3,check4,check5;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		File file = new File("Review");
		file.mkdirs();

	}
	
	//This method writes the users comment to a file.
	@FXML
	public void confirmButtonClicked(ActionEvent event){

		File file = new File("./Review/" + currentName + ".txt");

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
	
	@FXML 
	public void check1Clicked() {
		rating = 1;
		check2.setSelected(false);
		check3.setSelected(false);
		check4.setSelected(false);
		check5.setSelected(false);
		
	}
	@FXML 
	public void check2Clicked() {
		rating = 2;
		check1.setSelected(true);
		check3.setSelected(false);
		check4.setSelected(false);
		check5.setSelected(false);
	}
	@FXML 
	public void check3Clicked() {
		rating = 3;
		check1.setSelected(true);
		check2.setSelected(true);
		check4.setSelected(false);
		check5.setSelected(false);
	}
	@FXML 
	public void check4Clicked() {
		rating = 4;
		check1.setSelected(true);
		check2.setSelected(true);
		check3.setSelected(true);
		check5.setSelected(false);
	}
	@FXML 
	public void check5Clicked() {
		rating = 5;
		check1.setSelected(true);
		check2.setSelected(true);
		check3.setSelected(true);
		check4.setSelected(true);
	}
	
	@FXML
	public void setCurrentName(String name) {
		currentName = name;
	}

	public void setWorkSpaceController(WorkSpaceController workSpaceController){
		controller = workSpaceController;
	}

	public void refreshRating(){
		controller.setRating(currentName);
	}

}
