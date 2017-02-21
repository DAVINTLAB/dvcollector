package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import misc.IsoDateFormatter;
import twitter4j.Status;

public class StatusDAO {
	
	private Connection con;
	
	private final String DEFAULT_DB = "dvcollector.db";
	private final String DEFAULT_TABLE = "TWEETS";
	
	private String database;
	private String table;
	
	public StatusDAO() {
		this.con = null;
		this.database = DEFAULT_DB;
		this.table = DEFAULT_TABLE;
	}
	
	public StatusDAO(String table) {
		this.con = null;
		this.database = DEFAULT_DB;
		this.table = table;
	}
		
	public boolean insertStatus(Status status) {
		if(con == null) connectToDatabase();
		
		try{
			long id = status.getId();
        	Date created_at = status.getCreatedAt();
        	long user_id = status.getUser().getId();
        	String user_screenname = status.getUser().getScreenName();
        	String text = status.getText();
        	
        	System.out.print("Tweet " + id);
        	
        	String sql = "INSERT INTO " + table + " (user_id, user_screenname, id, text, created_at) " + 
                		 "VALUES( ?, ?, ?, ?, ? )";
        	
        	PreparedStatement statement = con.prepareStatement(sql);
        	statement.setLong(1, user_id);
        	statement.setString(2, user_screenname);
        	statement.setLong(3, id);
        	statement.setString(4, text);
        	statement.setString(5, IsoDateFormatter.format(created_at));
			statement.executeUpdate();			

			statement.close();
			System.out.println(" was saved succesfully.");			
	    	String tweet = String.format("%s: %s\n", user_screenname, text);
	    	System.out.println(tweet);
			return true;
			
		} catch( Exception e ) {
			System.out.println(" failed on insertion.");
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
			//System.exit(0);
		}
	}
	
	//TODO Re-do this whole thing somehow. Looks way too heavy on memory
	public List<Status> getAllStatus(){
		if(con == null) connectToDatabase();
		
		List<Status> statusResult = null;
		
    	try {
    		String sql = "SELECT * FROM " + table;
    		
			PreparedStatement statement = con.prepareStatement(sql);
			ResultSet results = statement.executeQuery();
			
			statusResult = new ArrayList<Status>();
			
			while(results.next()){
				long userId = results.getLong("user_id");
				String userScreenname = results.getString("user_screenname");
				long id = results.getLong("id");
				String text = results.getString("text");
				Date createdAt = IsoDateFormatter.format(results.getString("created_at"));
				
				System.out.println(id + " - " + userId);
				
				SimpleStatus status = new SimpleStatus();
				status.setId(id);
				status.setText(text);
				status.setUserId(userId);
				status.setCreatedAt(createdAt);
				status.setUserScreename(userScreenname);
				
				statusResult.add(status);
			}
    	
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return statusResult;
	}
	
	private ResultSet getAllStatusResultSet(){
		if(con == null) connectToDatabase();
		ResultSet results = null;
		
    	try {
    		String sql = "SELECT * FROM " + table;
    		
			PreparedStatement statement = con.prepareStatement(sql);
			results = statement.executeQuery();    	
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return results;
	}
	
	public int getTotalStatus(){
		if(con == null) connectToDatabase();
		ResultSet results = null;
		int total = -1;
    	try {
    		String sql = "SELECT COUNT(*) FROM " + table;
    		
			PreparedStatement statement = con.prepareStatement(sql);
			results = statement.executeQuery();   
			total = results.getInt(1);
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("Total tweets: " + total);
    	return total;
	}
	
	private boolean connectToDatabase() {
		try{
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + database);
			System.out.println("Connected to database successfully.");
			return true;
		} catch ( Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
	}
		
	public void exportAllStatus(File file){		
		if(!file.getName().endsWith(".csv")) file = new File(file.getAbsolutePath() + ".csv");
		try (CSVPrinter printer = new CSVPrinter(new BufferedWriter(new FileWriter(file)), CSVFormat.EXCEL)){			
			System.out.println("Exporting tweets to:\n" + file.getAbsolutePath());
			printer.printRecords(getAllStatusResultSet());
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void exportAllStatus(){
		Calendar now = Calendar.getInstance();
		String fileName = String.format("%d-%d-%d %s-%s", now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), database, table);
		exportAllStatus(new File(fileName));
	}
	
}
