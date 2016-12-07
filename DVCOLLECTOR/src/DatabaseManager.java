import java.sql.*;
import java.util.Date;

import twitter4j.Status;

public class DatabaseManager {
	
	public static String DB_NAME = "dvcollector.db";
	
	public static Connection con;
	
	public static void main( String args[] ) {
		createDatabase();		
	}
	
	public static boolean insertStatus(Status status) {
		try{
			long id = status.getId();
        	Date created_at = status.getCreatedAt();
        	long user_id = status.getUser().getId();
        	String user_screenname = status.getUser().getScreenName();
        	String text = status.getText();
        	
        	Statement statement = con.createStatement();			
			String sql = String.format("INSERT INTO TWEETS VALUES( %d, %s, %d, %s, %s )", 
					user_id, user_screenname, id, text, created_at);
			statement.executeUpdate(sql);			
			
			statement.close();
			System.out.println("Tweet saved succesfully.");			
			return true;
			
		} catch( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.out.println("Failure on saving a tweet.");
			return false;
			//System.exit(0);
		}
	}
	
	public static void createDatabase() {
		try {			
			if(!connectToDatabase()) throw new Exception("Connection couldn't be established.");

			Statement statement = con.createStatement();
			String sql = 
					"CREATE TABLE TWEETS( " +
					" USER_ID				INTEGER				NOT NULL," +
					" USER_SCREENNAME		TEXT				NOT NULL, " + 
					" ID					INTEGER PRIMARY KEY	NOT NULL," +
					" TEXT					TEXT				NOT NULL, " + 
					" CREATED_AT			TEXT				NOT NULL " +
					" )"; 
			statement.executeUpdate(sql);			
			statement.close();
			System.out.println("Table \"Tweets\" created succesfully.");
			
			con.close();
			System.out.println("Connection closed succesfully.");
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
	
	public static boolean connectToDatabase(){
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
