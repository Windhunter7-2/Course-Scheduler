package CourseScheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ProfileDB  {
    private  Database profiledb;
    Connection  connection;
     Statement statement;

    private  ProfileDB() throws SQLException {
        profiledb = new Database("User Profiles");
        connection = profiledb.get();
        statement = connection.createStatement();
    }
    private static final String PROFILES_TABLE="Profiles";
    private static final String MY_GRAD_COURSE_TABLE="My Grad Courses";
    private static final String NEEDED_COURSES_TABLE="Needed Courses";
    private static final String DONE_COURSES_TABLE="Done Courses";
    private static final String USER_ID="id";
    private static final String USER_NAME="User Name";
    private static final String COURSE_NAME="Course Name";
    private static final String COURSE_NUMBER="Course Number";
    private static final String COURSE_CREDITS="Credits";
    private static final String COURSE_TYPE="Credits";
    private static final String COURSE_DESCRIPTION="description";
    private static final String PREQUISTIES="Prerequisites";
    private static final String FLAGS="Flags";

    private static final String TABLE_CREATE_PROFILES= "create table"+PROFILES_TABLE+"("+
           USER_ID + "integer PRIMARY KEY NOT NULL AUTO_INCREMENT,"+ USER_NAME + "TEXT"+")";
    private static final String TABLE_CREATE_MY_GRAD_COURSES= "create table"+MY_GRAD_COURSE_TABLE+"("+
            USER_ID + "integer not null,"+ COURSE_NAME + "text,"+
            COURSE_NUMBER + "integer not null,"+
            COURSE_CREDITS + "integer not null," +
            COURSE_TYPE + "text not null," +
            COURSE_DESCRIPTION + "text not null,"+
            PREQUISTIES + "text not null,"+
            FLAGS + "integer not null," + ")";

    private static final String TABLE_CREATE_NEEDED_COURSES= "create table"+NEEDED_COURSES_TABLE+"("+
            USER_ID + "integer,"+ COURSE_NAME + "text,"+
            COURSE_NUMBER + "integer not null,"+
            COURSE_CREDITS + "integer not null," +
            COURSE_TYPE + "text not null," +
            COURSE_DESCRIPTION + "text not null,"+
            PREQUISTIES + "text not null,"+
            FLAGS + "integer not null," + ")";
    private static final String TABLE_CREATE_DONE_COURSES= "create table"+DONE_COURSES_TABLE+"("+
            USER_ID + "integer not null,"+ COURSE_NAME + "text,"+
            COURSE_NUMBER + "integer not null,"+
            COURSE_CREDITS + "integer not null," +
            COURSE_TYPE + "text not null," +
            COURSE_DESCRIPTION + "text not null,"+
            PREQUISTIES + "text not null,"+
            FLAGS + "integer not null," + ")";




    /**
     * Creates the profiles' necessary information in the database.
     */
    public void CreateProfileTable() throws SQLException {

        statement.execute(TABLE_CREATE_PROFILES);
    }
    public void onUpgrade() throws SQLException {
        String query = "DROP TABLE IF EXIST" + TABLE_CREATE_PROFILES;
        statement.execute(query);
        profiledb.create();
    }

    /**
     * Creates the needed Courses necessary information in the database.
     */
    public void createGraddCourses() throws SQLException {
        statement.execute(TABLE_CREATE_MY_GRAD_COURSES);
    }
    /**
     * Creates the needed Courses necessary information in the database.
     */
    public void createNeededCourses() throws SQLException {
        statement.execute(TABLE_CREATE_NEEDED_COURSES);
    }
    /**
     * Creates the completed Courses necessary information in the database.
     */
    public void createCompletedCourses() throws SQLException {
        statement.execute(TABLE_CREATE_DONE_COURSES);
    }

    public void insertProfile(String name){
        String sql = "INSERT INTO Profiles(name) VALUES(?)";
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            //pstmt.setDouble(2, capacity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {

        ProfileDB app = new ProfileDB();
        // insert three new rows
        app.insertProfile("Raw Materials");
        app.insertProfile("Semifinished Goods");
        app.insertProfile("Finished Goods");
    }

}
