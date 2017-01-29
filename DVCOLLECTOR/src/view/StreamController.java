package view;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import misc.IsoDateFormatter;
import twitter4j.MediaEntity;
import twitter4j.Status;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import controller.Collector;

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
    @SuppressWarnings("unused")
	private MainApp mainApp;
    private Image idleImage;
    private Image connectingImage;
    private Image streamingImage;
    private Image stoppingImage;
    private Image errorImage;
    private String imageURL;

    private Collector collector;
    private ObjectProperty<Status> currentTweet;
    private String defaultFilter = "league of legends, hearthstone, killing floor 2";
    private ObjectProperty<Collector.State> state;
    private volatile int tweetCounter;
    private IntegerProperty tweetCount;
    
    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public StreamController() {
    	this.idleImage = new Image("logo.png");
    	this.connectingImage = new Image("loading.gif");
    	this.streamingImage = new Image("streaming.gif");
    	this.stoppingImage = new Image("stopping.gif");
    	this.errorImage = new Image("error.png");
    	
    	//this.imageURL = "http://www.google.com";
    	this.tweetCount = new SimpleIntegerProperty();
    	this.currentTweet = new SimpleObjectProperty<Status>();
    	this.state = new SimpleObjectProperty<Collector.State>();
    	this.collector = new Collector();
    	this.collector.setFilter(defaultFilter);
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {    	
    	setStatus(Collector.State.UNREADY); // Necessary, since we don't actually see the collector state change to UNREADY during it's construction
    	
    	filterTextField.setPromptText(collector.getFilter());
    	
    	state.bind(collector.stateProperty());    	
    	state.addListener((observable, oldValue, newValue) -> { setStatus(newValue); });
    	
    	currentTweet.bind(collector.currentTweetProperty());
    	currentTweet.addListener((observable, oldValue, newValue) -> { updateStatus(newValue); });	
    	
    	tweetCount.bind(collector.tweetCounterProperty());
    	tweetCount.addListener((observable, oldValue, newValue) -> { counterTextField.setText(String.format("%d", tweetCount.get())); });
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
				languageTextField.setText(status.getLang());
				
				MediaEntity[] mediaEntities = status.getMediaEntities();
				if(mediaEntities != null && mediaEntities.length != 0){
					new Thread(() -> { tweetImage.setImage(new Image(mediaEntities[0].getMediaURL())); }).start(); //TODO Figure out what to do with the rest of media entities			
				} else tweetImage.setImage(null);
			}    		    
    	});
    }
    
    public void startStream() {
    	this.collector.setOAuth(consumerKey.getText(), consumerSecret.getText(), accessToken.getText(), accessTokenSecret.getText());
    	if(!(filterTextField.getText() == null || filterTextField.getText().trim().isEmpty())){
    		this.collector.setFilter(filterTextField.getText());
    	}
    	collector.start();
    }
    
	public void stopStream() {		
		collector.cancel();
	}
	
	public void openImageURL() throws IOException, URISyntaxException{
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		desktop.browse(new URI(imageURL));
	}
    
    public void setStatus(Collector.State newStatus){
    	String statusText;
    	Paint statusTextColor;
    	Image image;
    	
    	switch(newStatus){
    	case UNREADY:
    	case READY:
    		statusText = "Idle";
    		statusTextColor = Paint.valueOf("#333333"); // TODO Get the exact default color (this one is pretty close)
    		image = this.idleImage;
    		toggleStreamButtons(true);
    		break;
    	case CONNECTING:
    		statusText = "Connecting...";
    		statusTextColor = Paint.valueOf("#b3b300"); // Yellow - has to be a little dark or it gets pretty hard to read
    		image = this.connectingImage;
    		toggleStreamButtons(false);
    		break;
    	case STREAMING:
    		statusText = "Receiving stream...";
    		statusTextColor = Paint.valueOf("#006600"); // Green
    		image = this.streamingImage;
    		break;
    	case STOPPING:
    		statusText = "Stopping stream...";
    		statusTextColor = Paint.valueOf("#990000"); // Red
    		image = this.stoppingImage;
    		break;  
    	default:
    	case ERROR:
    		statusText = "Error";
    		statusTextColor = Paint.valueOf("#990000"); // Red
    		image = this.errorImage;
    	}
    	
    	Platform.runLater(new Runnable(){
    		public void run() { 
    			status.setText(statusText);
    			status.setTextFill(statusTextColor);
    			statusImage.setImage(image);
    		}    		   
    	});
    }
        
    public void toggleStreamButtons(boolean toggle){
    	// Disables or enables the start/stop stream buttons and the filter text field accordingly
    	startStreamButton.setDisable(!toggle);
    	stopStreamButton.setDisable(toggle);
    	filterTextField.setDisable(!toggle);    
    }
}