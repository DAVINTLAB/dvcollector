package view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import twitter4j.Status;
import twitter4j.TwitterException;
import util.IsoDateFormatter;

import java.io.IOException;

import controller.*;

public class StreamController {
	
	@FXML
	private Button startStreamButton;
	@FXML
	private Button stopStreamButton;
	
	@FXML
	private TextArea tweetTextField;
    @FXML
    private TextField userTextField;
    @FXML
    private TextField dateTextField;
    
    @FXML
    private TextField consumerKey;
    @FXML
    private TextField consumerSecret;
    @FXML
    private TextField accessToken;
    @FXML
    private TextField accessTokenSecret;
    
    @FXML
    private Label status;
    @FXML
    private ImageView animation;
    
    // Reference to the main application.
    private MainApp mainApp;
    private Image logo;
    private Image loadAnimation;
    private Image streamAnimation;
    private Collector collector;
    private boolean buttonToggle;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public StreamController() {
    	//this.collector = new Collector(this);
    	this.logo = new Image("logo.png");
    	this.loadAnimation = new Image("loading.gif");
    	this.streamAnimation = new Image("streaming.gif");
    	this.buttonToggle = true;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {    	
    	//setIdleStatus();
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
    
    public void startStream(){
    	//createCollector();
    	collector = new Collector(this);
    	setLoadingStatus();
    	toggleStreamButtons();
    	try {
			collector.startStreamFilter();
		} catch (TwitterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void toggleStreamButtons(){
    	startStreamButton.setDisable(buttonToggle);
    	stopStreamButton.setDisable(!buttonToggle);
    	buttonToggle = !buttonToggle;
    }
    
    private void createCollector() {
    	String consumerKey = this.consumerKey.getText();
    	String consumerSecret = this.consumerSecret.getText();
    	String accessToken = this.accessToken.getText();
    	String accessTokenSecret = this.accessTokenSecret.getText();
    	System.out.println(consumerKey);
    	System.out.println(consumerSecret);
    	System.out.println(accessToken);
    	System.out.println(accessTokenSecret);
		this.collector = new Collector(this, consumerKey, consumerSecret, accessToken, accessTokenSecret);
	}

	public void stopStream(){
    	collector.stopStreamFilter();
    	toggleStreamButtons();
    	setIdleStatus();
    }
    
    public void setLoadingStatus(){ 
    	status.setText("Connecting..."); 
    	status.setTextFill(Paint.valueOf("#b3b300")); // Yellow - has to be a little dark or it gets pretty hard to read
    	animation.setImage(loadAnimation); 
    }
    public void setStreamingStatus(){
    	Platform.runLater(new Runnable(){
			public void run() { 
				status.setText("Receiving stream");
				status.setTextFill(Paint.valueOf("#006600")); // Green
				animation.setImage(streamAnimation);
			}    		   
    	});
    }
    public void setIdleStatus(){
    	status.setText("Idle");
    	status.setTextFill(Paint.valueOf("#333333")); // TODO Get the exact default color (this one is pretty close)
    	animation.setImage(logo);
    }
}