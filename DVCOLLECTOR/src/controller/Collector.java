package controller;

import java.io.File;
import java.util.Date;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import model.StatusDAO;
import twitter4j.ConnectionLifeCycleListener;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Collector{

	private ObjectProperty<State> state;
	private StringProperty filter;
	private TwitterStream twitterStream;
	private volatile IntegerProperty tweetCounter;
	private ObjectProperty<Status> currentTweet;
	private boolean isCancelled;

	private StatusDAO statusDAO;
	
	private final String DEFAULT_FILTER = "league of legends, hearthstone, killing floor 2";
	
	public enum State{ UNREADY, READY, CONNECTING, STREAMING, STOPPING, ERROR };
	
	public Collector() {
		this.isCancelled = false;
		
		this.filter = new SimpleStringProperty();
		this.filter.set(DEFAULT_FILTER);
		
		this.state = new SimpleObjectProperty<State>();
		this.state.set(State.UNREADY);
		
		this.currentTweet = new SimpleObjectProperty<Status>();
		this.currentTweet.set(null);
		
		
		this.twitterStream = null;
		
		this.statusDAO = new StatusDAO();
		this.tweetCounter = new SimpleIntegerProperty();
		this.tweetCounter.set(statusDAO.getTotalStatus());
	}
	
	public boolean setOAuth(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret){
		if(!(state.get().equals(State.UNREADY) || state.get().equals(State.READY))) return false;
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(false);
		cb.setOAuthConsumerKey(consumerKey);
		cb.setOAuthConsumerSecret(consumerSecret);
		cb.setOAuthAccessToken(accessToken);
		cb.setOAuthAccessTokenSecret(accessTokenSecret);
		this.twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		
		if(filter.get() != null) changeState(State.READY);
		return true;
	}

	private void changeState(State newState){ // Useless for now, might be useful this later.
		state.set(newState);
		/*Platform.runLater(new Runnable() {
			    @Override public void run() {
			    	state.set(newState);
			    }
		 });*/
	}

	public boolean start(){
		if(!state.get().equals(State.READY)) return false;
		
		changeState(State.CONNECTING);
		
	    this.twitterStream.addListener(new StatusListener() {
			public void onException(Exception arg0) { System.out.println("@@@@@@@@@@EXCEPTION@@@@@@@@@@");}
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
			public void onScrubGeo(long userId, long upToStatusId) {}
			public void onStallWarning(StallWarning warning) { System.out.println("@@@@@@@@@@STALL WARNING@@@@@@@@@@");}
			public void onStatus(Status status) {
				currentTweet.set(status);
				tweetCounter.set(tweetCounter.get() + 1);
		    	statusDAO.insertStatus(status);
			}
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
	    });
	    this.twitterStream.addConnectionLifeCycleListener(new ConnectionLifeCycleListener() {			
	    	public void onConnect() { changeState(State.STREAMING); }
	    	public void onDisconnect() { System.out.println("Disconnected!"); }
			public void onCleanUp() { changeState(State.READY); System.out.println("Stream shut down."); }
		});
	    
    	System.out.println("Beginning stream...");
	    
	    twitterStream.filter(filter.get());
	    
	    new Thread(new Runnable(){
			public void run() {
				while(!isCancelled){
					System.out.println(getMessage());
					try { Thread.sleep(2000); /*System.out.println(getState());*/ }
					catch (InterruptedException e) {
						changeState(State.ERROR);
						e.printStackTrace();
					}
				}
				twitterStream.cleanUp();
			}
	    }).start();
	    
	    return true;
	}
	
	public boolean cancel() {
		if(!(state.get().equals(State.CONNECTING) || state.get().equals(State.STREAMING))) return false;		
		changeState(State.STOPPING);
		this.isCancelled = true;
		return true;
	}
	
	public String getFilter(){
		return filter.get();
	}
	
	public boolean setFilter(String filter){
		if(!(state.get().equals(State.UNREADY) || state.get().equals(State.READY))) return false;
		
		this.filter.set(filter);
		System.out.println("Filter set to:\n" + this.filter.get());
		
		if(twitterStream != null) changeState(State.READY);
		return true;
	}
	
	public void exportStatus(File file){
		statusDAO.exportAllStatus(file);
	}
	public void exportStatus(){
		statusDAO.exportAllStatus();
	}
	
	public void setStateChangeListener(ChangeListener<State> changeListener){
		state.addListener(changeListener);
	}
	
	public void removeStateChangeListener(ChangeListener<State> changeListener){
		state.removeListener(changeListener);
	}
	
	public ObjectProperty<State> stateProperty(){
		return state;
	}
	
	public ReadOnlyObjectProperty<Exception> exceptionProperty() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Throwable getException() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getMessage() {
		String message;
		switch(state.get()){
		case CONNECTING:
			message = "Connecting to Twitter's filter streaming API...";
			break;
		case READY:
			message = "Ready to begin streaming.";
			break;
		case STOPPING:
			message = "Shutting down the stream...";
			break;
		case STREAMING:
			message = "Receiving tweets...";
			break;
		case UNREADY:
			message = "Waiting for a keyword filter or OAuth information...";
			break;
		default:
		case ERROR:
			message = "An error has occurred.";
			break;
		}
		return message;
	}
	
	public double getProgress() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public State getState() {
		return state.get();
	}
	
	public ObjectProperty<Status> currentTweetProperty(){
		return currentTweet;
	}
	
	public void setTweetChangeListener(ChangeListener<Status> changeListener){
		currentTweet.addListener(changeListener);
	}
	
	public void removeTweetChangeListener(ChangeListener<Status> changeListener){
		currentTweet.removeListener(changeListener);
	}
	
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public double getTotalWork() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getWorkDone() {
		return tweetCounter.intValue();
	}

	public boolean isRunning() {
		return state.get().equals(State.CONNECTING) || state.get().equals(State.STREAMING);
	}

	
	public ReadOnlyStringProperty messageProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	public ReadOnlyDoubleProperty progressProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	public ReadOnlyBooleanProperty runningProperty() {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	public ReadOnlyObjectProperty stateProperty() {
		// TODO Auto-generated method stub
		return null;
	}
	*/
	public ReadOnlyStringProperty titleProperty() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ReadOnlyDoubleProperty totalWorkProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	public ReadOnlyObjectProperty<Object> valueProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	public IntegerProperty tweetCounterProperty() {
		return tweetCounter;
	}
	
	public void setTweetCounterChangeListener(ChangeListener<Number> changeListener){
		tweetCounter.addListener(changeListener);
	}
	
	public void removeTweetCounterChangeListener(ChangeListener<Number> changeListener){
		tweetCounter.removeListener(changeListener);
	}

}