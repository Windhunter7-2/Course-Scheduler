package CourseScheduler;

import java.util.ArrayList;
import java.util.List;

public class Profile {
    private static int user_id=0;
    String name;
    int numcredits;
    int numsemesters;
    public static ProfileDB user_profiles;
    ArrayList<String> neededCourses;
    ArrayList<String> doneCourses;
    public Profile(String Name){
         name = Name;

         numcredits=18;
         numsemesters=8;
         user_id++;

         neededCourses = new ArrayList<String>(); //Jack added this and the below line to aid CourseScheduler.checkListGUI()
         doneCourses = new ArrayList<String>();
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

    /**
     * insersts a profile into the table of profiles in profileDB.
     */
    public void insertProfileTable(){
        user_profiles.insertProfile(name);
    }

    /**
     * inserts graduation courses associated with user id
     * @param id each user has an id
     * @param code course code
     */
    public void insertMyGradCoursesTable(int id, String code ){
        user_profiles.insertMyGradCourses(id, code);
    }
    /**
     * inserts done courses associated with user id
     * @param id each user has an id
     * @param code course code
     */
    public void insertNeededCoursesTable(int id, String code ){
        user_profiles.insertNeededCourses(id, code);
    }

    /**
     * inserts done courses associated with user id
     * @param id each user has an id
     * @param code course code
     */
    public void insertDoneCoursesTable(int id, String code ){
        user_profiles.insertDoneCourses(id, code);
    }

    /**
     * updaate mygrad  courses associated with user id
     * @param id each user has an id
     * @param code course code
     */
    public void updateMyGradCoursesTable(int id, String code ){
        user_profiles.insertDoneCourses(id, code);
    }
    /**
     * updaate needed courses associated with user id
     * @param id each user has an id
     * @param code course code
     */
    public void updateNeededCoursesTable(int id, String code ){
        user_profiles.insertDoneCourses(id, code);
    }

    /**
     * updaate done courses associated with user id
     * @param id each user has an id
     * @param code course code
     */
    public void updateDoneCoursesTable(int id, String code ){
        user_profiles.insertDoneCourses(id, code);
    }
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
    public int getNumSemesters() {
        return numsemesters;
    }

    /**
     * adds new courses to needed list
     * @param course course to add
     * @return list of needed courses.
     */
    public ArrayList<String> addNeededCourses(String course){
        neededCourses.add(course);
        return neededCourses;
    }
    /**
     * adds done courses to done list
     * @param course course to add
     * @return list of done courses.
     */
    public ArrayList<String> addDoneCourses(String course){
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