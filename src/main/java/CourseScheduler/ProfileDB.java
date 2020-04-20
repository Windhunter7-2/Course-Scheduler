package CourseScheduler;

import javafx.scene.Cursor;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class ProfileDB  {
    private  Database profiledb;
    Connection  connection;


    private  ProfileDB() throws SQLException {
        profiledb = new Database("user_profiles");
        connection = profiledb.get();

    }
    private static final String PROFILES_TABLE="profiles";
    private static final String MY_GRAD_COURSE_TABLE="my_grad_courses";
    private static final String NEEDED_COURSES_TABLE="needed_courses";
    private static final String DONE_COURSES_TABLE="done_courses";
    private static final String USER_ID="id";
    private static final String USER_NAME="user_name";
    private static final String COURSE_NAME="course_name";
    private static final String COURSE_NUMBER="course_number";
    private static final String COURSE_CREDITS="credits";
    private static final String COURSE_TYPE="types";
    private static final String COURSE_DESCRIPTION="description";
    private static final String PREQUISTIES="prerequisites";
    private static final String FLAGS="flags";
    private static final String CODE="code";
    /**
     * creates a table of users.
     */
    private static final String TABLE_CREATE_PROFILES= "create table"+PROFILES_TABLE+"("+
           USER_ID + "integer PRIMARY KEY NOT NULL AUTO_INCREMENT,"+ USER_NAME + "TEXT"+")";

    private static final String TABLE_CREATE_MY_GRAD_COURSES= "create table"+MY_GRAD_COURSE_TABLE+"("+
            USER_ID + "integer not null,"+ COURSE_NAME + "text,"+
            CODE + "text not null,"+
           /* COURSE_NUMBER + "integer not null,"+
            COURSE_CREDITS + "integer not null," +
            COURSE_TYPE + "text not null," +
            COURSE_DESCRIPTION + "text not null,"+
            PREQUISTIES + "text not null,"+
            FLAGS + "integer not null," +*/ ")";
    /**
     * creates needed courses for a user.
     */
    private static final String TABLE_CREATE_NEEDED_COURSES= "create table"+NEEDED_COURSES_TABLE+"("+
            USER_ID + "integer,"+ COURSE_NAME + "text,"+
            CODE + "text not null,"+
            /*COURSE_NUMBER + "integer not null,"+
            COURSE_CREDITS + "integer not null," +
            COURSE_TYPE + "text not null," +
            COURSE_DESCRIPTION + "text not null,"+
            PREQUISTIES + "text not null,"+
            FLAGS + "integer not null," +*/ ")";
    /**
     * creates done courses for a user.
     */
    private static final String TABLE_CREATE_DONE_COURSES= "create table"+DONE_COURSES_TABLE+"("+
            USER_ID + "integer not null,"+ COURSE_NAME + "text,"+
            CODE + "text not null,"+
           /* COURSE_NUMBER + "integer not null,"+
            COURSE_CREDITS + "integer not null," +
            COURSE_TYPE + "text not null," +
            COURSE_DESCRIPTION + "text not null,"+
            PREQUISTIES + "text not null,"+
            FLAGS + "integer not null," +*/ ")";




    /**
     * Creates the profiles' necessary information in the database.
     */
    public void CreateProfileTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(TABLE_CREATE_PROFILES);
    }
    public void onUpgrade() throws SQLException, IOException {
        Statement statement = connection.createStatement();
        String query = "DROP TABLE IF EXIST" + TABLE_CREATE_PROFILES;
        statement.execute(query);
        profiledb.create();
    }

    /**
     * Creates the grad Courses necessary information in the database.
     */
    public void createGradCourses() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(TABLE_CREATE_MY_GRAD_COURSES);
    }
    /**
     * Creates the needed Courses necessary information in the database.
     */
    public void createNeededCourses() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(TABLE_CREATE_NEEDED_COURSES);
    }
    /**
     * Creates the completed Courses necessary information in the database.
     */
    public void createCompletedCourses() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(TABLE_CREATE_DONE_COURSES);
    }

    public void insertProfile(String name){
        String sql = "INSERT INTO Profiles(user_name) VALUES(?)";
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            //pstmt.setDouble(2, capacity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertMyGradCourses(int id, String code ){
        String sql = "INSERT INTO my_grad_courses(user_id,code) VALUES(?,?)";
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, code);
                      //pstmt.setDouble(2, capacity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void deleteMyGradCourses(int id, String code ){
        String sql = "DELETE FROM my_grad_courses WHERE user_id = id AND code = code";
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, code);
            //pstmt.setDouble(2, capacity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void insertNeededCourses(int id, String code ){
        String sql = "INSERT INTO needed_courses(user_id,code) VALUES(?,?)";
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, code);
            //pstmt.setDouble(2, capacity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void deleteNeededCourses(int id, String code ){
        String sql = "DELETE FROM needed_courses WHERE user_id = id AND code = code";
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, code);
            //pstmt.setDouble(2, capacity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void insertDoneCourses(int id, String code ){
        String sql = "INSERT INTO done_courses(user_id,code) VALUES(?,?)";
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, code);
            //pstmt.setDouble(2, capacity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteDoneCourses(int id, String code ){
        String sql = "DELETE FROM done_courses WHERE user_id = id AND code = code";
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, code);
            //pstmt.setDouble(2, capacity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public ArrayList<String> getNeededCourses(int id) throws SQLException {
        ArrayList neededCourses = new ArrayList<String>();
        Statement statement = connection.createStatement();
        String sql = "SELECT code FROM needed_courses WHERE user_id=id ;";
        ResultSet rs = statement.executeQuery(sql);
        if (rs.next()) {
            //int id = rs.getInt("first_column_name");
            String str1 = rs.getString("code");
            neededCourses.add(str1);
        }
        return neededCourses;
    }
    public ArrayList<String> getDoneCourses(int id) throws SQLException {
        ArrayList doneCourses = new ArrayList<String>();
        Statement statement = connection.createStatement();
        String sql = "SELECT code FROM needed_courses WHERE user_id=id ;";
        ResultSet rs = statement.executeQuery(sql);
        if (rs.next()) {
            //int id = rs.getInt("first_column_name");
            String str1 = rs.getString("code");
            doneCourses.add(str1);
        }
        return doneCourses;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {

        ProfileDB app = new ProfileDB();
        // insert three new rows
        app.insertProfile("Akeem");
        app.insertProfile("Jack");
        app.insertProfile("Nate");
    }

}
