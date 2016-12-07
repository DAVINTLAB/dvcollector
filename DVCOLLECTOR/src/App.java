import java.io.IOException;

import twitter4j.TwitterException;

public class App {
	public static void main(String args[]) throws TwitterException, IOException{
		DatabaseManager.createDatabase();
		Collector.startStreamFilter();
		
		
	}
}
