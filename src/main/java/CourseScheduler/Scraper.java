package CourseScheduler;

import java.sql.SQLException;

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
	
	
}
