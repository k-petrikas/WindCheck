# WindCheck

this will be a java web app for amazon Echo to ask "whats the wind" and return the current wind speed on the chesapeake bay

## Main method (app.java)
this is the applicatinos main method and currently when run will kick off two methods one to return the file in a parsed format using the syndication.feed jar library `

#### Functions:
* **readRSS:** get the website RSS feed from the Annapolis bay buoy and returns a SyndFeed object
* **sendSyndFeedToArrayList:** sends the SyndFeed object to an array list so information can quickly be extracted (specifically the wind speed)
