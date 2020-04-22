package CourseScheduler;

import org.junit.Test;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class JackTests {

    @Test
    public void testGetLastRun() throws IOException {
        Scraper scr = new Scraper(new Catalog(new Database("testdb")));
        //Preserve the actual time the scraper was last run.
        File dateFile = LocalStorage.get("dateLastRun.txt");
        BufferedReader br = new BufferedReader(new FileReader(dateFile));
        String line = br.readLine();
        boolean wasEmpty = false;
        if(line == null) {
            scr.setLastRun(LocalDateTime.now());
            wasEmpty = true;
        }
        LocalDateTime realLastRun = LocalDateTime.parse(line, DateTimeFormatter.ofPattern("MM/dd/yyyy, HH:mm:ss"));
        //Update the stored time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy, HH:mm:ss");
        LocalDateTime testTime = LocalDateTime.now().withNano(0);
        scr.setLastRun(testTime);
        //Make sure it works
        assertEquals(scr.getLastRun(), testTime);
        //Restore old time
        if(wasEmpty) {
            new FileWriter(dateFile).close(); //Clear file
        } else {
            scr.setLastRun(realLastRun); //Restore old contents of file
        }
    }
}