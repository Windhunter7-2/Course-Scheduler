package CourseScheduler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
		return doc.select(".courseblock").stream().map(this::scrapeCourse).collect(Collectors.toList());
	}
	
	/**
	 * Scrapes an individual course element and transforms it into a Course.
	 *
	 * @param element The element to scrape.
	 * @return A Course representing the element.
	 */
	private Course scrapeCourse(Element element) {
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
		
		List<ScrapedPrereq> prereqs = new ArrayList<>();
		
		String preTitle = null;
		List<String> conditions = new ArrayList<>();
		
		if (element.selectFirst(".prereq") != null) {
			for (Element e : element.selectFirst(".prereq").children()) {
				if (e.className().equals("a")) {
					if (preTitle != null) {
						// Push the current one
						prereqs.add(new ScrapedPrereq(preTitle, conditions));
					}
					preTitle = e.attr("title");
				} else if (e.className().equals("pre")) {
					conditions.add(e.text());
				}
			}
		}
		
		if (preTitle != null) {
			// Push the last one
			prereqs.add(new ScrapedPrereq(preTitle, conditions));
		}
		
		return new Course(
				name, title, name.split(" ")[0], credits, desc, name.split(" ")[0] + name.split(" ")[1], Collections.emptyList(), Collections.emptyList(), 0
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
