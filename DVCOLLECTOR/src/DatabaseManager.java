import java.sql.*;

public class DatabaseManager {
	
	public static String DB_NAME = "dvcollector.db";
	
	public static void main( String args[] ){
		Connection c = null;
		Statement statement = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);

			statement = c.createStatement();
			String sql = 
					"CREATE TABLE TWEETS( " +
					" ID		INTEGER PRIMARY KEY	NOT NULL," +
					" NAME		TEXT			NOT NULL, " + 
					" AGE		INT				NOT NULL, " + 
					" ADDRESS	CHAR(50), " + 
					" SALARY	REAL" +
					" )"; 
			statement.executeUpdate(sql);
			statement.close();
			c.close();


			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		System.out.println("Opened database successfully");
	}
}
