package CourseScheduler;

import java.sql.SQLException;

public class ProfileDB {
    private Database profiledb;

    private static final String TABLE_NAME="Needed Courses";
    private static final String COLUMN_ID="id";
    private static final String COURSE_NAME="Course Name";
    private static final String COURSE_NUMBER="Course Number";
    private static final String COURSE_="Credits";
    private static final String PREQUISTIES_="Prerequisites";
   // private static final String COLUMN_="Profiles";

    public ProfileDB(Database db) {
       profiledb = db;
    }

    /**
     * Creates the catalog's necessary information in the database.
     */
    public void create() throws SQLException {
        String string = "CREATE TABLE IF NOT EXISTS Needed Courses (" +
                "code TEXT PRIMARY KEY NOT NULL," + // compound identifier of name + number for convenience
                "course name TEXT NOT NULL," + // eg 'Calc II'
                "number INTEGER NOT NULL," + // eg '101'
                "type TEXT NOT NULL," + // eg 'cs'
                "credits INTEGER," + // number of credits
                "description TEXT," + // full course description
                "prereqs TEXT NOT NULL," + // pre-requisite codes stored in a comma separated list
                "flag INTEGER" + // flags for the algorithm
                ");";}
}
