package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import twitter4j.Status;
import controller.*;

public class StreamController {

    @FXML
    private Label tweetLabel;
    @FXML
    private Label userLabel;
    @FXML
    private Button startStreamButton;
    @FXML
    private Button stopStreamButton;
    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public StreamController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
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
    	userLabel.setText(status.getUser().getScreenName());
    	tweetLabel.setText(status.getText());
    }
    
}