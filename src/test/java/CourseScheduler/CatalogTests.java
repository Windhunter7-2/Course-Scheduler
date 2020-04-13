package CourseScheduler;

import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertTrue;

public class CatalogTests {
	
	@Test
	public void creation() throws SQLException, IOException {
		// This test verifies that no exception is thrown when a Database is created and connected to.
		Database db = new Database("test");
		db.create();
		
		Catalog catalog = new Catalog(db);
		catalog.create();
		
		Connection connection = db.get();
		Statement statement = connection.createStatement();
		statement.execute("SELECT name FROM sqlite_master WHERE type='table' AND name = 'course'");
		assertTrue("Table was not successfully created", statement.getResultSet().next());
	}
	
}
