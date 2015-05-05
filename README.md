# Tagline:

"Exploring the surrounding digital footprint"

# Core Problem:

Social media is a major part of today's digital landscape. There is currently no way to view social media sources such as Twitter in a location-based manner. Traces fills that gap by giving the user a graphical, location-based window into the Twitterverse. Our application queries Twitter's servers, and displays to a user all of the tweets that have been made within a 1 mile radius of their current location. The user can also search for tweets in a different location either by long pressing on the map, quick search, or using an advance search.Tweets can also be viewed in a list view, sorting the most popular tweets at the top of the list.

# How:

This application was developed in Android Studio. All of us were new to Android development at the beginning of this project, however, we really like the way the final product turned out.The backend is written in Go, and runs on Google App Engine.The back-end produces a JSON response from the Twitter API,which the Android app consumes, then displays the results to the user in an easy to interface.

# Future work:

In an ideal world, we would have liked to have been able to allow users to pin, and save tweets of interest. We also would have liked to allow the user to share interesting tweets via other social media sites, implement a Webview to allow links to photos and videos to be visible in the list view (or have the links open in a web browser), cluster nearby tweets together and display each one individually when zoomed, and a unified display design to brand our application better.

# Link to app:
https://play.google.com/store/apps/details?id=edu.ncsu.mobile.traces

# Link to video:
http://youtu.be/796xuZgvIxc

# Link to GitHub:
https://github.com/enj/traces

# References:

- Google App Engine
- Google Play Services
- Google Map API
- Android David Webb REST - https://github.com/hgoebl/DavidWebb
- Google Geocoder API / Golang Geo - https://github.com/kellydunn/golang-geo
- Twitter API - Anaconda - https://github.com/ChimeraCoder/anaconda
- Google GSON - https://github.com/google/gson
- Ion Framework - https://github.com/koush/ion
- Golang JSON-REST - https://github.com/ant0ine/go-json-rest
