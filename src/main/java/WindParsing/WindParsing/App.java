
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
		System.out.println("rss feed raw from rome: ");
		String stringWebsiteURL = "http://buoybay.noaa.gov/locations/rss/AN";
		SyndFeed feed = readRSS(stringWebsiteURL);

		// send rss feed to aray list
		JSONObject parsedXMLJSON = sendSyndFeedToJsonObject(feed);
		// String[][] arrayOfRSSDataValues = sendSyndFeedToArrayList(feed);

		// output of the array list
		System.out.println("wind speed is:");
		System.out.println(parsedXMLJSON.getJSONObject("Wind Speed").get("description"));

		TESTMETHOD();

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
			// System.out.println(entry.getTitle());
			// System.out.println(indexASOF);

			String trimedTitle = entry.getTitle().substring(0, indexASOF);
			// System.out.println(trimedTitle + ":");

			try {

				JSONObject jsonContent = new JSONObject();
				jsonContent.put("title", entry.getTitle());
				jsonContent.put("url", entry.getUri());
				jsonContent.put("description", entry.getDescription().getValue());

				jsonHeader.put(trimedTitle, jsonContent);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
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

	private static String[][] sendSyndFeedToArrayList(SyndFeed feed) {
		/*
		 * this method will put the parsed rss feed into an array of arrays in
		 * order to manipulate the data for later use
		 */
		int i = 0;
		String[][] arrayOfRSSDataValues = new String[26][];
		for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {
			// System.out.println("\n" + i + ":");
			String title = entry.getTitle();
			String uri = entry.getUri();
			String description = entry.getDescription().getValue();
			// // ... you should include all values here... will be all lines
			// that need to get added to the parsed array list

			arrayOfRSSDataValues[i] = new String[6];
			arrayOfRSSDataValues[i][0] = title;
			arrayOfRSSDataValues[i][1] = uri;
			arrayOfRSSDataValues[i][2] = description;

			// System.out.println("title: " + title);
			// System.out.println("uri: " + uri);
			// System.out.println("description: " + description);

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

		// return the wind speed from JSON
		try {
			return parsedXMLJSON.getJSONObject("Wind Speed").get("description").toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "error finding wind data";
		}
	}

	/*
	 * we will need to fill in the below intents...
	 * 
	 * TODO: fill in below with stuff that returns values...
	 */

	// FIXME: not sure if we need a log and or logger factory KP: 12/17/2016
	private static final Logger log = LoggerFactory.getLogger(App.class);

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

		Intent intent = request.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;

		if ("SailingWindForecast".equals(intentName)) {
			return getWindSpeed();
		} else if ("ChesapeakBuoySystem".equals(intentName)) {
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
		// TODO: probably should dump all parsed wind data
	}

	@Override
	public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
		log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
		// any initialization logic goes here
		// TODO: probably should get the latest wind data and parse it and store
		// it for later here...

	}

	/**
	 * Creates and returns a {@code SpeechletResponse} with a welcome message.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	private SpeechletResponse getWelcomeResponse() {
		String speechText = "You can ask for the current windspeed by saying, get wind speed";

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

		String urlCodeForBuoyLocation = ChesapeakBayBuoyLocations.get("ANNAPOLIS").toString();

		windSpeed = getWindSpeedForParticularBuoy(urlCodeForBuoyLocation);

		String speechText = "The current wind speed in annapolis maryland is " + windSpeed;

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

		// String buoyLocation = "annapolis";
		// Annapolis
		// buoyLocation = buoyLocation.toUpperCase();
		// String urlCodeForBuoyLocation =
		// ChesapeakBayBuoyLocations.get(buoyLocation).toString();

		windSpeed = getWindSpeedForParticularBuoy(urlCodeForBuoyLocation);

		String speechText = "The current wind speed for " + buoyLocation + " is " + windSpeed;

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