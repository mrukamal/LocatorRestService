package com.locator.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.storage.local.CachedRequest;

@Path("/recentrequests")
public class CacheLocatorService {
	@GET
    @Produces("application/json")
	public Response retrieveRecentLookups() {
		JSONObject jsonObject = new JSONObject(); 		
 
		if(AddressLookupService.cachedLookups != null && !AddressLookupService.cachedLookups.isEmpty())
		{
			jsonObject.put("recentLookups", AddressLookupService.cachedLookups);
			String result = jsonObject.toString();
			return Response.status(200).entity(result).build();
		}
		else
		{
			CachedRequest cachedRequest = new CachedRequest();
			cachedRequest.setLatitude("n/a");
			cachedRequest.setLongitude("n/a");
			cachedRequest.setAddress("No recent lookups.");
			cachedRequest.setLookupDateTime(Calendar.getInstance().getTime());
			List<CachedRequest> cachedLookups = new ArrayList<CachedRequest>();
			cachedLookups.add(cachedRequest);
			jsonObject.put("recentLookups", cachedLookups);
			String result = jsonObject.toString();
			return Response.status(200).entity(result).build();
		}
	}
	
}
