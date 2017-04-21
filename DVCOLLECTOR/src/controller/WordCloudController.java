package controller;

import org.w3c.dom.Document;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import model.Collector;
import model.WordCloudCore;

public class WordCloudController {
	
	@FXML
	private WebView webView;
	
	private MainApp mainApp;
	private WebEngine webEngine;
	
	private WordCloudCore wcc;
	private boolean running;
	private Collector collector;
	
	public WordCloudController(Collector collector) {
		this.running = false;
		this.collector = collector;
	}
	
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
		this.webEngine = webView.getEngine();
		this.wcc = new WordCloudCore(collector, webEngine);
		/*
		setFrequencyList("["
				+ "{text: \"Rick\", size: 100},"
				+ "{text: \"Morty\", size: 100},"
				+ "{text: \"Summer\", size: 100},"
				+ "{text: \"Beth\", size: 100},"
				+ "{text: \"Jerry\", size: 100}"
				+ "]");
		
		collector.setStateChangeListener((observable, oldValue, newValue) -> { 
			switch(newValue){
			case STREAMING:
			case STOPPING:
				running = true;
				break;
			default:
				running = false;
			}
		});
		
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				while(true){
					try {
						//System.out.println("10s");
						Thread.sleep(10000);
						//System.out.println(wcc.getFrequencyListString());
						if(running){
							setFrequencyList(wcc.getFrequencyListString());
							draw();
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}				
			}

		});
		t.setDaemon(true); //Forces the thread to stop after the program has been close. TODO look for a better solution.
		t.start();
		*/
    	webEngine.load(WordCloudController.class.getResource("/tagcloud/tagcloud.html").toExternalForm());
    	
    	

    	//Debug tool
    	
    	webEngine.documentProperty().addListener(new ChangeListener<Document>() {
    		@Override 
    		public void changed(ObservableValue<? extends Document> prop, Document oldDoc, Document newDoc) {
    			wcc.init();
    			//webEngine.executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}"); 
    		}
    	});
    	
    }
    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
    private void draw() {
    	Platform.runLater(()-> webEngine.executeScript("drawCloud()") );
    }
    
    public void setFrequencyList(String frequencyList){
    	StringBuilder sb = new StringBuilder();
    	sb.append("frequency_list = ");
    	sb.append(frequencyList);
    	Platform.runLater(()-> webEngine.executeScript(sb.toString()) );
    }
    
	
}
