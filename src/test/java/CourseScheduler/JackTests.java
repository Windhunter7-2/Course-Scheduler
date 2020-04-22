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
        BufferedReader br2 = br;
        String realLastRun = br.readLine();
        //Update the stored time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy, HH:mm:ss");
        LocalDateTime testTime = LocalDateTime.now().withNano(0);
        FileWriter fw = new FileWriter(LocalStorage.get("dateLastRun.txt"));
        fw.write(dtf.format(testTime));
        fw.close();
        //Make sure it works
        assertEquals(scr.getLastRun(), testTime);
        //Restore old time
        FileWriter fw2 = new FileWriter(LocalStorage.get("dateLastRun.txt"), false);
        fw2.write(realLastRun);
        fw2.close();
    }
}