package CourseScheduler;

import java.util.List;

public class Course {
	
	/**
	 * The full name of the course, eg "Software Engineering"
	 */
	final String fullName;
	/**
	 * The shortened name of the course, eg "CS 321"
	 */
	final String name;
	/**
	 * The type of the course, eg "CS" or "MATH" etc
	 */
	final String type;
	/**
	 * The number of credits that this course is worth
	 */
	final int credits;
	/**
	 * The description of the course from the catalog
	 */
	final String desc;
	/**
	 * The informal code for the course, which combines the type and number, eg "CS321"
	 */
	final String code;
	/**
	 * A list of course codes that are immediate prerequisites for this course. This list might overlap
	 * with some of the codes in coreqs.
	 */
	final List<String> prereqs;
	/**
	 * A list of course codes that are required, but can be taken alongside this course
	 */
	final List<String> coreqs;
	/**
	 * An internal representation of flags used to process the course in the algorithm
	 */
	int flag;
	
	public Course(String fullName, String name, String type, int credits, String desc, String code, List<String> prereqs, List<String> coreqs, int flag) {
		this.fullName = fullName;
		this.name = name;
		this.type = type;
		this.credits = credits;
		this.desc = desc;
		this.code = code;
		this.prereqs = prereqs;
		this.coreqs = coreqs;
		this.flag = flag;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public int getCredits() {
		return credits;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public String getCode() {
		return code;
	}
	
	public List<String> getPrereqs() {
		return prereqs;
	}
	
	public List<String> getCoreqs() {
		return coreqs;
	}
	
	public int getFlag() {
		return flag;
	}
	
	public Course setFlag(int flag) {
		this.flag = flag;
		return this;
	}
	
}