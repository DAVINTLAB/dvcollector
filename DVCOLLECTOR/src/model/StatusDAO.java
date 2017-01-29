package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;

import misc.IsoDateFormatter;
import twitter4j.Status;

public class StatusDAO {
	
	private Connection con;
	
	private String DB_NAME = "dvcollector.db";
	private final String DEFAULT_TABLE = "TWEETS";
	
	private String table;
	
	public StatusDAO(){
		this.con = null;
		this.table = DEFAULT_TABLE;
	}
	
	public StatusDAO(String table) {
		this.con = null;
		this.table = table;
	}
		
	public boolean insertStatus(Status status){
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
	
	private boolean connectToDatabase(){
		try{
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
			System.out.println("Opened database successfully.");
			return true;
		} catch ( Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return false;
		}
	}
}
