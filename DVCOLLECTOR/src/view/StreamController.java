package view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import twitter4j.Status;
import twitter4j.TwitterException;
import util.IsoDateFormatter;

import java.io.IOException;

import controller.*;

public class StreamController {
	
	@FXML
	private TextArea tweetTextField;
    @FXML
    private TextField userTextField;
    @FXML
    private TextField dateTextField;
    @FXML
    private Button startStreamButton;
    @FXML
    private Button stopStreamButton;
    // Reference to the main application.
    private MainApp mainApp;
    private Collector collector;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public StreamController() {
    	this.collector = new Collector(this);
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	try {
			collector.startStreamFilter();
		} catch (TwitterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
    public void updateStatus(Status status){
    	// Necessary to update the UI without crashing
    	// TODO Look into this
    	Platform.runLater(new Runnable(){
			public void run() { 
				userTextField.setText(status.getUser().getScreenName());
				dateTextField.setText(IsoDateFormatter.format(status.getCreatedAt()));
				tweetTextField.setText(status.getText());
			}    		   
    	});
    }
    
}