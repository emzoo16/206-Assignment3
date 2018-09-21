package namesayer;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class RateScreenController implements Initializable  {
	
	//The folder containing all the reviews.
	public File sampleDir = new File("./reviews");
	
	//FXML variables
	@FXML
	Label rateText;
	@FXML
	TextArea commentArea;
	@FXML
	Button confirmButton;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Sets a prompt for the textArea
		commentArea.setPromptText("Write a review");
		commentArea.setFocusTraversable(false); 
		
	}
	
	//This method writes the users comment to a file.
	@FXML
	public void confirmButtonClicked(ActionEvent event){
		
		//Writes to a file (hardcoded as name.txt here so will need to change for the actual
		//name variable). Creates a new file if none is found, else appends to the current file.
		try(FileWriter fileWriter = new FileWriter("./reviews/name.txt", true);
			    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			    PrintWriter out = new PrintWriter(bufferedWriter))
			{
				//Write date/time of the new review and write to the given file.
				out.println("\n\n");
				String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
				out.println("-------------------"+currentTime + "-------------------");
				String review = commentArea.getText();
			    out.println(review);
			    
			    //Close the rate window after confirmation.
			    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			    currentStage.close();
			    
			    
			} catch (IOException e) {
			    //Do something here. Not sure what?
			}
		
		
	}

}
