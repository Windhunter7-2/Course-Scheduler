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

//    @Test
//    public void testRunAlgorithm() {
//
//    }
}