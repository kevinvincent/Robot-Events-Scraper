Robot-Events-Scraper
====================

Robot Events Scraper is a simple semantic library to fetch VEX Robotics event and match information through screen scraping

Usage
-----
Get list of all events

    ArrayList<Event> events = ScraperUtils.getEventList();
    
Get an Event

    events.get(0);

Load details for the event

    events.get(0).reloadEventDetails();
    
Congradulations, You can now access every bit of match information available from Robot Events including per-match data like scores and match times

Dependencies
------------
jsoup http://jsoup.org/ 
  - Make sure it's in your CLASSPATH.
