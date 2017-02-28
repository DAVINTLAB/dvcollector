package view;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import view.StreamController;
import controller.Collector;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    
    private Collector collector;

    @Override
    public void start(Stage primaryStage) {    	
    	this.collector = new Collector();
    	
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("DVCollector");
        this.primaryStage.getIcons().add(new Image("logo.png"));
        this.primaryStage.setOnCloseRequest((event) -> { Platform.exit(); System.exit(0); });
        initRootLayout();

        showMainWindow();
        showWordCloud();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("BorderWindow.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    public void showMainWindow() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("MainWindow.fxml"));            
            loader.setControllerFactory(className -> new StreamController(collector));            
            AnchorPane mainWindow = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(mainWindow);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showWordCloud(){
    	try{
    		FXMLLoader loader = new FXMLLoader();
    		loader.setLocation(MainApp.class.getResource("WordCloud.fxml"));            
    		loader.setControllerFactory(className -> new WordCloudController(collector));            
    		AnchorPane wordCloudWindow = (AnchorPane) loader.load();
    		
    		rootLayout.setBottom(wordCloudWindow);
    	} catch (IOException e) {
 
    		e.printStackTrace();
    	}
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public Collector getCollector(){
    	return collector;
    }

    public static void main(String[] args) {
        launch(args);
    }
}