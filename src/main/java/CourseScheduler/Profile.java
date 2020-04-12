package CourseScheduler;

import java.util.ArrayList;
import java.util.List;

public class Profile {
    Database db;
    String name;
    int numcredits;
    int numsemesters;
    ArrayList<String> neededCourses;
    ArrayList<String> doneCourses;
    public Profile(String Name){
         name = Name;
         db= new Database(name);//users database
         numcredits=18;
         numsemesters=8;
    }

    /**
     * sets the user's profile name
     * @param name user's name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    ProfileDB userprofiledb = new ProfileDB(db);

    /**
     * sets user's choice of credits
     * @param credits overall credits.
     */
    public void setNumCredits(int credits){
        this.numcredits= credits;
     }

    /**
     * returns numcredits
     * @return numcredits
     */
    public int getNumcredits() {
        return numcredits;
    }

    /**
     * sets number of semesters
     * @param semesters numsemesters
     */
    public void setNumSemesters(int semesters){
        this.numsemesters= semesters;

    }

    /**
     * number of semesters.
     * @return number of semesters.
     */
    public int getNumsemesters() {
        return numsemesters;
    }

    /**
     * adds new courses to needed list
     * @param course course to add
     * @return list of needed courses.
     */
    public ArrayList<String> addneededCourses(String course){
        neededCourses.add(course);
        return neededCourses;
    }
    /**
     * adds done courses to done list
     * @param course course to add
     * @return list of done courses.
     */
    public ArrayList<String> adddoneCourses(String course){
        doneCourses.add(course);
        return doneCourses;
    }

    /**
     * gets needed courses
     * @return neededcourses
     */
    public ArrayList<String> getNeededCourses() {
        return neededCourses;
    }

    /**
     * gets done courses.
     * @return donecourses
     */
    public ArrayList<String> getDoneCourses() {
        return doneCourses;
    }
}