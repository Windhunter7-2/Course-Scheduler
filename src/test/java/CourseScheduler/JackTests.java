package CourseScheduler;

import org.junit.Test;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JackTests {

    @Test
    public void testGetLastRun() throws FileNotFoundException {
        Scraper scr = new Scraper(new Catalog(new Database("testdb")));
        PrintWriter out = new PrintWriter("testDateFile");
        out.println("11/22/1963, 12:30:00");
        LocalDateTime actual = LocalDateTime.now();
        try {
            actual = scr.getLastRun();
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        assertEquals("11/22/1963, 12:30:00", actual);
    }
}