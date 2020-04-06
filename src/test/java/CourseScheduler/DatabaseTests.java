package CourseScheduler;

import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseTests {
	
	@Test
	public void createsDatabase() throws SQLException, IOException {
		// This test verifies that no exception is thrown when a Database is created and connected to.
		Database db = new Database("test");
		db.create();
	}
	
}
