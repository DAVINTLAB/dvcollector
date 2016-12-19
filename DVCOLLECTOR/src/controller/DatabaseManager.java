package controller;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import twitter4j.Status;

public class DatabaseManager {
	
	public static String DB_NAME = "dvcollector.db";
	
	public static SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	public static Connection con;
	
	public static void main( String args[] ) throws SQLException {
		createDatabase();	
		connectToDatabase();
		Statement s = con.createStatement();
		s.execute("SELECT * FROM TWEETS");
		ResultSet resultSet = s.getResultSet();
		
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		while (resultSet.next()) {
		    System.out.println(resultSet.getString(3));
		    System.out.println("");
		}
		s.close();
	}
	
	public static boolean insertStatus(Status status) {
		try{
			long id = status.getId();
        	Date created_at = status.getCreatedAt();
        	long user_id = status.getUser().getId();
        	String user_screenname = status.getUser().getScreenName();
        	String text = status.getText();
        	
        	System.out.println(id);
        	
        	String sql = "INSERT INTO TWEETS (user_id, user_screenname, id, text, created_at) " + 
                		 "VALUES( ?, ?, ?, ?, ? )";
        	
        	PreparedStatement statement = con.prepareStatement(sql);
        	statement.setLong(1, user_id);
        	statement.setString(2, user_screenname);
        	statement.setLong(3, id);
        	statement.setString(4, text);
        	statement.setString(5, isoFormat.format(created_at));
			statement.executeUpdate();			

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
					" user_id				INTEGER				NOT NULL," +
					" user_screenname		TEXT				NOT NULL, " + 
					" id					INTEGER PRIMARY KEY	NOT NULL," +
					" text					TEXT				NOT NULL, " + 
					" created_at			TEXT				NOT NULL " +
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
