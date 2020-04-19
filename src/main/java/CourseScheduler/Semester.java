package CourseScheduler;

import java.util.ArrayList;
import java.util.List;

public class Semester {
	
	/**
	 * Constructor
	 */
	public Semester()
	{
		semesterCourses = new ArrayList<Course>();
	}
	
	/**
	 * This is an array (*In order*) of the Courses for a particular semester chosen.
	 */
	private List<Course> semesterCourses;
	
	/**
	 * Getter for Semester
	 * @return semesterCourses
	 */
	public List<Course> getSemester()
	{
		return semesterCourses;
	}

}
