
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

class Match {
	
	public int div;
	public String type;
	public String id;
	public String time;
	public String redA;
	public String redB;
	public String redC;
	public String blueA;
	public String blueB;
	public String blueC;
	
	public Match(int div, String type, String id, String time, String redA, String redB, String redC, String blueA, String blueB, String blueC) {
		this.div = div;
		this.type = type;
		this.id = id;
		this.time = time;
		this.redA = redA;
		this.redB = redB;
		this.redC = redC;
		this.blueA = blueA;
		this.blueB = blueB;
		this.blueC = blueC;
	}
	
	public String toString() {
		return id + " - " +time + " - " +redA + " - " +redB + " - " +redC + " - " +blueA + " - " +blueB + " - " +blueC;
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
				 System.out.println("Getting matches..");
				 for(int i = 1; i <= numberOfDivisions; i++) {
					 	System.out.println("Getting matches for division " + i);
					 	doc = Jsoup.connect("http://ajax.robotevents.com/tm/results/matches/?format=html&div="+i+"&sku="+sku).userAgent("Mozilla").timeout(20*1000).get();
						Elements matchbits = doc.select("div[class=matchbit]");
						
						for(Element matchbit : matchbits) {
							if(matchbit.select("div[class^=matchcol]").size() > 9) {
									
								String[] matchTitle = matchbit.select("div[class^=matchcol]").get(0).text().split(" ");
								String redA = matchbit.select("div[class^=matchcol]").get(1).text();
								String redB = matchbit.select("div[class^=matchcol]").get(2).text();
								String redC = matchbit.select("div[class^=matchcol]").get(3).text();
								String blueA = matchbit.select("div[class^=matchcol]").get(4).text();
								String blueB = matchbit.select("div[class^=matchcol]").get(5).text();
								String blueC = matchbit.select("div[class^=matchcol]").get(6).text();
								
								if(matchTitle.length==3) {
									eventMatches.add(new Match(i,matchTitle[0],matchTitle[1],matchTitle[2],redA,redB,redC,blueA,blueB,blueC));
								}
								else {
									eventMatches.add(new Match(i,matchTitle[0],matchTitle[1],null,redA,redB,redC,blueA,blueB,blueC));
								}
							}
						}
					 }
	}
	
	public String toString() {
		return date+" - "+location+" - "+name+ " - "+url;
	}
}

/* ------------------------------------ Start MAIN -------------------------------------- */
public class RobotEventsScraper {
	
	public static void main(String args[]) throws IOException {
		ArrayList<Event> events = ScraperUtils.getEventList();
		events.get(100).url="http://www.robotevents.com/2013-vex-robotics-high-school-world-championship.html";
		events.get(100).reloadEventDetails();
		System.out.println(events.get(100).eventMatches.get(2).toString());
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