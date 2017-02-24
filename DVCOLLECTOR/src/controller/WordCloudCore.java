package controller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;

import twitter4j.Status;

public class WordCloudCore {
	
	private HashMap<String, WordFrequency> frequencyList;
	private Deque<SimpleStatus> latestStatus;	
	private Integer maxLatestStatus = 300;
	private Integer maxWords = 100;
	
	private boolean running;
	
	private Collector collector;
	
	public WordCloudCore(Collector collector) {
		this.frequencyList = new HashMap<String, WordFrequency>();
		this.latestStatus = new ArrayDeque<SimpleStatus>();
		this.running = false;
		this.collector = collector;
		addListeners();
	}
	
	private void addListeners() {
		collector.setTweetChangeListener((observable, oldValue, newValue) -> { addStatus(newValue); });	
	}

	public void addStatus(Status status){
		if(latestStatus.size() == maxLatestStatus) removeOldestStatus();
		
		SimpleStatus simpleStatus = extractSimpleStatus(status);
		
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
		
		int wordLimit = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(WordFrequency word: list){
			System.out.print(word.size + " ");
			sb.append(word.toString() + ",");
			if(++wordLimit >= maxWords) break;
		}
		if(sb.charAt(sb.length()-1) == ',') sb.deleteCharAt(sb.length() - 1);
		sb.append("]");
		return sb.toString();
	}
	
	private String[] cleanText(String text){
		text.toLowerCase();
		text = removeLinks(text);
		text = removeNewLines(text);
		text = removeNonAlphabet(text);
		text = removeWhitespace(text);
		String[] words = text.split(" ");

		return words;		
	}
	
	private String removeLinks(String text) {
		text = text.replaceAll("https?:?[\\-a-zA-Z0-9@:%_\\+.~#?&\\/]*", "");
		text = text.replaceAll("www\\.[\\-a-zA-Z0-9@:%_\\+.~#?&z\\/]*", "");
		return text;
	}

	private String removeNewLines(String text) { // And Tabs
		return text.replaceAll("[\\r\\n\\t]", " ");
	}

	private String removeWhitespace(String rawText) {
		rawText.replaceAll("\\s+", " ");
		return rawText.trim();
	}

	private String removeNonAlphabet(String text) {
		return text.replaceAll("[^a-zA-Z0-9#@\\s]", " ");		
	}

	private SimpleStatus extractSimpleStatus(Status status) {
		String[] words = cleanText(status.getText());
		Date date = status.getCreatedAt();		
		SimpleStatus simpleStatus = new SimpleStatus(words, date);
		
		return simpleStatus;
	}
	
	private class SimpleStatus{
		String[] words;
		Date date;
		public SimpleStatus(String[] words, Date date) {
			this.words = words;
			this.date = date;
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
}
