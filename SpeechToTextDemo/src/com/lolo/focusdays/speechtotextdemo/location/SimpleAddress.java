package com.lolo.focusdays.speechtotextdemo.location;

import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;
import com.lolo.focusdays.speechtotextdemo.MainActivity;

public final class SimpleAddress extends Intent {
	
	public SimpleAddress(Intent o) {
		super(o);
		this.setAddress(o.getStringExtra("currentLocationAddress"));
		this.setZip(o.getStringExtra("currentLocationZip"));
		this.setCity(o.getStringExtra("currentLocationCity"));
		this.setCountry(o.getStringExtra("currentLocationCountry"));
		
		this.position = new LatLng(
				o.getDoubleExtra("marker.latitude", 0.0),
				o.getDoubleExtra("marker.longitude", 0.0));
		
	}

	private String address;
	private String zip;
	private String city;
	private String country;
	private LatLng position;

	public SimpleAddress(LatLng position, String address, String zip, String city, String country) {
		this.position = position;
		this.address = address;
		this.zip = zip;
		this.city = city;
		this.country = country;
		
	}

	public SimpleAddress(MainActivity mainActivity,
			Class<MyLocationDemoActivity> class1) {
		super(mainActivity, class1);
		if (mainActivity.getCurrentLocation() != null) {
			this.putExtra("currentLocationAddress", mainActivity.getCurrentLocation().getAddress());
			this.putExtra("currentLocationZip", mainActivity.getCurrentLocation().getZip());
			this.putExtra("currentLocationCity", mainActivity.getCurrentLocation().getCity());
			this.putExtra("currentLocationCountry", mainActivity.getCurrentLocation().getCountry());
			this.putExtra("marker.longitude", mainActivity.getCurrentLocation().getPosition().longitude);
			this.putExtra("marker.latitude", mainActivity.getCurrentLocation().getPosition().latitude);
		}
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public LatLng getPosition() {
		return position;
	}

	public void setPosition(LatLng position) {
		this.position = position;
	}
	
	public boolean isValid() {
		return this.getPosition().latitude != 0.0d && this.getPosition().longitude != 0.0d;
	}
}