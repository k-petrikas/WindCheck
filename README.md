# WindCheck

This will be a java web application for amazon Echo to ask "What is the wind speed?" and return the current wind speed on the Chesapeake Bay.

## Current Function
At present, this application pulls the barometric pressure, temperature, wind direction, speed, and time, among other variables, and displays it in the console. The data is pulled from [this NOAA RSS feed](http://buoybay.noaa.gov/locations/rss/AN).

## Next Steps

1. Design a Voice User Interface
  - Create a set of intents
  - Create sample utterances
2. Register the skill
  - Come up with an invocation name	
3. Figure out the cloud hosting
4. Create a configuration in the developer portal
5. Create the Alexa Skill
  - Parse the data
  - Create a JSON object
  - Include test code	
6. Submit

## Resources
* [Alexa Skills Kit](https://developer.amazon.com/alexa-skills-kit#Ready%20to%20start%3F)
* [Understanding Custom Skills](https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/overviews/understanding-custom-skills)
* [Steps to Build a Custom Skill](https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/overviews/steps-to-build-a-custom-skill)


## How To Run
This application has been developed in Eclipse (Java) and built using Maven. In order to modify and run the program, [Java Development Toolkit (jdk)](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html), [Eclipse](https://eclipse.org/downloads/), and [Maven](http://maven.apache.org/download.cgi) must first be installed. Additional Maven instructions can be found [here](https://www.mkyong.com/maven/how-to-install-maven-in-windows/).

### Instructions:
* Install Java.
* Install Eclipse.
* Install Maven.
* Open Eclipse and use the feature that allows you to pull from a git repo to pull this repo.
* Project -> Clean
* Make sure Project -> Build automatically is active
* Project -> Properties -> Java Build Path -> Libraries: *Remove any external libraries you have ever added.* Don't remove standard libraries like the JRE System Library.
* Run the main class.

### Notes
That should hopefully work. I ran into a few snags trying to run it myself including my Maven PATH variable not working (opted to hard code the PATH variable rather than using the %M2_HOME% variable) and receiving an error regarding the program not finding the main class. These directions were included above. The solution was originally found [here](http://stackoverflow.com/questions/11235827/eclipse-error-could-not-find-or-load-main-class).
