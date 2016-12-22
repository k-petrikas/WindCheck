
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

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import Enum.ChesapeakBayBuoyLocations;

/**
 * this class contains the main method for windParsing
 *
 */

public class App implements Speechlet {

	String windSpeed = "";

	public static void main(String args[]) throws JSONException {

		/*
		 * main method currently returns
		 * 
		 * in the future this class with probably just become an self
		 * instantiation
		 */

		// get the rss feed from website
		String stringWebsiteURL = "http://buoybay.noaa.gov/locations/rss/AN";
		SyndFeed feed = readRSS(stringWebsiteURL);

		// send rss feed to aray list
		JSONObject parsedXMLJSON = sendSyndFeedToJsonObject(feed);

		// output of the array list
		System.out.println("wind speed is:");
		System.out.println(parsedXMLJSON.getJSONObject("Wind Speed").get("description"));

//		TESTMETHOD();

	}

	private static JSONObject sendSyndFeedToJsonObject(SyndFeed feed) {
		/*
		 * attempt to put synd feed into json object
		 */

		// this is the main json object that will get returned
		JSONObject jsonHeader = new JSONObject();

		// for each synd entry run through and get data
		for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {

			int indexASOF = entry.getTitle().indexOf("as of") - 1;

			// set variable for the json node name
			String trimedNodeTitle = entry.getTitle().substring(0, indexASOF);

			// figure out if data is out of date (magnitude is how far out of
			// date it is)
			String magnitude = entry.getTitle().substring(indexASOF + 9);

			// check to see how old data is
			boolean upToDate;
			if (magnitude.substring(0, 3).equals("min") || magnitude.substring(0, 3).equals("sec")
					|| magnitude.substring(1, 4).equals("min") || magnitude.substring(1, 4).equals("sec")) {
				upToDate = true;
			} else {
				upToDate = false;
			}

			try {
				JSONObject jsonContent = new JSONObject();
				jsonContent.put("title", entry.getTitle());
				jsonContent.put("url", entry.getUri());
				jsonContent.put("description", entry.getDescription().getValue());
				jsonContent.put("upToDate", upToDate);

				jsonHeader.put(trimedNodeTitle, jsonContent);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		// TESTING
		// System.out.println(jsonHeader.toString());

		// return json object
		return jsonHeader;

	}

	private static void TESTMETHOD() {

		String buoyLocation = "gooses reef".toUpperCase();
		// buoyLocation = buoyLocation.toUpperCase();
		String urlCodeForBuoyLocation = ChesapeakBayBuoyLocations.get(buoyLocation).toString();
		System.out.println("buoy location: " + urlCodeForBuoyLocation);

		System.out.println("wind speed of below should be:");
		System.out.println(getWindSpeedForParticularBuoy(urlCodeForBuoyLocation));
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

	private static String getWindSpeedForParticularBuoy(String urlCodeForBuoyLocation) {

		/*
		 * PRODUCTION CODE
		 * 
		 * this will contain all info for getting wind speed for buoy by area
		 * location code
		 * 
		 * will populate wind speed
		 */

		// get the rss feed from website
		String stringWebsiteURL = "http://buoybay.noaa.gov/locations/rss/" + urlCodeForBuoyLocation;
		SyndFeed feed = readRSS(stringWebsiteURL);

		// send rss feed to JSON object
		JSONObject parsedXMLJSON = sendSyndFeedToJsonObject(feed);
		
		// return wind speed, first perform logic check for out of date data
		try {
			if (parsedXMLJSON.getJSONObject("Wind Speed").get("upToDate").equals(false)) {
				return "not up to date. Last " + parsedXMLJSON.getJSONObject("Wind Speed").get("title").toString();
			} else {
				return parsedXMLJSON.getJSONObject("Wind Speed").get("description").toString();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return "error finding wind data for this Buoy";
		}
	}

	/*
	 * below is all the logic for ASK (allexa skills kit) there are 4 required
	 * methods: onIntent onLaunch onSessionStarted onSessionEnded
	 * 
	 */

	// FIXME: not sure if we need a log and or logger factory KP: 12/17/2016
	private static final Logger log = LoggerFactory.getLogger(App.class);

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

		Intent intent = request.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;

		if ("ChesapeakBuoySystem".equals(intentName)) {
			return getWindSpeedForSpecificLocation(request);
		} else if ("AMAZON.HelpIntent".equals(intentName)) {
			return getHelpResponse();
		} else {
			throw new SpeechletException("Invalid Intent");
		}
	}

	@Override
	public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
		log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
		return getWelcomeResponse();
	}

	@Override
	public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
		log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
		// any cleanup logic goes here
	}

	@Override
	public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
		log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
		// any initialization logic goes here

	}

	/**
	 * Creates and returns a {@code SpeechletResponse} with a welcome message.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	private SpeechletResponse getWelcomeResponse() {
		String speechText = "You can ask for the current windspeed by saying, get wind speed in ANNAPOLIS."
				+ "or by asking for any other Chesapeake NOAA bouy location";

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("Wind Speed");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);

		// Create reprompt
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(speech);

		return SpeechletResponse.newAskResponse(speech, reprompt, card);
	}

	/**
	 * Creates a {@code SpeechletResponse} for the SailingWindForecast intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	private SpeechletResponse getWindSpeed() {
		
		/*
		 * this method is no longer used... user should request specific buoy location for wind speed each time
		 */

		String urlCodeForBuoyLocation = ChesapeakBayBuoyLocations.get("ANNAPOLIS").toString();

		windSpeed = getWindSpeedForParticularBuoy(urlCodeForBuoyLocation);

		String speechText = "The current wind speed for annapolis maryland is " + windSpeed;

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("Wind Speed");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);

		return SpeechletResponse.newTellResponse(speech, card);
	}

	/**
	 * Creates a {@code SpeechletResponse} for the SailingWindForecast intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	private SpeechletResponse getHelpResponse() {
		String speechText = "You can ask for the current windspeed by saying, get wind speed!";

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("Wind Speed");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);

		// Create reprompt
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(speech);

		return SpeechletResponse.newAskResponse(speech, reprompt, card);
	}

	/**
	 * Creates a {@code SpeechletResponse} for the SailingWindForecast intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	private SpeechletResponse getWindSpeedForSpecificLocation(IntentRequest request) {
		/*
		 * this method will return data for specific buoy location
		 */

		/*
		 * get the slot location for request steralize the slot location (send
		 * it to upper)
		 */
		String buoyLocation = request.getIntent().getSlot("BuoyLocation").getValue().toString().toUpperCase();
		String urlCodeForBuoyLocation = ChesapeakBayBuoyLocations.get(buoyLocation).toString();

		// request wind speed for particular location
		windSpeed = getWindSpeedForParticularBuoy(urlCodeForBuoyLocation);

		
		
		String speechText = "The current wind speed at the " + buoyLocation + " buoy is " + windSpeed;

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("Wind Speed");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);

		return SpeechletResponse.newTellResponse(speech, card);
	}

}
