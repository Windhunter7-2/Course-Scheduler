package CourseScheduler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProfileDB {
	
	private Database db;
	
	public ProfileDB(String name) throws IOException, SQLException {
		db = new Database(name);
		db.create();
	}
	
	public ProfileDB() throws IOException, SQLException {
		this("user_profiles");
	}
	
	private static final String PROFILES_TABLE = "profiles";
	private static final String NEEDED_COURSES_TABLE = "needed_courses";
	private static final String DONE_COURSES_TABLE = "done_courses";
	private static final String USER_NAME = "user_name";
	private static final String COURSE_NAME = "course_name";
	private static final String CODE = "code";
	
	/**
	 * creates a table of users.
	 */
	private static final String TABLE_CREATE_PROFILES = "create table if not exists " + PROFILES_TABLE + "(" +
			USER_NAME + " TEXT PRIMARY KEY NOT NULL);";
	
	/**
	 * creates needed courses for a user.
	 */
	private static final String TABLE_CREATE_NEEDED_COURSES = "create table if not exists " + NEEDED_COURSES_TABLE + "(" +
			USER_NAME + " TEXT NOT NULL, " +
			COURSE_NAME + " TEXT, " +
			CODE + " TEXT NOT NULL, " +
            "PRIMARY KEY(" + USER_NAME + "," + CODE + "));";
	
	/**
	 * creates done courses for a user.
	 */
	private static final String TABLE_CREATE_DONE_COURSES = "create table if not exists  " + DONE_COURSES_TABLE + "(" +
            USER_NAME + " TEXT NOT NULL, " +
			COURSE_NAME + " TEXT, " +
			CODE + " TEXT NOT NULL, " +
            "PRIMARY KEY(" + USER_NAME + "," + CODE + "));";
	
	/**
	 * Creates the profiles' necessary information in the database.
	 */
	public ProfileDB create() throws SQLException, IOException {
		Connection connection = db.get();
		Statement statement = connection.createStatement();
		statement.execute(TABLE_CREATE_PROFILES);
		
		createNeededCourses();
		createCompletedCourses();
		
		return this;
	}
	
	/**
	 * Creates the needed Courses necessary information in the database.
	 */
	private void createNeededCourses() throws SQLException, IOException {
		Connection connection = db.get();
		Statement statement = connection.createStatement();
		statement.execute(TABLE_CREATE_NEEDED_COURSES);
	}
	
	/**
	 * Creates the completed Courses necessary information in the database.
	 */
    private void createCompletedCourses() throws SQLException, IOException {
		Statement statement = db.get().createStatement();
		statement.execute(TABLE_CREATE_DONE_COURSES);
	}
	
	public void insertProfile(String name) {
		String sql = "INSERT OR IGNORE INTO profiles(user_name) VALUES(?);";
		try (Connection conn = db.get();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, name);
			pstmt.executeUpdate();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	public void deleteProfile(String name) {
		try (Connection conn = db.get();
			 PreparedStatement st = conn.prepareStatement("DELETE FROM " + PROFILES_TABLE + " WHERE user_name = ?;")) {
			st.setString(1, name);
			st.execute();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}

	}
	public List<String> getProfiles() {
    	String sql = "SELECT user_name FROM profiles;";
		List<String> strs = new ArrayList<>();
		try (Connection conn = db.get(); PreparedStatement st = conn.prepareStatement(sql)) {
			ResultSet res = st.executeQuery();
			int i = 1;
			while (res.next()) {
				strs.add(res.getString(i));
			}
		} catch (SQLException | IOException throwables) {
			throwables.printStackTrace();
		}
		return strs;
	}
	
	public void setNeededCourses(String profile, List<String> codes) {
    	setCoursesTable(NEEDED_COURSES_TABLE, profile, codes);
	}
	
	public void setDoneCourses(String profile, List<String> codes) {
		setCoursesTable(DONE_COURSES_TABLE, profile, codes);
	}
	
	private void setCoursesTable(String table, String profile, List<String> codes) {
		try (Connection conn = db.get();
			 PreparedStatement st = conn.prepareStatement("DELETE FROM " + table + " WHERE user_name = ?;")) {
			st.setString(1, profile);
			st.execute();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
  
		if (codes.isEmpty())
			return;
		
		StringBuilder sql = new StringBuilder("INSERT OR IGNORE INTO " + table + " (user_name, code) VALUES");
		for (String ignored : codes) {
			sql.append("(?, ?),");
		}
		sql.insert(sql.lastIndexOf(","), ";");
		
		int i = 1;
		try (Connection conn = db.get();
			 PreparedStatement st = conn.prepareStatement(sql.toString())) {
			for (String code : codes) {
				st.setString(i++, profile);
				st.setString(i++, code);
			}
			st.execute();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getNeededCourses(String profile) throws SQLException, IOException {
		List<String> neededCourses = new ArrayList<>();
		String sql = "SELECT code FROM needed_courses WHERE user_name = ?;";
		return getStrings(profile, neededCourses, sql);
	}
	
	public List<String> getDoneCourses(String profile) throws SQLException, IOException {
		List<String> doneCourses = new ArrayList<>();
		String sql = "SELECT code FROM done_courses WHERE user_name = ?;";
		return getStrings(profile, doneCourses, sql);
	}
	
	private List<String> getStrings(String profile, List<String> courses, String sql) throws SQLException, IOException {
		PreparedStatement statement = db.get().prepareStatement(sql);
		statement.setString(1, profile);
		ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			String code = rs.getString("code");
			courses.add(code);
		}
		return courses;
	}
	
}
