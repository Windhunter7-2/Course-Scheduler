package CourseScheduler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Scraper {
	
	static final String URL = "https://catalog.gmu.edu/courses/";
	
	private Catalog catalog;
	private Database db;
	
	public Scraper(Catalog catalog) {
		this.catalog = catalog;
		this.db = catalog.getDatabase();
	}
	
	public List<Course> run() throws SQLException, IOException {
		catalog.create();
		
		Document doc = Jsoup.parse(new java.net.URL(URL), 3000);
		
		// This block of code gets links to all the catalogs
		List<String> catalogs = new ArrayList<>();
		doc.body().select("#atozindex").select("ul").forEach(list -> {
			list.select("li").forEach(entry -> {
				catalogs.add(entry.select("a").first().absUrl("href"));
			});
		});
		
		System.out.println("Found " + catalogs.size() + " catalogs: " + catalogs);
		
		List<Course> courses = new ArrayList<Course>();
		
		for (String s : catalogs) {
			courses.addAll(scrapeCourses(s));
		}
		
		return courses;
	}
	
	/**
	 * Scrapes individual courses from a catalog page, eg "https://catalog.gmu.edu/courses/cs/"
	 *
	 * @param link The absolute link of the catalog.
	 */
	private List<Course> scrapeCourses(String link) throws IOException {
		Document doc = Jsoup.parse(new java.net.URL(link), 3000);
		return doc.select(".courseblock").stream().map(Scraper::scrapeCourse).collect(Collectors.toList());
	}
	
	// For testing purposes only
	static String parsePrereqs(String block) {
		return parsePrereqs(block, new ArrayList<>(), new ArrayList<>());
	}
	
	/**
	 * Parses the String representing a course, eg:
	 * <p>
	 * "(CS 110*C or 101*) and (CS 211C or 222C).
	 * * May be taken concurrently.
	 * C Requires minimum grade of C."
	 * <p>
	 * returns:
	 * <p>
	 * "(CS110|CS101)&(CS211|CS222)"
	 *
	 * @param block The block of text from the Catalog.
	 * @param prereqs A list that will be populated with the pre-requisites (and coreqs) of the course.
	 * @param coreqs A list that will be populated with stricly the coreqs of the course.
	 * @return A String to represent the pre-reqs.
	 */
	static String parsePrereqs(String block, List<String> prereqs, List<String> coreqs) {
		// Detect all conditions for minimum grades and preserve them for later removal.
		Map<String, String> conditions = new HashMap<>();
		Matcher matcher = Pattern.compile("([A-F][-+]?) Requires minimum grade of [A-F][-+]?").matcher(block);
		while (matcher.find()) {
			block = matcher.replaceFirst("");
			conditions.put(matcher.group(1), matcher.group(0));
		}
		block = block.replace("* May be taken concurrently.", "");
		conditions.put("*", "");
		
		// Break into parenthesis blocks first.
		// This uses a typical stack algorithm to traverse each nested parenthesis group.
		// It adds the starting position of each opening parenthesis and pops it off when the
		// closing parenthesis is encountered.
		List<String> groups = new ArrayList<>();
		if (block.contains("(")) {
			Stack<Integer> stack = new Stack<>();
			
			for (int i = 0; i < block.toCharArray().length; i++) {
				char c = block.charAt(i);
				switch (c) {
					case '(':
						stack.add(i);
						break; // save start
					case ')':
						groups.add(block.substring(stack.pop() + 1, i));
						break; // pop start and substring
					default:
						break;
				}
			}
		} else {
			groups.add(block);
		}
		
		// Now "groups" is every parenthesis group in order from most nested to least nested.
		// We should transform each group one by one and replace the result in every other string too.
		for (int i = 0; i < groups.size(); i++) {
			String group = groups.get(i);
			String replace = parsePrereqs(group, conditions, prereqs, coreqs);
			block = block.replace(group, replace);
			
			// Replace in all other groups as well.
			for (int j = i; j < groups.size(); j++) {
				groups.set(j, groups.get(j).replace(group, replace));
			}
		}
		
		// Cleanup of trailing characters/whitespace.
		block = block.replaceAll("[.\\s]+$", "").replaceAll("^\\(([^()]+)\\)$", "$1").trim();
		
		// Final pass to fix issues with mismanaged groups.
		block = block.replaceAll(" and ", "&").replaceAll(" or ", "|");
		
		return block;
	}
	
	/**
	 * Handles the parsing of a prerequisite block from the catalog.
	 *
	 * @param block The block of text to parse.
	 * @param conditions A map of conditions that can apply to each course, like requiring a certain grade or
	 * the ability to take the course concurrently.
	 * @param prereqs A list that will be populated with the pre-requisites (and coreqs) of the course.
	 * @param coreqs A list that will be populated with stricly the coreqs of the course.
	 * @return A String representing the parental relationship of the course.
	 */
	private static String parsePrereqs(String block, Map<String, String> conditions, List<String> prereqs, List<String> coreqs) {
		StringBuilder parents = new StringBuilder();
		
		// Strip trailing periods.
		block = block.replaceAll("\\.$", "");
		
		// Strip all testing pre-reqs first
		block = block.replaceAll("minimum score of .*?,", "");
		
		if (block.contains("or") || block.contains("and")) {
			System.out.println("Handle or/and: " + block);
			
			char join = block.contains("or") ? '|' : '&';
			block = block.replaceAll("and|or", "");
			
			// Grab the type of the first class, eg "MATH"
			String[] split = block.split(" ");
			String type = split[0];
			List<String> numbers = new ArrayList<>();
			
			// Handle each additional number
			for (int i = 1; i < split.length; i++) {
				String numb = split[i];
				if (numb.isBlank()) {
					continue;
				}
				if (numb.startsWith("(")) {
					numbers.add(numb);
					continue;
				}
				
				// Save all the conditions that were hit
				List<String> conditionals = new ArrayList<>();
				for (String c : numb.split("")) {
					if (conditions.containsKey(c)) {
						numb = numb.replace(c, "");
						conditionals.add(c);
					}
				}
				
				if (conditionals.contains("*"))
					coreqs.add(type + numb);
				
				// Now we're handling numbers
				numbers.add(type + numb);
				prereqs.add(type + numb);
			}
			
			parents.append(numbers.stream().collect(Collectors.joining(join + "")));
		} else {
			// Hopefully just a single course.
			block = block.trim().replaceAll("\\.$", "");
			System.out.println("Handle single: " + block);
			String type = block.split(" ")[0];
			String numb = block.split(" ")[1];
			
			// Strip the condition codes
			for (String key : conditions.keySet()) {
				numb = numb.replaceAll(key + "$", "");
			}
			
			parents.append(type);
			parents.append(numb);
		}
		
		return parents.toString();
	}
	
	/**
	 * Scrapes an individual course element and transforms it into a Course.
	 *
	 * @param element The element to scrape.
	 * @return A Course representing the element.
	 */
	static Course scrapeCourse(Element element) {
		Element elTitle = element.selectFirst(".courseblocktitle");
		
		// Retrieves the technical name, eg "ACCT 203:"
		// Strip the trailing semi-colon
		String name = elTitle.select(".cb_code").text().split(":$")[0];
		// Retrieves the full name of the course, eg "Survey of Accounting."
		// Strip the trailing period
		String title = elTitle.select(".cb_title").text().split("\\.$")[0];
		// Use a regular expression to
		int credits = Integer.parseInt(elTitle.text().replaceAll(".*(\\d+) credits?\\.$", "$1")); // TODO
		// Grab the entire description.
		String desc = element.selectFirst(".courseblockdesc").text();
		
		String parents = "";
		List<String> prereqs = new ArrayList<>();
		List<String> coreqs = new ArrayList<>();
		if (element.selectFirst(".prereq") != null) {
			String block = element.selectFirst(".prereq").text().replaceAll("Required Prerequisites: ", "");
			parents = parsePrereqs(block, prereqs, coreqs);
		}
		
		return new Course(
				title,
				name,
				name.split(" ")[0],
				credits,
				desc,
				name.split(" ")[0] + name.split(" ")[1],
				prereqs,
				coreqs,
				0, // TODO flag method
				parents
		);
	}
	
	private void save(Course course) {
		// TODO write to DB
	}
	
	public Date getLastRun() {
		// TODO
		System.out.println(System.getProperty("user.dir").toString());
		//Talk to Nathan about querying SQL if we're doing it that way.
		return new Date(1992);
	}
	
	class ScrapedPrereq {
		
		String title;
		List<String> conditions;
		
		public ScrapedPrereq(String title, List<String> conditions) {
			this.title = title;
			this.conditions = conditions;
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("ScrapedPrereq{");
			sb.append("title='").append(title).append('\'');
			sb.append(", conditions=").append(conditions);
			sb.append('}');
			return sb.toString();
		}
	}
	
}
