package CourseScheduler;

import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ProfileDBTest {

    //ProfileDB test1 = new ProfileDB();

    public ProfileDBTest() throws IOException, SQLException {
    }

    @Test
    public void criate() throws IOException, SQLException {
        ProfileDB test = new ProfileDB();

    }

    @Test
    public void insertProfile() throws IOException, SQLException {
        ProfileDB test1 = new ProfileDB();
        test1.insertProfile("Akeem");
        test1.insertProfile("Nate");
        test1.insertProfile("Jack");
        test1.insertProfile("Evan");

    }

    @Test
    public void deleteProfile() throws IOException, SQLException {
        ProfileDB test1 = new ProfileDB();
        test1.insertProfile("Akeem");
        test1.deleteProfile("Akeem");


    }

    @Test
    public void getProfiles() throws IOException, SQLException {
        ProfileDB test1 = new ProfileDB("test_profiles");
        test1.create();
        test1.insertProfile("Akeem");
        test1.insertProfile("Nate");
        test1.insertProfile("Jack");
        test1.insertProfile("Evan");

        /*test1.deleteProfile("Akeem");*/
        List<String> list = new ArrayList<>();
       // list.add("Akeem");
        list.add("Nate");
        list.add("Jack");
        list.add("Evan");
        list.add("Akeem");
        System.out.println( "profiles in db +" +  test1.getProfiles());
        assertEquals("They matched", list, test1.getProfiles());
    }

    @Test
    public void setNeededCourses() throws IOException, SQLException {
        ProfileDB test1 = new ProfileDB("test_profiles");
        test1.create();
        test1.insertProfile("Akeem");
        test1.insertProfile("Nate");
        test1.insertProfile("Jack");
        test1.insertProfile("Evan");

        List<String> codes = new ArrayList<>();
        codes.add("CS-110");
        codes.add("CS-220");
        codes.add("CS-330");
        codes.add("CS-483");
        test1.setNeededCourses("Akeem", codes);
        test1.setNeededCourses("Nate", codes);
        test1.setNeededCourses("Jack", codes);
        test1.setNeededCourses("Evan", codes);

    }

    @Test
    public void setDoneCourses() throws IOException, SQLException {
        ProfileDB test1 = new ProfileDB("test_profiles");
        test1.create();
        test1.insertProfile("Akeem");
        test1.insertProfile("Nate");
        test1.insertProfile("Jack");
        test1.insertProfile("Evan");

        List<String> dcodes = new ArrayList<>();
        dcodes.add("CS-120");
        dcodes.add("CS-270");
        dcodes.add("CS-360");
        dcodes.add("CS-493");
        test1.setDoneCourses("Akeem", dcodes);
        test1.setDoneCourses("Nate", dcodes);
        test1.setDoneCourses("Jack", dcodes);
        test1.setDoneCourses("Evan", dcodes);

    }


    @Test
    public void getNeededCourses() throws IOException, SQLException {
        ProfileDB test1 = new ProfileDB("test_profiles");
        test1.create();
        test1.insertProfile("Akeem");
        test1.insertProfile("Nate");
        test1.insertProfile("Jack");
        test1.insertProfile("Evan");

        List<String> codes = new ArrayList<>();
        codes.add("CS-110");
        codes.add("CS-220");
        codes.add("CS-330");
        codes.add("CS-483");
        test1.setNeededCourses("Akeem", codes);
        test1.setNeededCourses("Nate", codes);
        test1.setNeededCourses("Jack", codes);
        test1.setNeededCourses("Evan", codes);
        System.out.println( "getAkeem courses +" +  test1.getNeededCourses("Akeem"));
        System.out.println( "getNatecourses +" +  test1.getNeededCourses("Nate"));
        System.out.println("compared to +" +  codes);
        assertEquals("They matched", codes.toString(),test1.getNeededCourses("Akeem").toString());
        assertEquals("They matched", codes, test1.getNeededCourses("Nate"));
        assertEquals("They matched", codes, test1.getNeededCourses("Jack"));
        assertEquals("They matched", codes, test1.getNeededCourses("Evan"));
    }

    @Test
    public void getDoneCourses() throws IOException, SQLException {
        ProfileDB test1 = new ProfileDB("test_profiles");
        test1.create();
        test1.insertProfile("Akeem");
        test1.insertProfile("Nate");
        test1.insertProfile("Jack");
        test1.insertProfile("Evan");

        List<String> dcodes = new ArrayList<>();
        dcodes.add("CS-120");
        dcodes.add("CS-270");
        dcodes.add("CS-360");
        dcodes.add("CS-493");
        test1.setDoneCourses("Akeem", dcodes);
        test1.setDoneCourses("Nate", dcodes);
        test1.setDoneCourses("Jack", dcodes);
        test1.setDoneCourses("Evan", dcodes);

       // assertEquals("They matched", dcodes, test1.getDoneCourses("Akeem"));
        assertEquals("They matched", dcodes, test1.getDoneCourses("Nate"));
        assertEquals("They matched", dcodes, test1.getDoneCourses("Jack"));
        assertEquals("They matched", dcodes, test1.getDoneCourses("Evan"));
    }
}