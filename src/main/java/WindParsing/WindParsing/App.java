
package WindParsing.WindParsing;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * this class contains the main method for windParsing
 *
 */

public class App {
	public static void main(String args[]) {
		
		/*
		 * main method currently returns 
		 * 
		 * in the future this class with probably just become an self instantiation
		 */

		// get the rss feed from website
		System.out.println("rss feed raw from rome: ");
		String stringWebsiteURL = "http://buoybay.noaa.gov/locations/rss/AN";
		SyndFeed feed = readRSS(stringWebsiteURL);
		
		// send rss feed to aray list
		String[][] arrayOfRSSDataValues = sendSyndFeedToArrayList(feed);

		
		//output of the array list
		System.out.println("wind speed is:");
		System.out.println(arrayOfRSSDataValues[24][2]);

		// from the feed pull out the information you need
		System.out.println("\ntitle:");
		System.out.println(arrayOfRSSDataValues[24][0]);

		
		// parce of an existing document for practice
		// try {
		// File fXmlFile = new File("testXMLs/NOAADataXML.xml");
		//
		// DocumentBuilderFactory dbFactory =
		// DocumentBuilderFactory.newInstance();
		// DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		// Document doc = dBuilder.parse(fXmlFile);
		//
		// doc.getDocumentElement().normalize();
		//
		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		// NodeList nList = doc.getElementsByTagName("item");
		//
		// //get the wind speed
		// Node windNode = nList.item(24);
		// if (windNode.getNodeType() == Node.ELEMENT_NODE) {
		//
		// Element eElement = (Element) windNode;
		//
		// System.out.println(eElement.getElementsByTagName("title").item(0).getTextContent());
		// System.out.println("wind speed : " +
		// eElement.getElementsByTagName("description").item(0).getTextContent());
		//
		// }
		//
	
	}

	private static String[][] sendSyndFeedToArrayList(SyndFeed feed) {
		/*
		 * this method will put the parsed rss feed into an array of arrays in order to manipulate the data for later use
		 */
		int i = 0;
		String[][] arrayOfRSSDataValues = new String[26][];
		for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {
			System.out.println("\n" + i + ":");
			String title = entry.getTitle();
			String uri = entry.getUri();
			String description = entry.getDescription().getValue();
			// ...

			arrayOfRSSDataValues[i] = new String[6];
			arrayOfRSSDataValues[i][0] = title;
			arrayOfRSSDataValues[i][1] = uri;
			arrayOfRSSDataValues[i][2] = description;

			System.out.println("title: " + title);
			System.out.println("uri: " + uri);
			System.out.println("description: " + description);

			i++;
		}
		
		return arrayOfRSSDataValues;
	}

	private static SyndFeed readRSS(String websiteURL) {

		SyndFeed feed = null;
		try {
			URL feedSource = new URL(websiteURL);
			SyndFeedInput input = new SyndFeedInput();
			feed = input.build(new XmlReader(feedSource));

		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println(feed);
		return feed;

	}

}