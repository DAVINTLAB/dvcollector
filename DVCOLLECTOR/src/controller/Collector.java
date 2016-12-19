package controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import view.StreamController;

public class Collector {
	
	private StreamController streamController;
	private String filter_args = "hearthstone";

	public Collector(StreamController streamController) {
		this.streamController = streamController;
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
	    TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
	    twitterStream.addListener(listener);
	    twitterStream.filter(filter_args);	    
	}
}
