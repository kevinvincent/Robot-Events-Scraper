
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

class Match {
	public String name;
	public String time;
	public String redA;
	public String redB;
	public String blueA;
	public String blueB;
	
	public Match(String name, String time, String redA, String redB, Striing blueA, String blueB) {
		this.name = name;
		this.time = time;
		this.redA = redA;
		this.redB = redB;
		this.blueA = blueA;
		this.blueB = blueB;
	}
}

class Event {
	public String date;
	public String location;
	public String name;
	public String url;
	public String sku;
	public int numberOfDivisions;
	public ArrayList<String> eventDetails = new ArrayList<String>();
	public ArrayList<Match> eventMatches = new ArrayList<Match>();
	
	
	public Event(String date, String location, String name, String url) {
		this.date = date;
		this.location = location;
		this.name = name;
		this.url = url;
	}
	
	public void reloadEventDetails() throws IOException {
		// Get HTML
				Document doc = Jsoup.connect(url).userAgent("Mozilla").timeout(20*1000).get();
				
		//Get the SKU
				sku = doc.select("div[class=product-sku]").text().split(":")[1].replaceAll("\\s+","");
				
		//Count number of divisions
				 numberOfDivisions = doc.select("div[id^=division]").size()-1;
				 
		//Get Event Details
				 Element table = doc.select("table").get(0);
				 eventDetails = new ArrayList<String>();
				 for (Element row : table.select("tr:gt(0)")) eventDetails.add(row.text());
		
		//Get Matches
				 for(int i = 1; i <= numberOfDivisions; i++) {
					Elements matchbits = doc.select("div[class=matchbit]");
					for(Element matchbit : matchbits) {
						int numberOfColumns = matchbit.select("div").size();
						if( numberOfColumns == 10) { 
							System.out.println(matchbit.select("div").first().text());
						}
					}
				 }
	}
	
	public String toString() {
		return date+" - "+location+" - "+name+ " - "+url;
	}
}

/* ------------------------------------ END MAIN -------------------------------------- */
public class RobotEventsScraper {
	
	public static void main(String args[]) throws IOException {
		Document doc = Jsoup.connect("http://www.robotevents.com/2013-vex-robotics-high-school-world-championship.html").userAgent("Mozilla").timeout(20*1000).get();
		int numberOfDivisions = doc.select("div[id^=division]").size()-1;
		for(int i = 1; i <= numberOfDivisions; i++) {
			Elements matchbits = doc.select("div[class=matchbit]");
			for(Element matchbit : matchbits) {
				int numberOfColumns = matchbit.select("div").size();
				if( numberOfColumns == 10) { 
					System.out.println(matchbit.select("div").first().text());
				}
			}
		 }
	}
}

/* ------------------------------------ END MAIN -------------------------------------- */
class ScraperUtils {
	public static ArrayList<Event> getEventList() {
		Document doc;
		ArrayList<Event> events = new ArrayList<Event>();
		try {
	 
			// Get HTML
			doc = Jsoup.connect("http://www.robotevents.com/robot-competitions/vex-robotics-competition?limit=all").userAgent("Mozilla").timeout(20*1000).get();
			 
			//Find the first table
			for (Element table : doc.select("table")) {
				
				 //Get all the rows leaving out the first 3
			     for (Element row : table.select("tr:gt(2)")) {
			    	 
			    	//Pull data and add to list
			        Elements tds = row.select("td");
			        if(tds.size() != 3) break;
			        
			        Event theEvent = new Event(tds.get(0).text(),tds.get(1).text(),tds.get(2).text(),tds.get(2).select("h5").select("a").attr("href"));
			        events.add(theEvent);
			     }
			}
	 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return events;
	}
	
}