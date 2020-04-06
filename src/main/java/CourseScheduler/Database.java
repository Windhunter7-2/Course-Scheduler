package CourseScheduler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	
	private String name;
	private Connection connection;
	
	public Database(String name) {
		this.name = name;
	}
	
	/**
	 * Sets up the local filesystem before attempting to connect.
	 */
	private Path createPath() {
		// Use the local %APPDATA% variable
		String appdata = System.getenv("APPDATA");
		// Create a path within under %APPDATA%/CourseScheduler/name.db
		Path path = Paths.get(appdata, "CourseScheduler", name + ".db");
		// Make sure the parent folder (%APPDATA%/CourseScheduler) exists
		path.toFile().getParentFile().mkdir();
		return path;
	}
	
	/**
	 * Creates this Database, if it doesn't exist already. This also establishes a connection to
	 * the database, which can be accessed via {@link #get()}.
	 *
	 * @throws SQLException If an error occurs while trying to create the database.
	 */
	public void create() throws SQLException {
		Path path = createPath();
		connection = DriverManager.getConnection("jdbc:sqlite:" + path.toString());
	}
	
	/**
	 * @return A Connection to this database, which can be used to create statements and perform updates.
	 */
	public Connection get() {
		return connection;
	}
	
}
