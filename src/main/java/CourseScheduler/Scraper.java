package CourseScheduler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Scraper {
	
	private static final String URL = "https://catalog.gmu.edu/courses/";
	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("MM/dd/yyyy, HH:mm:ss");
	private static final int TOTAL_CLASSES = 7522;
	
	private final Catalog catalog;
	private final Database db;
	
	public Scraper(Catalog catalog) {
		this.catalog = catalog;
		this.db = catalog.getDatabase();
	}
	
	public List<Course> run(Consumer<Progress> consumer) throws SQLException, IOException {
		catalog.create();
		dump();
		
		consumer.accept(new Progress(null, "Scraping catalog list..."));
		List<String> catalogs = scrapeCatalogList();
		consumer.accept(new Progress(null, "Found " + catalogs.size() + " catalogs to process."));
		
		List<Course> courses = new ArrayList<Course>();
		for (String s : catalogs) {
			courses.addAll(scrapeCourses(s));
			consumer.accept(new Progress(courses.size() * 1f / TOTAL_CLASSES, "Scraped " + s));
		}
		
		save(courses);
		setLastRun(LocalDateTime.now());
		
		consumer.accept(new Progress(Float.MAX_VALUE, "Finished scraping " + courses.size() + " courses"));
		
		return courses;
	}
	
	/**
	 * Scrapes the main courses endpoint for a list of all sub-catalogs.
	 *
	 * @return A list of absolute URLs to each subsequent catalog.
	 * @throws IOException If something goes wrong scraping.
	 */
	private List<String> scrapeCatalogList() throws IOException {
		Document doc = Jsoup.parse(new java.net.URL(URL), 60000);
		
		// This block of code gets links to all the catalogs
		List<String> catalogs = new ArrayList<>();
		doc.body().select("#atozindex").select("ul").forEach(list -> {
			list.select("li").forEach(entry -> {
				catalogs.add(entry.select("a").first().absUrl("href"));
			});
		});
		
		return catalogs;
	}
	
	/**
	 * Scrapes individual courses from a catalog page, eg "https://catalog.gmu.edu/courses/cs/"
	 *
	 * @param link The absolute link of the catalog.
	 */
	private List<Course> scrapeCourses(String link) throws IOException {
		Document doc = Jsoup.parse(new java.net.URL(link), 60000);
		return doc.select(".courseblock").stream().map(Scraper::scrapeCourse).collect(Collectors.toList());
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
	 * @param coreqs A list that will be populated with strictly the coreqs of the course.
	 * @return A String to represent the pre-reqs.
	 */
	static String parsePrereqs(String block, List<String> prereqs, List<String> coreqs) {
		// Detect all conditions for minimum grades and preserve them for later removal.
		Map<String, String> conditions = new HashMap<>();
		Pattern pattern = Pattern.compile("([A-Z]+[-+]?) Requires minimum grade of [A-Z]+[-+]?\\.?");
		Matcher matcher = pattern.matcher(block);
		while (matcher.find()) {
			block = matcher.replaceFirst("");
			conditions.put(matcher.group(1), matcher.group(0));
			matcher = pattern.matcher(block);
		}
		
		// Hardcode concurrent condition.
		block = block.replace("* May be taken concurrently.", "");
		conditions.put("*", "");
		
		// Also replace any test pre-requisites.
		block = block.replaceAll("minimum score of \\d+ in '.*'", "");
		// Basic cleanup.
		block = block.replaceAll("[.,]", "");
		
		StringBuilder out = new StringBuilder();
		
		// Detects course numbers, which sometimes include letters.
		Pattern numberPattern = Pattern.compile("^[LU]?[0-9]{3}[LUT" + Pattern.quote(String.join("", conditions.keySet())) + "]?");
		// Detects course names, like ACCT or CS.
		Pattern classPattern = Pattern.compile("^[A-Z]{1,4}");
		
		// Reusable matcher for course names
		Matcher cm;
		// Reusable matcher for course numbers
		Matcher nm;
		
		String type = "";
		String join = "";
		List<String> names = new ArrayList<>();
		
		// Pop classes on: parenthesis or string end
		while (!block.isEmpty()) {
			if (Character.isSpaceChar(block.charAt(0))) {
				block = block.substring(1);
			} else if (block.charAt(0) == '(' || block.charAt(0) == ')') {
				if (!names.isEmpty()) {
					if (names.size() == 1) {
						out.append(names.get(0) + join); // for (A&(...)) blocks
					} else {
						out.append(String.join(join, names));
					}
					prereqs.addAll(names);
					names.clear();
					join = "";
				} else if (!join.isEmpty()) {
					out.append(join);
					join = "";
				}
				out.append(block.charAt(0));
				block = block.substring(1);
			} else if (block.startsWith("or")) {
				join = "|";
				block = block.substring("or".length());
			} else if (block.startsWith("and")) {
				join = "&";
				block = block.substring("and".length());
			} else if ((nm = numberPattern.matcher(block)).find()) { // number encountered
				String group = nm.group();
				String name = type + "-" + group.replaceAll("[" + Pattern.quote(String.join("", conditions.keySet())) + "]$", "");
				names.add(name);
				
				if (group.contains("*")) {
					coreqs.add(name);
				}
				
				block = block.substring(group.length());
			} else if ((cm = classPattern.matcher(block)).find()) { // class encountered
				String found = cm.group();
				block = block.substring(found.length());
				
				if (!conditions.containsKey(found)) {
					type = cm.group();
				}
			} else {
				block = block.substring(1);
			}
		}
		
		if (!names.isEmpty()) {
			out.append(String.join(join, names));
			prereqs.addAll(names);
			names.clear();
		}
		
		return out.toString();
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
				name.split(" ")[0] + "-" + name.split(" ")[1],
				prereqs,
				coreqs,
				0, // TODO flag method
				parents
		);
	}
	
	/**
	 * Drops all entries in the course table.
	 */
	private void dump() throws SQLException, IOException {
		Connection c = db.get();
		c.createStatement().execute("DELETE FROM course;");
	}
	
	/**
	 * Saves a list of courses to the database.
	 *
	 * @param courses The courses to save.
	 * @throws SQLException If something goes wrong saving the courses.
	 */
	void save(List<Course> courses) throws SQLException, IOException {
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO course (code, name, number, type, credits, description, prereqs, coreqs, flag, parents) VALUES");
		for (Course ignored : courses) {
			builder.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?),");
		}
		builder.insert(builder.lastIndexOf(","), ";");
		
		Connection c = db.get();
		PreparedStatement statement = c.prepareStatement(builder.toString());
		
		int i = 1;
		for (Course course : courses) {
			statement.setString(i++, course.getCode());
			statement.setString(i++, course.getFullName());
			statement.setInt(i++, Integer.parseInt(course.getName().split(" ")[1]));
			statement.setString(i++, course.getType());
			statement.setInt(i++, course.getCredits());
			statement.setString(i++, course.getDesc());
			statement.setString(i++, String.join(",", course.getPrerequisites()));
			statement.setString(i++, String.join(",", course.getCoreqs()));
			statement.setInt(i++, course.getFlag());
			statement.setString(i++, course.getParents());
		}
		
		System.out.println("Executing... ");
		statement.execute();
		System.out.println("Done.");
	}
	
	/**
	 * @return The LocalDateTime the scraper was last run, or a year ago if it was never run.
	 * @throws IOException If the file couldn't be read.
	 */
	public static LocalDateTime getLastRun() {
		LocalDateTime timeLastRun = LocalDateTime.now().minusYears(1);
		try {
			File dateFile = LocalStorage.get("dateLastRun.txt");
			String line;
			BufferedReader br = new BufferedReader(new FileReader(dateFile));
			line = br.readLine();
			if (line != null)
				timeLastRun = LocalDateTime.parse(line, DTF);
		} catch (IOException ex) {
			System.out.println("IOException in Scraper.getLastRun()\n");
			ex.printStackTrace();
		}
		return timeLastRun;
	}
	
	/**
	 * @param time The time to write.
	 * @throws IOException If the file couldn't be written.
	 */
	public static void setLastRun(LocalDateTime time) throws IOException {
		FileWriter fw = new FileWriter(LocalStorage.get("dateLastRun.txt"));
		fw.write(DTF.format(time));
		fw.close();
	}
	
	/**
	 * @return Whether or not the scraper needs to run.
	 */
	public boolean needsToRun() {
		return getLastRun().isBefore(LocalDateTime.now().minusMonths(3));
	}
	
	// For testing purposes only.
	static String parsePrereqs(String block) {
		return parsePrereqs(block, new ArrayList<>(), new ArrayList<>());
	}
	
	class Progress {
		private Float percent;
		private String message;
		
		Progress(Float percent, String message) {
			this.percent = percent;
			this.message = message;
		}
		
		public Float getPercent() {
			return percent;
		}
		
		public String getMessage() {
			return message;
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("Progress{");
			sb.append("percent=").append(percent);
			sb.append(", message='").append(message).append('\'');
			sb.append('}');
			return sb.toString();
		}
		
	}
	
}
