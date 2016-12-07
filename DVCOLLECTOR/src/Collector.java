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

public class Collector {
	
	public static String filter_args = "hearthstone"; // Separate terms by comma
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
																	//2016-12-07T16:41:26Z
																	//Wed Aug 27 13:08:45 +0000 2008"
																	//EEE MMM dd HH:mm:ss Z yyyy

	public static void main(String args[]){
		try {
			startStreamFilter();
		} catch (TwitterException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void startStreamFilter() throws TwitterException, IOException{
	    StatusListener listener = new StatusListener(){
	        public void onStatus(Status status) {
	        	long id = status.getId();
	        	Date created_at = status.getCreatedAt();
	        	long user_id = status.getUser().getId();
	        	String user_screenname = status.getUser().getScreenName();
	        	String text = status.getText();
	        	
	        	
	            //System.out.println(status.getId() + "|" + status.getUser().getName() + " : " + status.getText());
	        	String tweet = String.format("%s %s %s\n\n", dateFormat.format(created_at), user_screenname, text);
	        	System.out.print(tweet);
	        }
	        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
	        public void onException(Exception ex) { ex.printStackTrace(); }
			public void onScrubGeo(long arg0, long arg1) {}
			public void onStallWarning(StallWarning arg0) {}
	    };
	    TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
	    twitterStream.addListener(listener);
	    twitterStream.filter(filter_args);
	    
	}
}
