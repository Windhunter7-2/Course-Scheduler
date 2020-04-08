package CourseScheduler;

import java.util.ArrayList;
import java.util.List;

public class Profile {

    public Profile(String Name){
        String name = Name;

    }


    public int setNumCredits(int credits){
        int numcredits= credits;
        return numcredits;
    }


    public int setNumSemesters(int semesters){
        int numSemesters= semesters;
        return numSemesters;
    }
    public List<Course> neededCourses(){
        return new ArrayList<Course>();
    }

    public List<Course> doneCourses(){
        return new ArrayList<Course>();
    }

}