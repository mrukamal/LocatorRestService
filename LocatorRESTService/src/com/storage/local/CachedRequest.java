package com.storage.local;

import java.util.Date;

public class CachedRequest {
	private String latitude;
	private String longitude;
	private String address;
	private Date lookupDateTime;
	
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Date getLookupDateTime() {
		return lookupDateTime;
	}
	public void setLookupDateTime(Date lookupDateTime) {
		this.lookupDateTime = lookupDateTime;
	}
}
