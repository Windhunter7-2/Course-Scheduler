package CourseScheduler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProfileDB  {
    
    private Database profiledb;
    Connection connection;
    
    private ProfileDB() throws SQLException {
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
    private static final String CODE="code";
    /**
     * creates a table of users.
     */
    private static final String TABLE_CREATE_PROFILES= "create table if not exists " + PROFILES_TABLE + "("+
           USER_ID + " integer PRIMARY KEY NOT NULL AUTO_INCREMENT, "+ USER_NAME + " TEXT);";

    private static final String TABLE_CREATE_MY_GRAD_COURSES= "create table if not exists  " + MY_GRAD_COURSE_TABLE + "("+
            USER_ID + " integer not null, " +
            COURSE_NAME + " text,"+
            CODE + " text not null);";
    /**
     * creates needed courses for a user.
     */
    private static final String TABLE_CREATE_NEEDED_COURSES= "create table if not exists " + NEEDED_COURSES_TABLE + "("+
            USER_ID + "integer, " +
            COURSE_NAME + "text, " +
            CODE + "text not null);";
    /**
     * creates done courses for a user.
     */
    private static final String TABLE_CREATE_DONE_COURSES= "create table if not exists  " + DONE_COURSES_TABLE + "("+
            USER_ID + " integer not null, " +
            COURSE_NAME + "text, " +
            CODE + " text not null);";
    
    /**
     * Creates the profiles' necessary information in the database.
     */
    public void create() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(TABLE_CREATE_PROFILES);
        
        createGradCourses();
        createNeededCourses();
        createCompletedCourses();
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
        String sql = "INSERT INTO profiles(user_name) VALUES(?)";
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
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
        String sql = "INSERT INTO needed_courses(user_id,code) VALUES(?,?);";
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void deleteNeededCourses(int id, String code ){
        String sql = "DELETE FROM needed_courses WHERE user_id = ? AND code = ?;";
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void insertDoneCourses(int id, String code ){
        String sql = "INSERT INTO done_courses(user_id,code) VALUES(?,?);";
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteDoneCourses(int id, String code ){
        String sql = "DELETE FROM done_courses WHERE user_id = ? AND code = ?;";
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public List<String> getNeededCourses(int id) throws SQLException {
        List<String> neededCourses = new ArrayList<>();
        String sql = "SELECT code FROM needed_courses WHERE user_id = ?;";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            //int id = rs.getInt("first_column_name");
            String str1 = rs.getString("code");
            neededCourses.add(str1);
        }
        return neededCourses;
    }
    
    public List<String> getDoneCourses(int id) throws SQLException {
        List<String> doneCourses = new ArrayList<>();
        String sql = "SELECT code FROM done_courses WHERE user_id = ?;";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            //int id = rs.getInt("first_column_name");
            String str1 = rs.getString("code");
            doneCourses.add(str1);
        }
        return doneCourses;
    }

}
