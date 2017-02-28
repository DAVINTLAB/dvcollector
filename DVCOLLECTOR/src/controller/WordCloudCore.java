package controller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import misc.TextCleaner;
import netscape.javascript.JSObject;
import twitter4j.Status;

public class WordCloudCore {
	
	private WebEngine webEngine;
	private JSBridge bridge;
	
	private HashMap<String, WordFrequency> frequencyList;
	private Deque<SimpleStatus> latestStatus;	
	private Integer maxLatestStatus = 300;
	private Integer maxWords = 100;
	
	private final Integer DRAW_INTERVAL = 15000;
	
	private boolean running;
	
	private Collector collector;
	
	public WordCloudCore(Collector collector, WebEngine webEngine) {
		this.frequencyList = new HashMap<String, WordFrequency>();
		this.latestStatus = new ArrayDeque<SimpleStatus>();
		this.running = false;
		this.webEngine = webEngine;
		this.bridge = new JSBridge(webEngine);
		this.collector = collector;
		setCollectorListeners();		
	}
	
	public void init(){
		bridge.setFrequencyList("["
				+ "{text: \"Rick\", size: 100},"
				+ "{text: \"Morty\", size: 100},"
				+ "{text: \"Summer\", size: 100},"
				+ "{text: \"Beth\", size: 100},"
				+ "{text: \"Jerry\", size: 100}"
				+ "]");
		
		Thread t = new Thread( () -> {
				while(true){
					try {
						Thread.sleep(DRAW_INTERVAL);
						if(running){
							bridge.setFrequencyList(getFrequencyListString());
							bridge.drawCloud();
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		});
		t.setDaemon(true); //Forces the thread to stop after the program has been close. TODO look for a better solution.
		t.start();
	}
	
	private void setCollectorListeners() {
		collector.setTweetChangeListener((observable, oldValue, newValue) -> { addStatus(newValue); });	
		
		collector.setStateChangeListener((observable, oldValue, newValue) -> { running = newValue.equals(Collector.State.STREAMING); });
	}

	public void addStatus(Status status){
		if(latestStatus.size() == maxLatestStatus) removeOldestStatus();
		
		SimpleStatus simpleStatus = new SimpleStatus(status);
		
		for(String word: simpleStatus.words){
			if(word.equals("")) continue;
			WordFrequency wf = frequencyList.get(word);
			if(wf == null) {
				wf = new WordFrequency(word);
				frequencyList.put(word, wf);
			}
			wf.increase();			
		}
		latestStatus.addLast(simpleStatus);			
	}
	
	private void removeOldestStatus() {
		SimpleStatus simpleStatus = latestStatus.removeFirst();
		
		for(String word: simpleStatus.words){
			if(word.equals("")) continue;
			WordFrequency wf = frequencyList.get(word);
			wf.decrease();
			if(wf.size == 0) frequencyList.remove(word); //TODO Might be too heavy on the JVM, change to periodically clean up empty WFs if necessary
		}
	}

	public String getFrequencyListString(){
		List<WordFrequency> list = new ArrayList<WordFrequency>(frequencyList.values());
		Collections.sort(list, (a, b) -> b.size - a.size);
		bridge.setWordSizeDomain(1, list.get(0).size);
		
		int wordLimit = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(WordFrequency word: list){
			sb.append(word.toString() + ",");
			if(++wordLimit >= maxWords) break;
		}
		if(sb.charAt(sb.length()-1) == ',') sb.deleteCharAt(sb.length() - 1);
		sb.append("]");
		return sb.toString();
	}
	
	private class SimpleStatus{
		List<String> words;
		Date date;
		
		public SimpleStatus(List<String> words, Date date) {
			this.words = words;
			this.date = date;
		}
		
		public SimpleStatus(Status status){
			this.words = TextCleaner.cleanText(status.getText());
			this.date = status.getCreatedAt();
		}
	}

	private class WordFrequency {
		String text;
		Integer size;
		
		public WordFrequency(String text){
			this.text = text;
			this.size = 0;
		}
		public WordFrequency(String text, Integer size) {
			this.text = text;
			this.size = size;
		}
		public void increase() { this.size++; }
		public void decrease() { this.size--; }
		public String toString() { 
			return String.format("{text: \"%s\", size: %d}", text, size);
		}
	}
	
	private class JSBridge{
		WebEngine engine;
		
		public JSBridge(WebEngine webEngine){
			this.engine = webEngine;
		}
		
		private void runScript(String script){
			Platform.runLater(() -> engine.executeScript(script));
		}
		
        public void setWordSizeDomain(int min, int max){
        	String script = String.format("varScale.domain([%d, %d])", min, max);
        	runScript(script);        	
        }
        
        public void setWordSizeRange(int min, int max){
        	String script = String.format("varScale.range([%d, %d])", min, max);
        	runScript(script);   
        }
        
        public void setCloudSize(int width, int height){
        	String script = String.format("layout.size([%d, %d])", width, height);
        	runScript(script);
        }
        
        public void setFrequencyList(String list){
        	StringBuilder script = new StringBuilder();
        	script.append("frequency_list = ");
        	script.append(list);
        	runScript(script.toString());
        }
        
        public void drawCloud(){
        	String script = "drawCloud()";
        	runScript(script);
        }
	}
}
