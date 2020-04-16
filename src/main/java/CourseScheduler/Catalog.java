package CourseScheduler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Catalog {
	
	private Database db;
	private List<Course> courses = null;
	
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
				"coreqs TEXT NOT NULL," + // co-requisite codes stored in a comma separated list
				"flag INTEGER," + // flags for the algorithm
				"parents TEXT NOT NUll" + // parents
				");";
		Connection connection = db.get();
		Statement statement = connection.createStatement();
		statement.execute(string);
	}
	
	/**
	 * Retrieves a list of the courses from the Catalog database. This might end up fetching the courses
	 * if this method has not been called before.
	 */
	public List<Course> getCourses() throws SQLException {
		if (courses != null) return courses;
		courses = new ArrayList<Course>();
		loadCourses();
		return courses;
	}
	
	/**
	 * Actually loads courses from the database.
	 *
	 * @throws SQLException If loading fails for some reason.
	 */
	private void loadCourses() throws SQLException {
		String string = "SELECT * FROM course;";
		Connection connection = db.get();
		Statement statement = connection.createStatement();
		statement.execute(string);
		
		ResultSet res = statement.getResultSet();
		while (res.next()) {
			String code = res.getString("code");
			String name = res.getString("name");
//			int number = res.getInt("number");
			String type = res.getString("type");
			int credits = res.getInt("credits");
			String description = res.getString("description");
			String prereqs = res.getString("prereqs");
			String coreqs = res.getString("coreqs");
			int flag = res.getInt("flag");
			String parents = res.getString("parents");
			
			courses.add(new Course(
					name,
					code.replace("-", " "),
					type,
					credits,
					description,
					code,
					Arrays.stream(prereqs.split(",")).filter(s -> !s.isBlank()).collect(Collectors.toList()),
					Arrays.stream(coreqs.split(",")).filter(s -> !s.isBlank()).collect(Collectors.toList()),
					flag,
					parents
				));
		}
	}
	
	public Database getDatabase() {
		return db;
	}
	
}
