package WindParsing.WindParsing;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

/**
 * This class could be the handler for an AWS Lambda function powering an Alexa
 * Skills Kit experience. To do this, simply set the handler field in the AWS
 * Lambda console to "helloworld.HelloWorldSpeechletRequestStreamHandler" For
 * this to work, you'll also need to build this project using the
 * {@code lambda-compile} Ant task and upload the resulting zip file to power
 * your function.
 */

/*
 * this class will handle all incoming requests, like a que... and then send
 * them over to app.java to run whichever specific method
 * 
 * KP 12/17/2016
 */

public class WindParsingRequestStreamHandler extends SpeechletRequestStreamHandler {

	private static final Set<String> supportedApplicationIds = new HashSet<String>();
	static {
		/*
		 * This Id can be found on https://developer.amazon.com/edw/home.html#/
		 * "Edit" the relevant Alexa Skill and put the relevant Application Ids
		 * in this Set.
		 */
		supportedApplicationIds.add("amzn1.echo-sdk-ams.app.[unique-value-here]");
	}

	public WindParsingRequestStreamHandler() {
		super(new App(), supportedApplicationIds);
	}

}
