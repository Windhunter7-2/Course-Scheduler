package CourseScheduler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Catalog {
	
	private Database db;
	
	public Catalog(Database db) {
		this.db = db;
	}
	
	/**
	 * Creates the catalog's necessary information in the database.
	 */
	public void create() throws SQLException {
		String string = "CREATE TABLE IF NOT EXISTS course (" +
				"code TEXT PRIMARY KEY NOT NULL," + // compound identifier of name + number for convenience
				"name TEXT NOT NULL," + // eg 'Calc II'
				"number INTEGER NOT NULL," + // eg '101'
				"type TEXT NOT NULL," + // eg 'cs'
				"credits INTEGER," + // number of credits
				"description TEXT," + // full course description
				"prereqs TEXT NOT NULL," + // pre-requisite codes stored in a comma separated list
				"flag INTEGER" + // flags for the algorithm
				");";
		Connection connection = db.get();
		Statement statement = connection.createStatement();
		statement.execute(string);
	}
	
	public Database getDatabase() {
		return db;
	}
	
}
