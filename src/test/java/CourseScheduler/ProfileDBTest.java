package CourseScheduler;

import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ProfileDBTest {


    @Test
    public ProfileDB create() throws IOException, SQLException {
        ProfileDB test = new ProfileDB();
        return test;
    }

    @Test
    public void insertProfile() throws IOException, SQLException {
        create().insertProfile("Akeem");
        create().insertProfile("Nate");
        create().insertProfile("Jack");
        create().insertProfile("Evan");


    }

    @Test
    public void getProfiles() throws IOException, SQLException {
        List<String> list = new ArrayList<>();
        list.add("Akeem");
        list.add("Nate");
        list.add("Jack");
        list.add("Evan");
        assertEquals("They matched", list, create().getProfiles());
    }

    @Test
    public List<String> setNeededCourses() throws IOException, SQLException {
        List<String> codes = new ArrayList<>();
        codes.add("CS 110");
        codes.add("CS 220");
        codes.add("CS 330");
        codes.add("CS 483");
        create().setNeededCourses("Akeem", codes);
        create().setNeededCourses("Nate", codes);
        create().setNeededCourses("Jack", codes);
        create().setNeededCourses("Evan", codes);
        return codes;
    }

    @Test
    public List<String> setDoneCourses() throws IOException, SQLException {
        List<String> dcodes = new ArrayList<>();
        dcodes.add("CS 110");
        dcodes.add("CS 220");
        dcodes.add("CS 330");
        dcodes.add("CS 483");
        create().setDoneCourses("Akeem", dcodes);
        create().setDoneCourses("Nate", dcodes);
        create().setDoneCourses("Jack", dcodes);
        create().setDoneCourses("Evan", dcodes);
        return dcodes;
    }


    @Test
    public void getNeededCourses() throws IOException, SQLException {
        assertEquals("They matched", setNeededCourses(), create().getNeededCourses("Akeem"));
        assertEquals("They matched", setNeededCourses(), create().getNeededCourses("Nate"));
        assertEquals("They matched", setNeededCourses(), create().getNeededCourses("Jack"));
        assertEquals("They matched", setNeededCourses(), create().getNeededCourses("Evan"));
    }

    @Test
    public void getDoneCourses() throws IOException, SQLException {
        assertEquals("They matched", setDoneCourses(), create().getDoneCourses("Akeem"));
        assertEquals("They matched", setDoneCourses(), create().getDoneCourses("Nate"));
        assertEquals("They matched", setDoneCourses(), create().getDoneCourses("Jack"));
        assertEquals("They matched", setDoneCourses(), create().getDoneCourses("Evan"));
    }
}