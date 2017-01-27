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
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.TwitterException;
import util.IsoDateFormatter;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import controller.*;

public class StreamController {
	
	//Left pane
	@FXML
	private Button startStreamButton;
	@FXML
	private Button stopStreamButton;
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
    private TextField counterTextField;
    @FXML
    private Label status;
    @FXML
    private ImageView statusImage;
    //Right pane
    @FXML
    private TextArea tweetTextField;
    @FXML
    private TextField userTextField;
    @FXML
    private TextField dateTextField;
    @FXML
    private TextField languageTextField;
    @FXML
    private ImageView tweetImage;
    
    // Reference to the main application.
    private MainApp mainApp;
    private Image logo;
    private Image loadAnimation;
    private Image streamAnimation;
    private Image error;
    private Collector collector;
    private volatile int counter;
    private String imageURL;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public StreamController() {
    	//this.collector = new Collector(this);
    	this.logo = new Image("logo.png");
    	this.loadAnimation = new Image("logo.png");
    	this.streamAnimation = new Image("logo.png");
    	this.error = new Image("logo.png");
    	this.imageURL = "http://www.google.com";
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	setStatus(ViewStatus.IDLE);
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
    public void updateStatus(Status status) {
    	// Necessary to update the UI without crashing
    	// TODO Look into this
    	Platform.runLater(new Runnable(){
			public void run() { 
				userTextField.setText(status.getUser().getScreenName());
				dateTextField.setText(IsoDateFormatter.format(status.getCreatedAt()));
				tweetTextField.setText(status.getText());
				counterTextField.setText(String.format("%d", ++counter));
				languageTextField.setText(status.getLang());
				
				MediaEntity[] mediaEntities = status.getMediaEntities();
				if(mediaEntities != null && mediaEntities.length != 0){
					System.out.println("IBAGEM");
					System.out.println(mediaEntities[0].getMediaURL());
					tweetImage.setImage(new Image(mediaEntities[0].getMediaURL()));
				} else tweetImage.setImage(null);
			}    		    
    	});
    }
    
    public void startStream() {
    	//createCollector();
    	this.collector = new Collector(this, consumerKey.getText(), consumerSecret.getText(), accessToken.getText(), accessTokenSecret.getText());
    	System.out.println(":" + filterTextField.getText() + ":");
    	if(!(filterTextField.getText() == null || filterTextField.getText().trim().isEmpty())){
    		this.collector.setFilter(filterTextField.getText());
    	}
    	setStatus(ViewStatus.LOADING);
    	
    	toggleStreamButtons(false);  	
    	
    	try {
			collector.startStreamFilter();
		} catch (TwitterException | IOException e) {
			setStatus(ViewStatus.ERROR);
			e.printStackTrace();
		}
    }
    
	public void stopStream() {
		setStatus(ViewStatus.STOPPING);
		
		toggleStreamButtons(true);
		
		collector.stopStreamFilter();
		
		//setIdleStatus();
	}
	
	public void openImageURL() throws IOException, URISyntaxException{
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		desktop.browse(new URI(imageURL));
	}
    
    public static enum ViewStatus { IDLE, LOADING, STREAMING, STOPPING, ERROR }
    
    public void setStatus(ViewStatus viewStatus){
    	String statusText;
    	Paint statusTextColor;
    	Image statusImage;
    	
    	switch(viewStatus){
    	case IDLE:
    		statusText = "Idle";
    		statusTextColor = Paint.valueOf("#333333"); // TODO Get the exact default color (this one is pretty close)
    		statusImage = this.logo;
    		break;
    	case LOADING:
    		statusText = "Connecting...";
    		statusTextColor = Paint.valueOf("#b3b300"); // Yellow - has to be a little dark or it gets pretty hard to read
    		statusImage = this.loadAnimation;
    		break;
    	case STREAMING:
    		statusText = "Receiving stream...";
    		statusTextColor = Paint.valueOf("#006600"); // Green
    		statusImage = this.streamAnimation;
    		break;
    	case STOPPING:
    		statusText = "Stopping stream...";
    		statusTextColor = Paint.valueOf("#990000"); // Red
    		statusImage = this.error; // TODO Put an appropriate image here
    		break;  
    	default:
    	case ERROR:
    		statusText = "Error";
    		statusTextColor = Paint.valueOf("#990000"); // Red
    		statusImage = this.error;
    	}
    	
    	this.status.setText(statusText);
    	this.status.setTextFill(statusTextColor); // Green
    	this.statusImage.setImage(statusImage);
    }
        
    public void toggleStreamButtons(boolean toggle){
    	// Disables or enables the start/stop stream buttons and the filter text field accordingly
    	startStreamButton.setDisable(toggle);
    	stopStreamButton.setDisable(!toggle);
    	filterTextField.setDisable(toggle);    
    }
}