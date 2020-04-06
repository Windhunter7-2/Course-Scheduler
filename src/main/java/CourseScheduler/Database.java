package CourseScheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	
	private String name;
	private Connection connection;
	
	public Database(String name) {
		this.name = name;
	}
	
	public void create() throws SQLException {
		connection = DriverManager.getConnection("jdbc:mysql://localhost/?user=root&password=");
		Statement s = connection.createStatement();
		int res = s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + name);
		System.out.println("Executed database creation, result: " + res);
	}
	
	public Connection get() {
		return connection;
	}
	
}
