package controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import util.IsoDateFormatter;
import view.StreamController;

public class StatusStreamListener implements StatusListener {
	
	public StreamController streamController;
	
	public StatusStreamListener(StreamController streamController) {
		this.streamController = streamController;
	}

	public void onStatus(Status status) {
    	Date created_at = status.getCreatedAt();
    	String user_screenname = status.getUser().getScreenName();
    	String text = status.getText();
    	
    	DatabaseManager.insertStatus(status);
    	streamController.updateStatus(status);
    	
    	String tweet = String.format("%s | %s | %s\n\n", IsoDateFormatter.format(created_at), user_screenname, text);
    	System.out.print(tweet);
    }
	
	public void onException(Exception ex) { 
		Platform.runLater(new Runnable(){
			public void run() { 
				streamController.setErrorStatus(); 
			}    		   
    	});
		ex.printStackTrace();
	}
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
	public void onScrubGeo(long arg0, long arg1) {}
	public void onStallWarning(StallWarning arg0) {}

}
