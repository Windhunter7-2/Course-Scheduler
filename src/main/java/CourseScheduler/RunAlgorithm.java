package CourseScheduler;

import java.util.ArrayList;
import java.util.List;

public class RunAlgorithm {
	
	/**
	 * This is the modified adjacency list of all the courses that the current Profile put as "needed" courses. The root
	 * node, "Graduation", is the first Course in the array. (Note that order doesn’t matter for the other Courses) The
	 * "linked lists" of this modified adjacency list are the prerequisites listed in the Course node, for each Course.
	 */
	private Course[] adjacencyList;

	/**
	 * This is the number of "levels" of prerequisites each Course in the modified adjacency list has. Note that the
	 * indices match up exactly with the adjacency list indices.
	 */
	private int [] coursePrereqCounts;

	/**
	 * This is simply the sum of coursePrereqCounts.
	 */
	private int totalCount;

	/**
	 * This is the list of *ordered* Courses to be returned to the caller of runAlgorithm. getPrereq() adds the Courses
	 * in the proper order to this array.
	 */
	private Course [] orderedCourseList;

	/**
	 * This is the current total number of *non-null* Courses in orderedCourseList.
	 */
	private int orderedListCount;

	/**
	 * This is a list of the Courses that are "deleted" *only* from the or flags. This is part of what's used in determining
	 * which Courses should be skipped over in getPrereq() for the "Graduation" Course.
	 */
	private List<Course> removedCourses;

    /**
     * Sets all the Courses from the given list as the Courses for every index after 0 in adjacencyList,
     * as well as calls setPrereqCounts() to set the “level” counts for all those courses.
     * It then adds a root “Graduation” Course at index 0, and for its prerequisites,
     * gives all the other Courses in the adjacencyList. It also initializes orderedCourseList,
     * to have the same number of slots as the length of List<Course>, and initializes those slots to null.
     * In addition, it also initiates orderedListCount to start as 0.
     * @param courseList The list of courses the user has indicated they want to take.
     */
	public void setNodesList(List<Course> courseList) {
		//Initialize adjacencyList and coursePrereqCounts
	    this.adjacencyList = new Course[courseList.size() + 1];
	    this.coursePrereqCounts = new int[courseList.size() + 1];
	    //Fill coursePrereqCounts with -1's for comparison in setPrereqCounts()
	    for(int i = 0; i < this.coursePrereqCounts.length; i++) {
	    	coursePrereqCounts[i] = -1;
		}
	    //Creates a List of Strings with data akin to courseList
	    ArrayList<String> gradPrereqs = new ArrayList<String>();
	    for(int j = 0; j < courseList.size(); j++) {
	    	gradPrereqs.add(courseList.get(j).getCode());
		}
	    //Create a new "Course": graduation. Set it to the 0th index of adjacencyList.
	    Course graduation = new Course("Goal Completed", "Graduation", "GRAD",
                120, "All requisite courses completed. Congratulations!", "GRAD-999",
                gradPrereqs, new ArrayList<String>(), 1234567890, "Everything");
	    this.adjacencyList[0] = graduation;
	    //Populate adjacencyList with the Courses from courseList. Once done, call setPrereqCounts().
	    for(int k = 1; k < courseList.size() + 1; k++) {
	        this.adjacencyList[k] = courseList.get(k - 1);
        }
		this.setPrereqCounts();
	    //Prepare orderedCourseList for later population.
        orderedCourseList = new Course[courseList.size()];
	    for(int l = 0; l < orderedCourseList.length; l++) {
	        orderedCourseList[l] = null;
        }
	    orderedListCount = 0;
	    removedCourses = new ArrayList<Course>();
    }

	/**
	 * This takes a flag and, if it's an or parent flag, returns *which* or parent flag it is. Otherwise, it returns -1.
	 * @param flag The flag to check
	 * @return newFlag Which parent flag it is (Or -1, if not applicable)
	 */
	private int parentFlag(int flag)
	{
		int newFlag = Math.abs(flag / 1000);
		if (newFlag != 0)
			return newFlag;
		else
			return -1;
	}
	
	/**
	 * This takes a flag and, if it's an or child flag, returns *which* or child flag it is. Otherwise, it returns -1.
	 * @param flag The flag to check
	 * @return newFlag Which child flag it is (Or -1, if not applicable)
	 */
	private int childFlag(int flag)
	{
		int absFlag = Math.abs(flag);
		int isParentFlag = parentFlag(absFlag);
		int subtractionValue = 0;
		if (isParentFlag != 0)
		{
			int temp = (absFlag / 1000);
			subtractionValue = (temp * 1000);
		}
		int newFlag = (absFlag - subtractionValue);
		newFlag = (newFlag / 10);
		if (newFlag != 0)
			return newFlag;
		else
			return -1;
	}
	
	/**
	 * This takes a flag and, if it's a corequisite flag, returns -1. Otherwise, it returns 1. (Or 0 for the 0 flag)
	 * @param flag
	 * @return newFlag The flag to return
	 */
	private int coreqFlag(int flag)
	{
		int newFlag = 0;
		if ( (flag < 0) || (Math.abs(flag) == 9999) )
			newFlag = -1;
		else if (flag == 0)
			newFlag = 0;
		else if (flag > 0)
			newFlag = 1;
		return newFlag;
	}
	
	/**
	 * This is a helper method for getPrereq() that specifically is for dealing with the various flags. (AKA for different
	 * flags, this method is for the conditionals of the different flags, to return the Course most likely to be chosen,
	 * based on the particular flag. It also converts the returned Course into the index of that Course, for ease of use.
	 * @param parentFlag The flag of the parent Course
	 * @param numPrereqs The number of prerequisites of the parent Course
	 * @param parentIndex The index of the parent Course
	 * @param childA The name of the first child Course being compared
	 * @param childB The name of the second child Course being compared
	 * @return returnedChildIndex The child returned, converted to that child's index in adjacencyList
	 */
	private int getPrereq_flagHelper(int parentFlag, int numPrereqs, int parentIndex, String childA, String childB)
	{
		//Set Important Local Variables
		int returnedChildIndex = -1;
		Course childCourseA = null;
		Course childCourseB = null;
		int childIndexA = -1;
		int childIndexB = -1;
		
		//Convert Strings to Courses/Indices
		for (int i = 0; i < adjacencyList.length; i++)
		{
			String testName = adjacencyList[i].getName();
			if ( testName.equals(childA) )
			{
				childCourseA = adjacencyList[i];
				childIndexA = i;
			}
			if ( testName.equals(childB) )
			{
				childCourseB = adjacencyList[i];
				childIndexB = i;
			}
		}
		
		//Case for childA = childB
		if ( childA.equals(childB) )
			return childIndexA;
		
		//Case for Handling "null"
		if ( (childA.equals("null")) || (childCourseA == null) )
		{
			if ( (childB.equals("null")) || (childCourseB == null) )
				return -1;
			else
				return childIndexB;
		}
		else if ( (childB.equals("null")) || (childCourseB == null) )
			return childIndexA;
		
		//Corequisite Flag
		if ( (parentFlag < -1) && (numPrereqs == 1) )
		{
			if (childCourseA.getFlag() < -1)
				return childIndexA;
			else
				return childIndexB;
		}
		
		//ALL Other Flags
		int flagA = childCourseA.getFlag();
		int flagB = childCourseB.getFlag();
		int isParentFlag = parentFlag(parentFlag);
		int isChildFlagA = childFlag(flagA);
		int isChildFlagB = childFlag(flagB);
		if ( (isParentFlag == 9) && (Math.abs(parentFlag) == 9999) )
			isParentFlag = -1;
		if (isParentFlag == -1)
		{
			isChildFlagA = -1;
			isChildFlagB = -1;
		}
		if ( isParentFlag != isChildFlagA )
		{
			if ( isParentFlag != isChildFlagB )
				return -1;
			else
				return childIndexB;
		}
		else if ( isParentFlag != isChildFlagB )
			return childIndexA;
		int countA = coursePrereqCounts[childIndexA];
		int countB = coursePrereqCounts[childIndexB];
		
		//Standard And Flag
		if ( (parentFlag == 0) || (Math.abs(parentFlag) == 9999) )
		{
			if ( (Math.abs(flagA) == 9999) || (Math.abs(flagB) == 9999) )
			{
				if (Math.abs(flagA) == 9999)
					countA = -1;
				else if (Math.abs(flagB) == 9999)
					countB = -1;
			}
			if (countA > countB)
				returnedChildIndex = childIndexA;
			else
				returnedChildIndex = childIndexB;
		}
		
		//Check for Count of 0 (And Enable Compatibility with Ors Still)
		if (coursePrereqCounts[childIndexA] == 0)
		{
			if (coursePrereqCounts[childIndexB] == 0)
				return -1;
			else
				countB = -1;
		}
		else if (coursePrereqCounts[childIndexB] == 0)
			countA = -1;

		//Or Flag (Same for ALL Or Flags)
		if (Math.abs(parentFlag) > 0)
		{
			//Check Childs of Childs
			int isParentFlagA = parentFlag(flagA);
			int isParentFlagB = parentFlag(flagB);
			if (countA == countB)
			{
				if (isParentFlagA == -1)
				{
					if (isParentFlagB != -1)
						countB = -1;
				}
				else if (isParentFlagB == -1)
					countA = -1;
			}
			
			//Pick Left Child
			if (countA < countB)
			{
				//Set That the Child Is Picked
				returnedChildIndex = childIndexA;
				//Remove the Or Not Selected (If Applicable)
				if ( (isChildFlagA == isChildFlagB) && (!childA.equals(childB)) )
					adjacencyList[parentIndex].getPrerequisites().remove(childB);
			}
			
			//Pick Right Child
			else
			{
				//Set That the Child Is Picked
				returnedChildIndex = childIndexB;
				//Remove the Or Not Selected (If Applicable)
				if ( (isChildFlagA == isChildFlagB) && (!childA.equals(childB)) )
					adjacencyList[parentIndex].getPrerequisites().remove(childA);
			}
			//Reset the Flags (If Needed)
			flagCheck(parentIndex);
		}
		
		//Standard Return
		return returnedChildIndex;
	}
	
	/**
	 * This is a helper method for getPrereq(). It lowers the count of the current Course.
	 * @param indexOfChild The index of the Course to lower the count of
	 * @param indexOfParent The index of the parent Course
	 */
	private void lowerCount(int indexOfChild, int indexOfParent)
	{
		//For Errors
		if (indexOfChild == -1)
			return;
		
		//"Lower Count" (Actual Lowering Done in Main Part of getPrereq())
		if ( !removedCourses.contains(adjacencyList[indexOfChild]) )
			removedCourses.add(adjacencyList[indexOfChild]);
		if (indexOfParent != -1)
		{
			if ( adjacencyList[indexOfParent].getPrerequisites().contains(adjacencyList[indexOfChild].getName()) )
				adjacencyList[indexOfParent].getPrerequisites().remove( adjacencyList[indexOfChild].getName() );
		}
		
		//Special Alt Case for Graduation
		if (indexOfParent == -1)
			return;
	}
	
	/**
	 * This is a helper method for getPrereq_flagHelper(). It checks if the parent flag needs to be changed, and if so, changes it.
	 * @param indexOfParent The index of the parent Course
	 */
	private void flagCheck(int indexOfParent)
	{
		//Variables
		Course parentCourse = adjacencyList[indexOfParent];
		int parentFlag = getFlag(adjacencyList[indexOfParent]);
		int isParentFlag = parentFlag(parentFlag);
		int isChildFlag = -1;
		int maxFlag = 0;
		boolean moreOrFlags = false;
		List<Integer> orFlags = new ArrayList<Integer>();
		
		//Find "Max" Flag (AKA Or Flag -> Corequisite Flag -> And Flag
		for (int i = 0; i < parentCourse.getPrerequisites().size(); i++)
		{
			int index = courseToIndex(parentCourse.getPrerequisites().get(i));
			isChildFlag = childFlag(adjacencyList[index].getFlag());
			//If Other Ors of Same Type Found
			if (isParentFlag == isChildFlag)
			{
				if (moreOrFlags)
					return;
				if (!moreOrFlags)
					moreOrFlags = true;
				continue;
			}
			int isCoreqFlag = coreqFlag(adjacencyList[index].getFlag());
			//If Corequisite and Last, Break
			if ( (isCoreqFlag == -1) && (parentCourse.getPrerequisites().size() == 1) )
			{
				maxFlag = -9999;
				break;
			}
			//If 0 Or Corequisite (Non-Last), Skip
			if ( (isCoreqFlag == 0) || (isCoreqFlag == -1) )
				continue;
			//Else, Set to *That* Or (If at Least 2)
			else
			{
				//More Than 1 of That Flag Type; Change
				if ( orFlags.contains(isChildFlag) )
				{
					maxFlag = isChildFlag;
					if (parentFlag < 0)
						maxFlag *= -1;
					maxFlag *= 1000;
					break;
				}
				//Only 1 of That Flag Type; Add
				if ( orFlags.isEmpty() )
					orFlags.add(isChildFlag);
			}
		}
		setFlag(parentCourse, maxFlag);
	}
	
	/**
	 * This is the main portion of the algorithm. It does a depth-first traversal on the given Course, adds that Course to
	 * orderedCourseList, and "removes" it from adjacencyList via lowering the "count" to below 1.
	 * @param recursCourse The Course currently being recursively called for the method
	 * @return null The return type was only for a failsafe/backdoor, it's not technically needed
	 */
	private Course getPrereq(Course recursCourse)
	{
		//Case for When recursCourse Is Graduation
		if (recursCourse.getFlag() == 1234567890)
		{
			//Determine Current Highest Count Course
			int indexHighest = 0;
			int highestCount = 0;
			for (int i = 0; i < adjacencyList.length; i++)
			{
				//Skip "Removed" Courses (Count of 0)
				if (coursePrereqCounts[i] == 0)
					continue;
				//Set Count of "Removed" Courses to 0 (And Remove from List)
				if ( removedCourses.contains(adjacencyList[i]) )
				{
					int amountToLower = coursePrereqCounts[i];
					String nameSub = adjacencyList[i].getName();
					//Remove Copies from Any Courses with It As a Prerequisite
					for (int j = 0; j < adjacencyList.length; j++)
					{
						if ( adjacencyList[j].getPrerequisites().contains(nameSub) )
							adjacencyList[j].getPrerequisites().remove(nameSub);
					}
					coursePrereqCounts[i] = 0;
					totalCount -= amountToLower;
					removedCourses.remove(adjacencyList[i]);
					continue;
				}
				//Checks Count & Sets if Applicable
				if (coursePrereqCounts[i] > highestCount)
				{
					indexHighest = i;
					highestCount = coursePrereqCounts[i];
				}
			}
			//Adds Highest Index Course When Applicable
			List<String> childPrereqs_Root = adjacencyList[indexHighest].getPrerequisites();
			if ( childPrereqs_Root.isEmpty() )
			{
				//Make Sure totalCount = 0 at All Courses Complete
				if ( ( adjacencyList[indexHighest].getName().equals("Graduation") )
						|| (orderedListCount == (orderedCourseList.length-1)) )
				{
					totalCount = 0;
					return null;
				}
				//Remove Removed Course from Graduation
				Course removedCourse = adjacencyList[indexHighest];
				adjacencyList[0].getPrerequisites().remove( removedCourse.getName() );
				//Lower Count & Add to orderedCourseList
				orderedCourseList[orderedListCount] = adjacencyList[indexHighest];
				orderedListCount++;
				lowerCount(indexHighest, -1);
				return adjacencyList[indexHighest];
			}
			//Execute getPrereq() for Highest Count
			if (indexHighest != 0)
				getPrereq(adjacencyList[indexHighest]);
			//Return from Graduation Course so runAlgorithm() Can Run It Again
			return null;
		}
		
		//Set Variables
		int parentIndex = courseToIndex(recursCourse.getName());
		int parentFlag = getFlag(recursCourse);
		String childA = "";
		String childB = "";
		int indexChosen = -1;
		int numPrereqs = recursCourse.getPrerequisites().size();
		List<String> prereqs = recursCourse.getPrerequisites();
		
		//Loop Through to Find Appropriate Course
		childA = prereqs.get(0);
		if (prereqs.size() > 1)
			childB = prereqs.get(1);
		else
			childB = childA;
		if (prereqs.size() == 1)
			indexChosen = getPrereq_flagHelper(parentFlag, numPrereqs, parentIndex, childA, childB);
		for (int i = 0; i < (numPrereqs - 1); i++)
		{
			int numPrereqs_Origin = numPrereqs;		//Copy of Original numPrereqs Before Editing
			indexChosen = getPrereq_flagHelper(parentFlag, numPrereqs, parentIndex, childA, childB);
			//Check if an Or Was Removed; If So, Return
			numPrereqs = recursCourse.getPrerequisites().size();
			if (numPrereqs < numPrereqs_Origin)
				return null;
			//For Case of -1 Returned from indexChosen
			if (indexChosen == -1)
				childA = "null";
			//Set childA
			else
				childA = adjacencyList[indexChosen].getName();
			//Set childB
			if ( (i != (numPrereqs - 2)) && (numPrereqs > 2) )
				childB = prereqs.get(i + 2);
		}
		if (indexChosen == -1)	//For if the Course Isn't Needed
			return null;
		
		//No Children Left; Base Case
		List<String> childPrereqs = adjacencyList[indexChosen].getPrerequisites();
		if ( childPrereqs.isEmpty() )
		{
			//Remove All Occurrences of the Removed Course
			Course removedCourse = adjacencyList[indexChosen];
			for (int i = 0; i < adjacencyList.length; i++)
			{
				if ( adjacencyList[i].getPrerequisites().contains(removedCourse.getName()) )
					adjacencyList[i].getPrerequisites().remove(removedCourse.getName());
			}
			//Lower Count & Add to orderedCourseList
			orderedCourseList[orderedListCount] = adjacencyList[indexChosen];
			orderedListCount++;
			lowerCount(indexChosen, parentIndex);
			return adjacencyList[indexChosen];
		}
		
		//Children Left; Recursive Case
		else if ( !childPrereqs.isEmpty() )
			getPrereq(adjacencyList[indexChosen]);
		
		//Return Statement for Errors
		return null;
	}

	private void setPrereqCounts() {
		//Track totalCount as we step through Courses.
		totalCount = 0;
		//Add to totalCount, and find prereq counts for all courses missing one.
		for(int i = 0; i < adjacencyList.length; i++) {
			if(coursePrereqCounts[i] == -1) {
				this.setPrereqCount(i);
			}
			totalCount += coursePrereqCounts[i];
		}
	}

	/**
	 * This gets the flag of the given Course.
	 * @param course The course from which to return the flag.
	 * @return The flag from the given course as an integer.
	 */
	private int getFlag(Course course) {
		return course.getFlag();
	}

	/**
	 * This sets the flag of the given Course to the given number.
	 * @param course The course to which the new flag will be set.
	 * @param newFlag The new flag of the given course.
	 */
	private void setFlag(Course course, int newFlag) {
		course.setFlag(newFlag);
	}
}
