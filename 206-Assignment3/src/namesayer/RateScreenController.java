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
	
	public File sampleDir = new File("./reviews");
	
	@FXML
	Label rateText;
	@FXML
	TextArea commentArea;
	@FXML
	Button confirmButton;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		commentArea.setPromptText("Write a review");
		commentArea.setFocusTraversable(false); 
		//commentArea.getParent().requestFocus();
	}
	
	@FXML
	public void confirmButtonClicked(ActionEvent event){
		
		try(FileWriter fileWriter = new FileWriter("./reviews/name.txt", true);
			    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			    PrintWriter out = new PrintWriter(bufferedWriter))
			{
				out.println("\n\n");
				String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
				out.println("-------------------"+currentTime + "-------------------");
				String review = commentArea.getText();
			    out.println(review);
			    
			    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			    currentStage.close();
			    
			    
			} catch (IOException e) {
			    
			}
		
		
	}

}
