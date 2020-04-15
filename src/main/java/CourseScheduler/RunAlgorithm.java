package CourseScheduler;

import java.util.ArrayList;
import java.util.LinkedHashSet;
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
	 * This is a temporary list of Courses that are "deleted" *only* from the or flags, and *only* involving the recursive
	 * part. This is used to determine if the child of a parent should be removed or not when the parent is removed.
	 */
	private List<Course> tempRemovedCourses;
	
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
		if (flag < 0)
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
			String testName = adjacencyList[i].name;
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
			if (childCourseA.flag < -1)
				return childIndexA;
			else
				return childIndexB;
		}
		
		//ALL Other Flags
		int flagA = childCourseA.flag;
		int flagB = childCourseB.flag;
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
		if ( (Math.abs(parentFlag) == 0) || (Math.abs(parentFlag) == 10) || (Math.abs(parentFlag) == 9999) )
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
		
		//Or Flag (Same for ALL Or Flags)
		else if ( (Math.abs(parentFlag) == 1000) || (Math.abs(parentFlag) == 1010) )
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
				//Remove One of the Ors, if Applicable
				if ( (isChildFlagA == isChildFlagB) && (!childA.equals(childB)) )
				{
					for (int i = 0; i < adjacencyList.length; i++)
					{
						int isParentFlagSub = parentFlag(adjacencyList[i].flag);
						//For Every Occurrence of the Child As an Or
						if ( (adjacencyList[i].prerequisites.contains(childB)) && (isParentFlagSub == isChildFlagB) )
						{
							//Remove from That Occurrence
							adjacencyList[i].prerequisites.remove(childB);
							int childIndex = courseToIndex(childB);
							//If It Has Children, Remove *Non-Used* Children
							if (coursePrereqCounts[childIndex] != 0)
							{
								//Reset tempRemovedCourses and Add to It *Non-Used* Children
								tempRemovedCourses.clear();
								tempRemovedCourses.add(adjacencyList[childIndex]);
								lowerCount(childIndex, childIndex);
								//Check if Parent Index Should Actually Be Added; Remove if Not
								String nameSub = adjacencyList[childIndex].name;
								for (int j = 1; j < adjacencyList.length; j++)
								{
									//Skip Over Current Slot (And Over Any "Removed" Temp Slots)
									if ( (j == childIndex) || (tempRemovedCourses.contains(adjacencyList[j])) )
										continue;
									//If an Occurrence of !0 Found, Mark As Found (true)
									if ( (coursePrereqCounts[j] != 0) && (adjacencyList[j].prerequisites.contains(nameSub)) )
										tempRemovedCourses.remove(adjacencyList[childIndex]);
								}
								//Remove Duplicates
								LinkedHashSet<Course> tempHashSet = new LinkedHashSet<Course>(tempRemovedCourses);
								List<Course> tempNoDuplicates = new ArrayList<Course>(tempHashSet);
								//Add All Non-Used Children to otherRemovedCourses
								removedCourses.addAll(tempNoDuplicates);
							}
							//Reset the Flags (If Needed)
							if (i != parentIndex)
								flagCheck(i);
						}
					}
					//Add the Child to otherRemovedCourses
					lowerCount(childIndexB, parentIndex);
				}
			}
			
			//Pick Right Child
			else
			{
				//Set That the Child Is Picked
				returnedChildIndex = childIndexB;
				//Remove One of the Ors, if Applicable
				if ( (isChildFlagA == isChildFlagB) && (!childA.equals(childB)) )
				{
					for (int i = 0; i < adjacencyList.length; i++)
					{
						int isParentFlagSub = parentFlag(adjacencyList[i].flag);
						//For Every Occurrence of the Child As an Or
						if ( (adjacencyList[i].prerequisites.contains(childA)) && (isParentFlagSub == isChildFlagA) )
						{
							//Remove from That Occurrence
							adjacencyList[i].prerequisites.remove(childA);
							int childIndex = courseToIndex(childA);
							//If It Has Children, Remove *Non-Used* Children
							if (coursePrereqCounts[childIndex] != 0)
							{
								//Reset tempRemovedCourses and Add to It *Non-Used* Children
								tempRemovedCourses.clear();
								tempRemovedCourses.add(adjacencyList[childIndex]);
								lowerCount(childIndex, childIndex);
								//Check if Parent Index Should Actually Be Added; Remove if Not
								String nameSub = adjacencyList[childIndex].name;
								for (int j = 1; j < adjacencyList.length; j++)
								{
									//Skip Over Current Slot (And Over Any "Removed" Temp Slots)
									if ( (j == childIndex) || (tempRemovedCourses.contains(adjacencyList[j])) )
										continue;
									//If an Occurrence of !0 Found, Mark As Found (true)
									if ( (coursePrereqCounts[j] != 0) && (adjacencyList[j].prerequisites.contains(nameSub)) )
										tempRemovedCourses.remove(adjacencyList[childIndex]);
								}
								//Remove Duplicates
								LinkedHashSet<Course> tempHashSet = new LinkedHashSet<Course>(tempRemovedCourses);
								List<Course> tempNoDuplicates = new ArrayList<Course>(tempHashSet);
								//Add All Non-Used Children to otherRemovedCourses
								removedCourses.addAll(tempNoDuplicates);
							}
							//Reset the Flags (If Needed)
							if (i != parentIndex)
								flagCheck(i);
						}
					}
					//Add the Child to otherRemovedCourses
					lowerCount(childIndexA, parentIndex);
				}
			}
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
		
		//Recursive Lowering; indexOfParent = Actual
		if (indexOfChild == indexOfParent)
		{
			int numChildren = adjacencyList[indexOfParent].prerequisites.size();
			for (int i = 0; i < numChildren; i++)
			{
				//Sub Variables
				String nameSub = adjacencyList[indexOfParent].prerequisites.get(i);
				int indexSub = courseToIndex(nameSub);
				int numChildrenSub = adjacencyList[indexSub].prerequisites.size();
				boolean nameSubKept = false;
				for (int j = 1; j < adjacencyList.length; j++)
				{
					//Skip Over Current Slot (And Over Any "Removed" Temp Slots)
					if ( (j == indexOfParent) || (tempRemovedCourses.contains(adjacencyList[j])) )
						continue;
					//If an Occurrence of !0 Found, Mark As Found (true)
					if ( (coursePrereqCounts[j] != 0) && (adjacencyList[j].prerequisites.contains(nameSub)) )
						nameSubKept = true;
				}
				//If Child Used Elsewhere, Ignore Lowering
				if (nameSubKept)
					continue;
				//Base Case
				if (numChildrenSub < 1)
					tempRemovedCourses.add(adjacencyList[indexSub]);
				//Recursive Case
				else
				{
					lowerCount(indexSub, indexSub);
					tempRemovedCourses.add(adjacencyList[indexSub]);
				}
			}
			return;
		}
		
		//"Lower Count" (Actual Lowering Done in Main Part of getPrereq())
		if ( !removedCourses.contains(adjacencyList[indexOfChild]) )
			removedCourses.add(adjacencyList[indexOfChild]);
		
		//Special Alt Case for Graduation
		if (indexOfParent == -1)
			return;
		
		//Remove Child Course
		Course parentCourse = adjacencyList[indexOfParent];
		for (int i = 0; i < parentCourse.prerequisites.size(); i++)
		{
			String currentChild = parentCourse.prerequisites.get(i);
			if ( currentChild.equals(adjacencyList[indexOfChild].name) )
				parentCourse.prerequisites.remove(currentChild);
		}
	}
	
	/**
	 * This is a helper method for getPrereq_flagHelper(). It checks if the parent flag needs to be changed, and if so, changes it.
	 * @param indexOfParent The index of the parent Course
	 */
	private void flagCheck(int indexOfParent)
	{
		//Variables
		Course parentCourse = adjacencyList[indexOfParent];
		boolean duplicateFlagFoundA = false;
		boolean duplicateFlagFoundB = false;
		int parentFlag = getFlag(adjacencyList[indexOfParent]);
		int isParentFlag = parentFlag(parentFlag);
		int isChildFlag = -1;
		int maxFlag = 0;
		
		//Find Any Flag of Same Type
		for (int i = 0; i < parentCourse.prerequisites.size(); i++)
		{
			int index = courseToIndex(parentCourse.prerequisites.get(i));
			isChildFlag = childFlag(adjacencyList[index].flag);
			//Flag of Parent Type Found
			if (isParentFlag == isChildFlag)
			{
				if (!duplicateFlagFoundA)
					duplicateFlagFoundA = true;
				else if (duplicateFlagFoundA)
					duplicateFlagFoundB = true;
			}
		}
		
		//No More Flags of the Type Exist
		if (!duplicateFlagFoundB)
		{
			//Find "Max" Flag (AKA Or Flag -> Corequisite Flag -> And Flag
			for (int i = 0; i < parentCourse.prerequisites.size(); i++)
			{
				int index = courseToIndex(parentCourse.prerequisites.get(i));
				isChildFlag = childFlag(adjacencyList[index].flag);
				//Skip Flag Being Removed (And Remove It)
				if (isParentFlag == isChildFlag)
				{
					adjacencyList[index].flag = 0;
					continue;
				}
				int isCoreqFlag = coreqFlag(adjacencyList[index].flag);
				//If Corequisite and Last, Break
				if ( (isCoreqFlag == -1) && (parentCourse.prerequisites.size() == 1) )
				{
					maxFlag = -9999;
					break;
				}
				//If 0 Or Corequisite (Non-Last), Skip
				if ( (isCoreqFlag == 0) || (isCoreqFlag == -1) )
					continue;
				//Else, Set to *That* Or
				else
				{
					maxFlag = isChildFlag;
					if (parentFlag < 0)
						maxFlag *= -1;
					maxFlag *= 1000;
					break;
				}
			}
			System.out.println("	Set flag for " + parentCourse.name + " as " + maxFlag);
			setFlag(parentCourse, maxFlag);
		}
	}
	
	/**
	 * This is the main portion of the algorithm. It does a depth-first traversal on the given Course, adds that Course to
	 * orderedCourseList, and "removes" it from adjacencyList via lowering the "count" to below 1.
	 * @param recurseCourse The Course currently being recursively called for the method
	 * @return null The return type was only for a failsafe/backdoor, it's not technically needed
	 */
	private Course getPrereq(Course recursCourse)
	{
		//Case for When recursCourse Is Graduation
		if (recursCourse.flag == 1234567890)
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
			//Execute getPrereq() for Highest Count
			if (indexHighest != 0)
				getPrereq(adjacencyList[indexHighest]);
			//Return from Graduation Course so runAlgorithm() Can Run It Again
			return null;
		}
		
		//Set Variables
		int parentIndex = courseToIndex(recursCourse.name);
		int parentFlag = getFlag(recursCourse);
		String childA = "";
		String childB = "";
		int indexChosen = -1;
		int numPrereqs = recursCourse.prerequisites.size();
		List<String> prereqs = recursCourse.prerequisites;
		
		//Alternate Base Case (Graduation Base Case)
		if (prereqs.size() < 1)
		{
			//Remove All Occurrences of the Removed Course
			for (int i = 0; i < adjacencyList.length; i++)
			{
				if ( adjacencyList[i].prerequisites.contains(recursCourse.name) )
					adjacencyList[i].prerequisites.remove(recursCourse.name);
			}
			//Lower Count & Add to orderedCourseList
			orderedCourseList[orderedListCount] = recursCourse;
			orderedListCount++;
			lowerCount(parentIndex, -1);
			return null;
		}
		
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
			String childA_Origin = childA;	//Copy of Original childA Before Editing
			indexChosen = getPrereq_flagHelper(parentFlag, numPrereqs, parentIndex, childA, childB);
			//For Case of -1 Returned from indexChosen
			if (indexChosen == -1)
				childA = "null";
			//Set childA (Normal)
			else
				childA = adjacencyList[indexChosen].name;
			//Set childB
			if ( (i != (numPrereqs - 2)) && (numPrereqs > 2) )
			{
				//Get Or Condition
				int childALoc = courseToIndex(childA_Origin);
				int childBLoc = courseToIndex(childB);
				int isChildFlagA = childFlag(adjacencyList[childALoc].flag);
				int isChildFlagB = childFlag(adjacencyList[childBLoc].flag);
				//If ONLY Both of the Children Are Ors
				if ( (isChildFlagA != -1) && (isChildFlagB != -1) )
				{
					i--;
					numPrereqs--;
				}
				childB = prereqs.get(i + 2);
				//Reset Flag (For Certain Conditions)
				parentFlag = getFlag(recursCourse);
			}
		}
		if (indexChosen == -1)	//For if the Course Isn't Needed
			return null;
		//Flag Check for Ors
		flagCheck(parentIndex);
		
		//No Children Left; Base Case
		List<String> childPrereqs = adjacencyList[indexChosen].prerequisites;
		if ( childPrereqs.isEmpty() )
		{
			//Remove All Occurrences of the Removed Course
			Course removedCourse = adjacencyList[indexChosen];
			for (int i = 0; i < adjacencyList.length; i++)
			{
				if ( adjacencyList[i].prerequisites.contains(removedCourse.name) )
					adjacencyList[i].prerequisites.remove(removedCourse.name);
			}
			//Lower Count & Add to orderedCourseList
			orderedCourseList[orderedListCount] = adjacencyList[indexChosen];
			orderedListCount++;
			lowerCount(indexChosen, parentIndex);
			if ( (adjacencyList[parentIndex].flag != 0) && (adjacencyList[parentIndex].flag != Math.abs(9999)) )
				flagCheck(parentIndex);
			return adjacencyList[indexChosen];
		}
		
		//Children Left; Recursive Case
		else if ( !childPrereqs.isEmpty() )
			getPrereq(adjacencyList[indexChosen]);
		
		//Return Statement for Errors
		return null;
	}

}
