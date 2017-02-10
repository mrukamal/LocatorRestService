package com.locator.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.storage.local.CachedRequest;

@Path("/addresslookup")
public class AddressLookupService {
	// private String serviceEndPoint =
	// "http://maps.google.com/maps/api/geocode/xml?";
	private String serviceEndPoint = "https://maps.googleapis.com/maps/api/geocode/xml?";
	private String latitudeRegex = "^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$";
	private String longitudeRegex = "^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$";
	public static List<CachedRequest> cachedLookups;

	@Path("/")
	@GET
	public Response displayUsageMessage() {
		Response response = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).build();
		JSONObject jsonStatus = new JSONObject();
		jsonStatus.put("status", Response.Status.BAD_REQUEST);	
		String result = buildResponseJSON(jsonStatus, response.getStatus(), "Failed to process your request: Please enter latitude and longitude values at the end of the above URL to determine physical address. Example 33.969601/84.445555", null);
		return Response.fromResponse(response).entity(result).build();

	}
	
	/*
	 * This method never gets called but prevents the user from entering one parameter in the request URI
	 */
	@Path("{input}")
	@GET
	public Response displayBadRequestMessage(@PathParam("input") String input) {
		Response response = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).build();
		JSONObject jsonStatus = new JSONObject();
		jsonStatus.put("status", Response.Status.BAD_REQUEST);	
		String result = buildResponseJSON(jsonStatus, response.getStatus(), "Failed to process your request: You did not supply one of the parameters to fulfill the request. Please enter latitude and longitude values at the end of the above URL to determine physical address. Example 33.969601/84.445555", null);
		return Response.fromResponse(response).entity(result).build();

	}

	@Path("{latitude}/{longitude}")
	@GET
	@Produces("text/plain")
	public String findAddressBasedOnCoordinates(
			@PathParam("latitude") String latitude,
			@PathParam("longitude") String longitude) {

		if (isDouble(latitude) && isDouble(longitude)) {
			DecimalFormat decimalFormat = new DecimalFormat("#.######");
			if (decimalFormat.format(Double.parseDouble(latitude)).matches(
					latitudeRegex)
					&& decimalFormat.format(Double.parseDouble(longitude))
							.matches(longitudeRegex)) {
				String address = findNearestAddress(latitude, longitude);
				CachedRequest cachedRequest = new CachedRequest();
				cachedRequest.setLatitude(latitude);
				cachedRequest.setLongitude(longitude);
				cachedRequest.setAddress(address);
				cachedRequest.setLookupDateTime(Calendar.getInstance()
						.getTime());
				if (cachedLookups == null) {
					cachedLookups = new ArrayList<CachedRequest>();
				}
				cachedLookups.add(0, cachedRequest);
				if (cachedLookups.size() > 10) {
					cachedLookups.remove(cachedLookups.size() - 1);
				}
				return address;
			} else {
				Response response = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).build();
				JSONObject jsonStatus = new JSONObject();			
				jsonStatus.put("status", Response.Status.BAD_REQUEST);	
				return buildResponseJSON(jsonStatus, response.getStatus(), "Failed to process your request: Please enter correct format for the latitude and longitude values. Example 33.969601/84.445555", null);
			}
		} else {
			//return "Failed to process your request: Please enter correct format for the latitude and longitude values. Example 33.969601/84.445555";
			Response response = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).build();
			JSONObject jsonStatus = new JSONObject();			
			jsonStatus.put("status", Response.Status.BAD_REQUEST);	
			return buildResponseJSON(jsonStatus, response.getStatus(), "Failed to process your request: Please enter correct format for the latitude and longitude values. Example 33.969601/84.445555", null);


		}
	}

	private String findNearestAddress(String latitude, String longitude) {
		String result = null;
		try {
			URL url = new URL(
					serviceEndPoint
							+ "latlng="
							+ URLEncoder.encode(latitude, "UTF-8")
							+ ","
							+ URLEncoder.encode(longitude, "UTF-8")
							+ "&sensor=false&key=AIzaSyC7bILwr4vLvnIJ8dszhwGy74AgZCG8yGI");
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/xml");

			if (connection.getResponseCode() != 200) {
				//throw new RuntimeException("Failed to process your request: A connection error was encountered, HTTP error code : " + connection.getResponseCode());
				JSONObject jsonStatus = new JSONObject();
				jsonStatus.put("status", Response.Status.BAD_REQUEST);
				return buildResponseJSON(jsonStatus, connection.getResponseCode(), "Failed to process your request: A connection error was encountered", null);
			}

			SAXBuilder builder = new SAXBuilder();
			Document document = (Document) builder.build(connection
					.getInputStream());
			Element rootNode = document.getRootElement();
			Element statusElement = rootNode.getChild("status");
			if ("ok".equals(statusElement.getText().toLowerCase())) {
				Element firstResult = rootNode.getChild("result");
				Element formattedAddress = firstResult
						.getChild("formatted_address");
				result = formattedAddress.getText();
			} else {
				Response response = Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).build();
				JSONObject jsonStatus = new JSONObject();
				jsonStatus.put("status", Response.Status.OK);
				result = buildResponseJSON(jsonStatus, response.getStatus(), "Location not found. Please verify the latitude and longitude values then try your request again.", null);
			}

			connection.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
			//throw new RuntimeException("Failed to process your request: A malformed URL was encountered "	+ e);
			
			Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).build();
			JSONObject jsonStatus = new JSONObject();
			jsonStatus.put("status", Response.Status.INTERNAL_SERVER_ERROR);
			result = buildResponseJSON(jsonStatus, response.getStatus(), "Failed to process your request: A malformed URL was encountered.", e.toString());

		} catch (IOException e) {
			e.printStackTrace();
			//throw new RuntimeException("Failed to process your request: An IO error was encountered while retrieving XML stream " + e);
			Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).build();
			JSONObject jsonStatus = new JSONObject();
			jsonStatus.put("status", Response.Status.INTERNAL_SERVER_ERROR);
			result = buildResponseJSON(jsonStatus, response.getStatus(), "Failed to process your request: An IO error was encountered while retrieving XML stream.", e.toString());

		} catch (JDOMException e) {
			e.printStackTrace();
			//throw new RuntimeException("Failed to process your request: An XML parsing error was encountered " + e);
			Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).build();
			JSONObject jsonStatus = new JSONObject();
			jsonStatus.put("status", Response.Status.INTERNAL_SERVER_ERROR);
			result = buildResponseJSON(jsonStatus, response.getStatus(), "Failed to process your request: An XML parsing error was encountered.", e.toString());
		}
		return result;
	}

	private boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	private String buildResponseJSON(JSONObject jsonStatus, int statusCode, String message, String developerMessage)
	{		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonHttpCode = new JSONObject();
		JSONObject jsonMessage = new JSONObject();
		JSONObject jsonDeveloperMessage = new JSONObject();
		jsonHttpCode.put("httpCode", statusCode);
		jsonMessage.put("message", "Location not found. Please verify the latitude and longitude values then try your request again.");
		jsonArray.put(jsonHttpCode);
		jsonArray.put(jsonStatus);
		jsonArray.put(jsonMessage);
		if(developerMessage != null && !developerMessage.isEmpty())
		{
			jsonDeveloperMessage.put("developerMessage", developerMessage);
			jsonArray.put(jsonDeveloperMessage);
		}
		return jsonArray.toString();
	}

}
