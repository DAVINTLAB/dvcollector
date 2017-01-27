package controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import twitter4j.ConnectionLifeCycleListener;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import view.StreamController;
import view.StreamController.ViewStatus;

public class Collector {
	
	private StreamController streamController;
	private String filter = "league of legends, hearthstone, killing floor 2";
	private TwitterStream twitterStream;

	public Collector(StreamController streamController) {
		this.streamController = streamController;
	}
	
	public Collector(StreamController streamController, String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		this.streamController = streamController;

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(false);
		cb.setOAuthConsumerKey(consumerKey);
		cb.setOAuthConsumerSecret(consumerSecret);
		cb.setOAuthAccessToken(accessToken);
		cb.setOAuthAccessTokenSecret(accessTokenSecret);
		this.twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		
	}
	
	/*public static void main(String args[]){
		try {
			DatabaseManager.connectToDatabase();
			startStreamFilter();
		} catch (TwitterException | IOException e) {
			e.printStackTrace();
		}
	}*/
	public void setFilter(String filter){
		this.filter = filter;
	}
	
	public void startStreamFilter() throws TwitterException, IOException{
		DatabaseManager.connectToDatabase();
	    StatusListener listener = new StatusStreamListener(streamController);
	    this.twitterStream = new TwitterStreamFactory().getInstance();
	    this.twitterStream.addListener(listener);
	    this.twitterStream.addConnectionLifeCycleListener(new ConnectionLifeCycleListener() {			
	    	public void onConnect() { 
	    		Platform.runLater(new Runnable(){
	    			public void run() { 
	    				streamController.setStatus(ViewStatus.STREAMING);
	    			}    		   
	        	});
	    	}
	    	public void onDisconnect() {}
			public void onCleanUp() {}
		});
	    
	    //this.twitterStream.filter(filter);	    
	    this.twitterStream.sample();	    

	}
	
	public void stopStreamFilter(){
		System.out.println("Heyo");
		twitterStream.cleanUp();
		streamController.setStatus(ViewStatus.IDLE);
		System.out.println("Stream shutdown.");
	}
	
	public String getFilter(){
		return filter;
	}
}
