package CourseScheduler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class ScraperTests {
	
//	@Test
	public void test() throws IOException, SQLException {
		Database db = new Database("scraper_test");
		db.create();
		
		Catalog catalog = new Catalog(db);
		catalog.create();
		
		Scraper scraper = new Scraper(catalog);
		scraper.run().forEach(c -> {
			System.out.println(c.toString());
		});
		
		// TODO actual tests
	}
	
	@Test
	public void prereqsParsing1() {
		String s1 = Scraper.parsePrereqs("ACCT 351.");
		assertEquals("ACCT351", s1);
	}
	
	@Test
	public void prereqsParsing2() {
		String s1 = Scraper.parsePrereqs("ACCT 351C. C Requires minimum grade of C.");
		assertEquals("ACCT351", s1);
	}
	
	@Test
	public void prereqsParsing3() {
		String s1 = Scraper.parsePrereqs("(ACCT 351C). C Requires minimum grade of C.");
		assertEquals("ACCT351", s1);
	}
	
	@Test
	public void prereqsParsing4() {
		String s1 = Scraper.parsePrereqs("(ACCT 351C and 101). C Requires minimum grade of C.");
		assertEquals("ACCT351&ACCT101", s1);
	}
	
	@Test
	public void prereqsParsing5() {
		String s1 = Scraper.parsePrereqs("(ACCT 351C and (ACCT 101 or 202)). C Requires minimum grade of C.");
		assertEquals("(ACCT351&(ACCT101|ACCT202))", s1);
	}
	
	@Test
	public void prereqsParsing6() {
		String s1 = Scraper.parsePrereqs("(MATH 105C, 105T, 104C, 104T, 113C or 123C). C Requires minimum grade of C.");
		assertEquals("MATH105|MATH105T|MATH104|MATH104T|MATH113|MATH123", s1);
	}
	
	@Test
	public void prereqsParsing7() {
		String s1 = Scraper.parsePrereqs("(CS 110C or 101) and (CS 211C or 222C). C Requires minimum grade of C.");
		assertEquals("(CS110|CS101)&(CS211|CS222)", s1);
	}
	
	@Test
	public void prereqsParsing8() {
		String s1 = Scraper.parsePrereqs("(CS 110C or 101) and (CS 211C or 222C). C Requires minimum grade of C.");
		assertEquals("(CS110|CS101)&(CS211|CS222)", s1);
	}
	
	@Test
	public void scrapeCS262() {
		String html = "<div class=\"courseblock\">\n" +
				"<div class=\"courseblocktitle\"><strong class=\"cb_code\">CS&nbsp;262:</strong> <em class=\"cb_title\">Introduction to Low-Level Programming.</em> 3 credits.</div>\n" +
				"<div class=\"courseblockdesc\">Introduction to the language C, as well as operating system concepts, in UNIX, to prepare students for topics in systems programming. Offered by <a " +
				"target=\"_blank\" href=\"/colleges-schools/engineering/computer-science/\">Computer Science</a>. Limited to two attempts.</div><div class=\"courseblockextra\"><strong>Registration Restrictions: </strong><p class=\"prereq\"><b>Required Prerequisites:</b> (<a href=\"/search/?P=CS%20110\" title=\"CS&nbsp;110\" class=\"bubblelink code\" onclick=\"return showCourse(this, 'CS 110');\">CS&nbsp;110</a><sup>*</sup><sup>C</sup> or 101<sup>*</sup>) and (<a href=\"/search/?P=CS%20211\" title=\"CS&nbsp;211\" class=\"bubblelink code\" onclick=\"return showCourse(this, 'CS 211');\">CS&nbsp;211</a><sup>C</sup> or <a href=\"/search/?P=CS%20222\" title=\"CS&nbsp;222\" class=\"bubblelink code\" onclick=\"return showCourse(this, 'CS 222');\">222</a><sup>C</sup>).<br><sup>*</sup> May be taken concurrently.<br><sup>C</sup> Requires minimum grade of C.</p><p class=\"att\">Students with the terminated from VSE major attribute may <strong>not</strong> enroll.</p></div><div class=\"courseblockextra\"><strong>Schedule Type: </strong>Laboratory, Lecture</div><div class=\"courseblockextra\"><strong>Grading: </strong><br>This course is graded on the <a href=\"/policies/academic/grading/\">Undergraduate Regular scale.</a></div></div>";
		
		Element el = Jsoup.parse(html).getAllElements().first();
		
		Course c = Scraper.scrapeCourse(el);
		assertEquals("Introduction to Low-Level Programming", c.fullName);
		assertEquals("CS 262", c.name);
		assertEquals(3, c.credits);
		assertEquals("CS", c.type);
		assertEquals("Introduction to the language C, as well as operating system concepts, in UNIX, to prepare students for topics in systems programming. Offered by Computer Science. Limited to " +
				"two attempts.", c.desc);
		assertEquals("(CS110|CS101)&(CS211|CS222)", c.parents);
		assertEquals(Arrays.asList("CS110", "CS101"), c.coreqs);
		assertEquals(Arrays.asList("CS110", "CS101", "CS211", "CS222"), c.prereqs);
		assertEquals("CS262", c.code);
	}
	
}
