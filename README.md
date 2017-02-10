# LocatorRestService
RESTful web service (HTTP) which is capable of looking up a physical street address given a set of geographic coordinates (longitude and latitude values).  Uses Google Maps Reverse Geocode API.

# How To Run
1) To run from the command line, download the LocatorRestServicejar.jar to a location where the JRE is accessible via ClassPath. Then type java -jar LocatorRestServicejar.jar and hit Enter. The project jar is self contained and runs independent of a web server or web container.

OR

2) Setup an Eclipse project named "LocatorRESTService" (Eclipse Luna version used) and download the LocatorRESTService (and sub-folders) from the repository. Then run the Main class AppServer.java under the com.rest.app package. The project is also self contained and depends only on Maven to provide the correct libraries at runtime. Please review the pom.xml file in the project for more details.

# Usage
Once the jar or project is running the following REST API services are accessible:

http://localhost:8080/addresslookup/

Displays a location's address by providing its latitude and longitude values in the request. For example, http://localhost:8080/addresslookup/33.969601/-84.100033 . This uses Google's Geocode Reverse API service from https://maps.googleapis.com/maps/api/geocode/xml?

http://localhost:8080/recentrequests
Displays the last 10 lookups performed by the addresslookup service above.

# Contact
In case of any issues, please feel  free to contact me at mr.ukamal@gmail.com

Thanks and enjoy!

Umair Kamal
