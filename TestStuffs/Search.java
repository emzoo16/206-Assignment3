package TestStuffs;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class Search implements Initializable{

ObservableList<String> testData = FXCollections.observableArrayList();
FilteredList<String> filteredList;
@FXML
TextField searchBar;

@FXML
ListView<String> listView;

@Override
public void initialize(URL location, ResourceBundle resources) {

	testData.add("Apple");
	testData.add("Banana");
	testData.add("Carrot");
	testData.add("Watermelon");
	testData.add("Broccoli");
	testData.add("Spinach");
	testData.add("Orange");
	testData.add("Blueberry");
	testData.add("Grape");
	
	filteredList= new FilteredList<>(testData, data -> true);
	listView.setItems(filteredList);
	searchBar.textProperty().addListener(((observable, oldValue, newValue) -> {
	        filteredList.setPredicate(currentValue -> {
	        	String lastChar = newValue.substring(newValue.length() - 1);
	        	if(lastChar.equals(" ") || lastChar.equals("_")) {
	        		filteredList.setPredicate(x -> true);
                }
	        	
	            if (newValue == null || newValue.isEmpty()){
                return true;
	            }
            String lowerCaseSearch = newValue.toLowerCase();
            return currentValue.toLowerCase().contains(lowerCaseSearch);
        });

	}));
}

public void addSearchListener() {
	filteredList= new FilteredList<>(testData, data -> true);
	listView.setItems(filteredList);
	searchBar.textProperty().addListener(((observable, oldValue, newValue) -> {
	        filteredList.setPredicate(currentValue -> {
	        	String lastChar = newValue.substring(newValue.length() - 1);
	        	if(lastChar.equals(" ") || lastChar.equals("_")) {
	        		filteredList = new FilteredList<>(testData, data -> true);
	        		listView.setItems(filteredList);
                }
	        	
	            if (newValue == null || newValue.isEmpty()){
                return true;
	            }
            String lowerCaseSearch = newValue.toLowerCase();
            return currentValue.toLowerCase().contains(lowerCaseSearch);
        });

	}));
}

}
