package CourseScheduler;

import java.util.List;

public class Course {
	
	/**
	 * The full name of the course, eg "Software Engineering"
	 */
	public final String fullName;
	/**
	 * The shortened name of the course, eg "CS 321"
	 */
	public final String name;
	/**
	 * The type of the course, eg "CS" or "MATH" etc
	 */
	public final String type;
	/**
	 * The number of credits that this course is worth
	 */
	public final int credits;
	/**
	 * The description of the course from the catalog
	 */
	public final String desc;
	/**
	 * The informal code for the course, which combines the type and number, eg "CS321"
	 */
	public final String code;
	/**
	 * A list of course codes that are immediate prerequisites for this course. This list might overlap
	 * with some of the codes in coreqs.
	 */
	public final List<String> prerequisites;
	/**
	 * A list of course codes that are required, but can be taken alongside this course
	 */
	final List<String> coreqs;
	/**
	 * An internal representation of flags used to process the course in the algorithm
	 */
	public int flag;
	/**
	 * A string representation of the parents, eg "(CS110 & MATH125) | MATHXYZ"
	 */
	final String parents;
	
	public Course(String fullName, String name, String type, int credits, String desc, String code, List<String> prereqs, List<String> coreqs, int flag, String parents) {
		this.fullName = fullName;
		this.name = name;
		this.type = type;
		this.credits = credits;
		this.desc = desc;
		this.code = code;
		this.prerequisites = prereqs;
		this.coreqs = coreqs;
		this.flag = flag;
		this.parents = parents;
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
	
	public List<String> getPrerequisites() {
		return prerequisites;
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
	
	public String getParents() {
		return parents;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Course{");
		sb.append("fullName='").append(fullName).append('\'');
		sb.append(", name='").append(name).append('\'');
		sb.append(", type='").append(type).append('\'');
		sb.append(", credits=").append(credits);
		sb.append(", desc='").append(desc).append('\'');
		sb.append(", code='").append(code).append('\'');
		sb.append(", prereqs=").append(prerequisites);
		sb.append(", coreqs=").append(coreqs);
		sb.append(", flag=").append(flag);
		sb.append('}');
		return sb.toString();
	}
}