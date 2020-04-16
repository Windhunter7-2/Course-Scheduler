package CourseScheduler;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JackTests {

    @Test
    public void testGetLastRun() throws IOException {
        Scraper scr = new Scraper(new Catalog(new Database("testdb")));
        LocalDateTime actual = LocalDateTime.now();
        try {
            actual = scr.getLastRun();
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        assertEquals(LocalDateTime.parse("11/22/1963, 12:30:00", DateTimeFormatter.ofPattern("MM/dd/yyyy, HH:mm:ss")), actual);
    }

    /* What could someday be a setNodesList() test if it and its constituent parameters weren't private. (They should be tho)
    @Test
    public void testSetNodesList() {
        RunAlgorithm ra = new RunAlgorithm();
        ArrayList<Course> courseList = new ArrayList<Course>();
        Course test1 = new Course("Test Course 1", "Test1", "TEST",
                3, "This course exists only for testing of setNodesList()", "TEST-001",
                new ArrayList<String>(), new ArrayList<String>(), 0, "Test2");
        Course test2 = new Course("Test Course 2", "Test2", "TEST",
                3, "This course exists only for testing of setNodesList()", "TEST-002",
                new ArrayList<String>(), new ArrayList<String>(), 0, "");
        courseList.add(test1);
        courseList.add(test2);
        ra.setNodesList(courseList);
        assertEquals()
    }*/
}