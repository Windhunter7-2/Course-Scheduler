package CourseScheduler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScraperTests {
	
//	@Test
	public void test() throws IOException, SQLException {
		Database db = new Database("scraper_test");
		db.create();
		
		Catalog catalog = new Catalog(db);
		catalog.create();
		
		Scraper scraper = new Scraper(catalog);
		List<Course> courses = scraper.run();
		List<Course> retrieved = catalog.getCourses();
		
		// Tests that each course was correctly inserted and retrieved from the database.
		for (Course course : courses) {
			Optional<Course> closest = retrieved.stream().filter(c -> c.getCode().equals(course.getCode())).findFirst();
			if (!retrieved.contains(course)) {
				Course r = closest.get();
				Course c = course;
				System.out.println(r.getCredits() == c.getCredits());
				System.out.println(r.getFlag() == c.getFlag());
				System.out.println(Objects.equals(r.getFullName(), c.getFullName()));
				System.out.println(Objects.equals(r.getName(), c.getName()));
				System.out.println(Objects.equals(r.getType(), c.getType()));
				System.out.println(Objects.equals(r.getDesc(), c.getDesc()));
				System.out.println(Objects.equals(r.getCode(), c.getCode()));
				System.out.println(Objects.equals(r.getPrerequisites(), c.getPrerequisites()));
				System.out.println(Objects.equals(r.getCoreqs(), c.getCoreqs()));
				System.out.println(Objects.equals(r.getParents(), c.getParents()));
			}
			assertTrue(course + " was not preserved. Closest match: ", retrieved.contains(course));
		}
	}
	
	@Test
	public void prereqsParsing1() {
		String s1 = Scraper.parsePrereqs("ACCT 351.");
		assertEquals("ACCT-351", s1);
	}
	
	@Test
	public void prereqsParsing2() {
		String s1 = Scraper.parsePrereqs("ACCT 351C. C Requires minimum grade of C.");
		assertEquals("ACCT-351", s1);
	}
	
	@Test
	public void prereqsParsing3() {
		String s1 = Scraper.parsePrereqs("(ACCT 351C). C Requires minimum grade of C.");
		assertEquals("(ACCT-351)", s1);
	}
	
	@Test
	public void prereqsParsing4() {
		String s1 = Scraper.parsePrereqs("(ACCT 351C and 101). C Requires minimum grade of C.");
		assertEquals("(ACCT-351&ACCT-101)", s1);
	}
	
	@Test
	public void prereqsParsing5() {
		String s1 = Scraper.parsePrereqs("(ACCT 351C and (ACCT 101 or 202)). C Requires minimum grade of C.");
		assertEquals("(ACCT-351&(ACCT-101|ACCT-202))", s1);
	}
	
	@Test
	public void prereqsParsing6() {
		String s1 = Scraper.parsePrereqs("(MATH 105C, 105T, 104C, 104T, 113C or 123C). C Requires minimum grade of C.");
		assertEquals("(MATH-105|MATH-105T|MATH-104|MATH-104T|MATH-113|MATH-123)", s1);
	}
	
	@Test
	public void prereqsParsing7() {
		String s1 = Scraper.parsePrereqs("(CS 110C or 101) and (CS 211C or 222C). C Requires minimum grade of C.");
		assertEquals("(CS-110|CS-101)&(CS-211|CS-222)", s1);
	}
	
	@Test
	public void prereqsParsing8() {
		String s1 = Scraper.parsePrereqs("(FNAN 303B- or L303) and ((ACCT 303C or L303) or (ACCT 330C or L330)).\n" +
												 "B- Requires minimum grade of B-.\n" +
												 "C Requires minimum grade of C.");
		assertEquals("(FNAN-303|FNAN-L303)&((ACCT-303|ACCT-L303)|(ACCT-330|ACCT-L330))", s1);
	}
	
	@Test
	public void prereqsParsing9() {
		String s1 = Scraper.parsePrereqs("(ACCT 303) or (ACCT 330)");
		assertEquals("(ACCT-303)|(ACCT-330)", s1);
	}
	
	@Test
	public void prereqsParsing10() {
		String str = Scraper.parsePrereqs("((ACCT 203C, U203, 204C or U204) and (BUS 210C or U210) and (MATH 108C, U108, 113C, U113, 114C, U114, HNRT 225C or U225)). C Requires minimum grade of C.");
		assertEquals("((ACCT-203|ACCT-U203|ACCT-204|ACCT-U204)&(BUS-210|BUS-U210)&(MATH-108|MATH-U108|MATH-113|MATH-U113|MATH-114|MATH-U114|HNRT-225|HNRT-U225))", str);
	}
	
	@Test
	public void prereqsParsing11() {
		String str = Scraper.parsePrereqs("(ACCT 303 or 303L)");
		assertEquals("(ACCT-303|ACCT-303L)", str);
	}
	
	@Test
	public void scrapeCS262() {
		String html = "<div class=\"courseblock\">\n" +
				"<div class=\"courseblocktitle\"><strong class=\"cb_code\">CS&nbsp;262:</strong> <em class=\"cb_title\">Introduction to Low-Level Programming.</em> 3 credits.</div>\n" +
				"<div class=\"courseblockdesc\">Introduction to the language C, as well as operating system concepts, in UNIX, to prepare students for topics in systems programming. Offered by <a " +
				"target=\"_blank\" href=\"/colleges-schools/engineering/computer-science/\">Computer Science</a>. Limited to two attempts.</div><div class=\"courseblockextra\"><strong>Registration Restrictions: </strong><p class=\"prereq\"><b>Required Prerequisites:</b> (<a href=\"/search/?P=CS%20110\" title=\"CS&nbsp;110\" class=\"bubblelink code\" onclick=\"return showCourse(this, 'CS 110');\">CS&nbsp;110</a><sup>*</sup><sup>C</sup> or 101<sup>*</sup>) and (<a href=\"/search/?P=CS%20211\" title=\"CS&nbsp;211\" class=\"bubblelink code\" onclick=\"return showCourse(this, 'CS 211');\">CS&nbsp;211</a><sup>C</sup> or <a href=\"/search/?P=CS%20222\" title=\"CS&nbsp;222\" class=\"bubblelink code\" onclick=\"return showCourse(this, 'CS 222');\">222</a><sup>C</sup>).<br><sup>*</sup> May be taken concurrently.<br><sup>C</sup> Requires minimum grade of C.</p><p class=\"att\">Students with the terminated from VSE major attribute may <strong>not</strong> enroll.</p></div><div class=\"courseblockextra\"><strong>Schedule Type: </strong>Laboratory, Lecture</div><div class=\"courseblockextra\"><strong>Grading: </strong><br>This course is graded on the <a href=\"/policies/academic/grading/\">Undergraduate Regular scale.</a></div></div>";
		
		Element el = Jsoup.parse(html).getAllElements().first();
		
		Course c = Scraper.scrapeCourse(el);
		assertEquals("Introduction to Low-Level Programming", c.getFullName());
		assertEquals("CS 262", c.getName());
		assertEquals(3, c.getCredits());
		assertEquals("CS", c.getType());
		assertEquals("Introduction to the language C, as well as operating system concepts, in UNIX, to prepare students for topics in systems programming. Offered by Computer Science. Limited to " +
				"two attempts.", c.getDesc());
		assertEquals("(CS-110|CS-101)&(CS-211|CS-222)", c.getParents());
		assertEquals(Arrays.asList("CS-110", "CS-101"), c.getCoreqs());
		assertEquals(Arrays.asList("CS-110", "CS-101", "CS-211", "CS-222"), c.getPrerequisites());
		assertEquals("CS-262", c.getCode());
	}
	
}
