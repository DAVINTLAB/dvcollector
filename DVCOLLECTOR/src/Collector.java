import java.io.IOException;
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

	public static void main(String args[]){
		try {
			filter();
		} catch (TwitterException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void filter() throws TwitterException, IOException{
	    StatusListener listener = new StatusListener(){
	        public void onStatus(Status status) {
	        	long id = status.getId();
	        	Date created_at = status.getCreatedAt();
	        	status.getGeoLocation();
	            System.out.println(status.getId() + "|" + status.getUser().getName() + " : " + status.getText());
	        }
	        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
	        public void onException(Exception ex) {
	            ex.printStackTrace();
	        }
			public void onScrubGeo(long arg0, long arg1) {}
			public void onStallWarning(StallWarning arg0) {}
	    };
	    TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
	    twitterStream.addListener(listener);
	    twitterStream.filter(filter_args);
	    
	}
}
