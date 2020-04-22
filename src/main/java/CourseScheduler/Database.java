package CourseScheduler;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	
	private final String name;
	
	public Database(String name) {
		this.name = name;
	}
	
	/**
	 * Sets up the local filesystem before attempting to connect.
	 */
	private Path createPath() throws IOException {
		return LocalStorage.get(name + ".db").toPath();
	}
	
	/**
	 * Creates this Database, if it doesn't exist already. This also establishes a connection to
	 * the database, which can be accessed via {@link #get()}.
	 */
	public void create() throws IOException {
		createPath();
	}
	
	/**
	 * @return A Connection to this database, which can be used to create statements and perform updates.
	 */
	public Connection get() throws SQLException, IOException {
		return DriverManager.getConnection("jdbc:sqlite:" + createPath().toString());
	}
	
}
