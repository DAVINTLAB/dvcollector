package controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

public class Collector {
	
	private StreamController streamController;
	private String filterArgs = "league of legends, hearthstone";
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
	
	public void startStreamFilter() throws TwitterException, IOException{
		DatabaseManager.connectToDatabase();
	    StatusListener listener = new StatusStreamListener(streamController);
	    this.twitterStream = new TwitterStreamFactory().getInstance();
	    this.twitterStream.addListener(listener);
	    this.twitterStream.addConnectionLifeCycleListener(new ConnectionLifeCycleListener() {			
	    	public void onConnect() { streamController.setStreamingStatus(); }
	    	public void onDisconnect() {}
			public void onCleanUp() {}
		});
	    
	    this.twitterStream.filter(filterArgs);	    
	}
	public void stopStreamFilter(){
		this.twitterStream.shutdown();
	}
}
