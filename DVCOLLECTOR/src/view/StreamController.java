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
    private TextField counterTextField;
    
    @FXML
    private TextField consumerKey;
    @FXML
    private TextField consumerSecret;
    @FXML
    private TextField accessToken;
    @FXML
    private TextField accessTokenSecret;
    
    @FXML
    private TextField filterTextField;
    
    @FXML
    private Label status;
    @FXML
    private ImageView animation;
    
    // Reference to the main application.
    private MainApp mainApp;
    private Image logo;
    private Image loadAnimation;
    private Image streamAnimation;
    private Image error;
    private Collector collector;
    private volatile int counter;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public StreamController() {
    	//this.collector = new Collector(this);
    	this.logo = new Image("logo.png");
    	this.loadAnimation = new Image("loading.gif");
    	this.streamAnimation = new Image("streaming.gif");
    	this.error = new Image("error.png");
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {    	
    	setIdleStatus();
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
				counterTextField.setText(String.format("%d", ++counter));
			}    		   
    	});
    }
    
    public void startStream(){
    	//createCollector();
    	this.collector = new Collector(this, consumerKey.getText(), consumerSecret.getText(), accessToken.getText(), accessTokenSecret.getText());
    	if(filterTextField.getText() != null){
    		this.collector.setFilter(filterTextField.getText());
    	}
    	setLoadingStatus();
    	
    	startStreamButton.setDisable(true);
    	stopStreamButton.setDisable(false);
    	filterTextField.setDisable(true);    	
    	
    	try {
			collector.startStreamFilter();
		} catch (TwitterException | IOException e) {
			setErrorStatus();
			e.printStackTrace();
		}
    }
    
	public void stopStream(){
		collector.stopStreamFilter();
		
		startStreamButton.setDisable(false);
		stopStreamButton.setDisable(true);
		filterTextField.setDisable(false);
		
		setIdleStatus();
    }
    
    public void setLoadingStatus(){ 
    	status.setText("Connecting..."); 
    	status.setTextFill(Paint.valueOf("#b3b300")); // Yellow - has to be a little dark or it gets pretty hard to read
    	animation.setImage(loadAnimation); 
    }
    public void setStreamingStatus(){
    	status.setText("Receiving stream");
    	status.setTextFill(Paint.valueOf("#006600")); // Green
    	animation.setImage(streamAnimation);
    }
    public void setIdleStatus(){
    	status.setText("Idle");
    	status.setTextFill(Paint.valueOf("#333333")); // TODO Get the exact default color (this one is pretty close)
    	animation.setImage(logo);
    }
    public void setErrorStatus(){
    	status.setText("Error");
    	status.setTextFill(Paint.valueOf("#990000"));
    	animation.setImage(error);
    }
}