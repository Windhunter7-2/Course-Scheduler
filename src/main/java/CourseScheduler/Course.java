package CourseScheduler;

import java.util.List;
import java.util.Objects;

public class Course {
	
	/**
	 * The full name of the course, eg "Software Engineering"
	 */
	private final String fullName;
	
	/**
	 * The shortened name of the course, eg "CS 321"
	 */
	private String name;
	
	/**
	 * The type of the course, eg "CS" or "MATH" etc
	 */
	private final String type;
	
	/**
	 * The number of credits that this course is worth
	 */
	private final int credits;
	
	/**
	 * The description of the course from the catalog
	 */
	private final String desc;
	
	/**
	 * The informal code for the course, which combines the type and number, eg "CS-321"
	 */
	private String code;
	
	/**
	 * A list of course codes that are immediate prerequisites for this course. This list might overlap
	 * with some of the codes in coreqs.
	 */
	private final List<String> prerequisites;
	
	/**
	 * A list of course codes that are required, but can be taken alongside this course
	 */
	private final List<String> coreqs;
	
	/**
	 * An internal representation of flags used to process the course in the algorithm
	 */
	private int flag;
	
	/**
	 * A string representation of the parents, eg "(CS-110&MATH-125)|(MATH-110)"
	 */
	private String parents;
	
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
	
	public Course(Course other) {
		this.fullName = other.fullName;
		this.name = other.name;
		this.type = other.type;
		this.credits = other.credits;
		this.desc = other.desc;
		this.code = other.code;
		this.prerequisites = other.prerequisites;
		this.coreqs = other.coreqs;
		this.flag = other.flag;
		this.parents = other.parents;
	}

	public String getFullName() {
		return fullName;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
	
	public void setCode(String code) {
		this.code = code;
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
	
	public void setParents(String parents) {
		this.parents = parents;
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
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Course course = (Course) o;
		return credits == course.credits &&
				flag == course.flag &&
				Objects.equals(fullName, course.fullName) &&
				Objects.equals(name, course.name) &&
				Objects.equals(type, course.type) &&
				Objects.equals(desc, course.desc) &&
				Objects.equals(code, course.code) &&
				Objects.equals(prerequisites, course.prerequisites) &&
				Objects.equals(coreqs, course.coreqs) &&
				Objects.equals(parents, course.parents);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fullName, name, type, credits, desc, code, prerequisites, coreqs, flag, parents);
	}
}