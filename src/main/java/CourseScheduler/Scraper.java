package CourseScheduler;

import java.sql.SQLException;
import java.util.Date;

public class Scraper {
	
	static final String URL = "";
	
	private Catalog catalog;
	private Database db;
	
	public Scraper(Catalog catalog) {
		this.catalog = catalog;
		this.db = catalog.getDatabase();
	}
	
	public void run() throws SQLException {
		catalog.create();
		
		// TODO
	}

	public Date getLastRun() {
		System.out.println(System.getProperty("user.dir").toString());
		//Talk to Nathan about querying SQL if we're doing it that way.
		return new Date(1992);
	}
}
