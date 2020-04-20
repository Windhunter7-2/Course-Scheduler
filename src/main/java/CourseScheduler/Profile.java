package CourseScheduler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Profile {
	
	public static ProfileDB db;
	
	private final String name;
	private final List<String> needed;
	private final List<String> done;
	private int numCredits;
	private int numSemesters;
	
	private Profile(String name) {
		this.name = name;
		
		numCredits = 18;
		numSemesters = 8;
		
		needed = new ArrayList<>(); //Jack added this and the below line to aid CourseScheduler.checkListGUI()
		done = new ArrayList<>();
	}
	
	/**
	 * Loads a Profile with the given name, or creates it if it doesn't exist.
	 *
	 * @param name The name of the profile
	 * @return A fully initialized Profile instance
	 */
	public static Profile load(String name) throws SQLException, IOException {
		db.insertProfile(name);
		Profile profile = new Profile(name);
		profile.needed.addAll(db.getNeededCourses(name));
		profile.done.addAll(db.getDoneCourses(name));
		return profile;
	}
	
	/**
	 * Saves this profile back to the database, committing any changes made.
	 */
	public void save() {
		db.setDoneCourses(this.name, this.done);
		db.setNeededCourses(this.name, this.needed);
	}
	
	/**
	 * sets user's choice of credits
	 *
	 * @param credits overall credits.
	 */
	public void setNumCredits(int credits) {
		this.numCredits = credits;
	}
	
	/**
	 * returns numcredits
	 *
	 * @return numcredits
	 */
	public int getNumCredits() {
		return numCredits;
	}
	
	/**
	 * sets number of semesters
	 *
	 * @param semesters numsemesters
	 */
	public void setNumSemesters(int semesters) {
		this.numSemesters = semesters;
		
	}
	
	/**
	 * number of semesters.
	 *
	 * @return number of semesters.
	 */
	public int getNumSemesters() {
		return numSemesters;
	}
	
	/**
	 * gets needed courses
	 *
	 * @return neededcourses
	 */
	public List<String> getNeeded() {
		return needed;
	}
	
	/**
	 * gets done courses.
	 *
	 * @return donecourses
	 */
	public List<String> getDone() {
		return done;
	}
	
}