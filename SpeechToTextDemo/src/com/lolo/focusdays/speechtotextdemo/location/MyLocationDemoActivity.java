/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lolo.focusdays.speechtotextdemo.location;

import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lolo.focusdays.speechtotextdemo.R;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location. To
 * track changes to the users location on the map, we request updates from the
 * {@link LocationClient}.
 */
public class MyLocationDemoActivity extends FragmentActivity
        implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        OnMyLocationButtonClickListener, 
        OnMarkerClickListener, 
        OnMarkerDragListener, 
        OnInfoWindowClickListener, 
        InfoWindowAdapter {

	public MyLocationDemoActivity() {
		super();
	}
	private GoogleMap mMap;

    private LocationClient mLocationClient;
    private TextView mMessageView;
    private LatLng markerPosition;

    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	private SimpleAddress currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.my_location_demo);
        mMessageView = (TextView) findViewById(R.id.message_text);
        
        if (savedInstanceState != null) {
        	double latitude = savedInstanceState.getDouble("marker.latitude");
        	double longitude = savedInstanceState.getDouble("marker.longitude");
        	if (latitude != 0.0d || longitude != 0.0d) {
	        	markerPosition = new LatLng(latitude, longitude);
	        }
        	if (savedInstanceState.getString("currentLocationAddress") != null) {
	    		this.currentLocation = new SimpleAddress(
	    				markerPosition,
	    				savedInstanceState.getString("currentLocationAddress"),
	    				savedInstanceState.getString("currentLocationZip"), 
	    				savedInstanceState.getString("currentLocationCity"),
	    				savedInstanceState.getString("currentLocationCountry"));
        	}

        }
    }
    
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putDouble("marker.latitude", markerPosition.latitude);
		outState.putDouble("marker.longitude", markerPosition.longitude);
		if (this.currentLocation != null) {
			outState.putString("currentLocationAddress", this.currentLocation.getAddress());
			outState.putString("currentLocationZip", this.currentLocation.getZip());
			outState.putString("currentLocationCity", this.currentLocation.getCity());
			outState.putString("currentLocationCountry", this.currentLocation.getCountry());
		}
	}

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        mLocationClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);
                
                mMap.setOnMarkerClickListener(this);
                mMap.setOnMarkerDragListener(this);
                mMap.setOnInfoWindowClickListener(this);
                mMap.setInfoWindowAdapter(this);
                this.addStoredOrCurrentLocationAsMarkersToMap();
            }
        }
    }

    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener

        }
    }

    /**
     * Button to get current Location. This demonstrates how to get the current Location as required
     * without needing to register a LocationListener.
     */
    public void showMyLocation(View view) {
        if (mLocationClient != null && mLocationClient.isConnected()) {
            String msg = "Location = " + mLocationClient.getLastLocation();
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
        mMessageView.setText("Location = " + location);
    }


    private LatLng zoomInCameraToCurrentLocation() {
    	if (mLocationClient != null) {
	        Location location = mLocationClient.getLastLocation();
	        if (location != null) {
	        	mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
	                    new LatLng(location.getLatitude(), location.getLongitude()), 15));
	            return
	            	new LatLng(location.getLatitude(), location.getLongitude());
	        }
    	}
    	return null;
    }
    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener
        this.addStoredOrCurrentLocationAsMarkersToMap();
    }

    
    private void addCurrentLocationAsMarkersToMap() {
	    this.addLocationAsMarkersToMap(this.zoomInCameraToCurrentLocation());
    }
    private void addStoredOrCurrentLocationAsMarkersToMap() {
    	if (this.markerPosition == null) {
    		if (this.currentLocation == null) {
    			this.currentLocation = new SimpleAddress(this.getIntent());
    			if (this.currentLocation.isValid()) {
	    			this.markerPosition = this.currentLocation.getPosition();
	    			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(this.markerPosition, 15));
    			} else {
        			this.markerPosition = this.zoomInCameraToCurrentLocation();
    			}
    		} else {
    			this.markerPosition = this.zoomInCameraToCurrentLocation();
    		}
		}
		this.addLocationAsMarkersToMap(this.markerPosition);
    }
    private void addLocationAsMarkersToMap(LatLng latLng) {
    	if (latLng == null) return;
    	try {
			AddressResult addressResult = new GetAddressAsyncTask(this).execute(latLng).get();
			if (addressResult.hasResult()) {
		    	Marker marker = mMap.addMarker(new MarkerOptions()
		        .position(latLng)
		        .title(addressResult.getSimpleAddress().getCity())
		        .draggable(true)
		        .snippet(
		        		addressResult.getSimpleAddress().getAddress() +" "+
		        		addressResult.getSimpleAddress().getZip() +" "+
		        		addressResult.getSimpleAddress().getCity() +" ("+
		        		addressResult.getSimpleAddress().getCountry()+")"
		        )
		        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
		    	this.setResultIntent(addressResult.getSimpleAddress());
		    	marker.showInfoWindow();
		    	markerPosition = marker.getPosition();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	private void setResultIntent(SimpleAddress simpleAddress) {
		this.currentLocation = simpleAddress;
	}


	/**
     * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onDisconnected() {
        // Do nothing
    }

    /**
     * Implementation of {@link OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        mMap.clear();
        this.addCurrentLocationAsMarkersToMap();

        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    /* map maper interfaces - drag */ 
   	@Override
	public void onMarkerDrag(Marker marker) {
		
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		if (marker.isInfoWindowShown()) {
			marker.hideInfoWindow();
		}
		addLocationAsMarkersToMap(marker.getPosition());
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		if (marker.isInfoWindowShown()) {
			marker.hideInfoWindow();
		}
		
	}

    /* map maper interfaces - click */

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return false;
	}


    /* map maper interfaces - on window click */
	@Override
	public void onInfoWindowClick(Marker marker) {
		if (marker.isInfoWindowShown()) {
			marker.hideInfoWindow();
		} else {
			marker.showInfoWindow();
		}
		
	}

    /* map maper interfaces - customizatble info windows*/

	@Override
	public View getInfoContents(Marker arg0) {
		
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		/* do not change */
		return null;
	}


	@Override
	public void onBackPressed() {
		if (this.currentLocation != null) {
			this.currentLocation.putExtra("marker.latitude", this.currentLocation.getPosition().latitude);
			this.currentLocation.putExtra("marker.longitude", this.currentLocation.getPosition().longitude);

			this.currentLocation.putExtra("currentLocationAddress", this.currentLocation.getAddress());
			this.currentLocation.putExtra("currentLocationZip", this.currentLocation.getZip());
			this.currentLocation.putExtra("currentLocationCity", this.currentLocation.getCity());
			this.currentLocation.putExtra("currentLocationCountry", this.currentLocation.getCountry());
		}
		setResult(RESULT_OK, this.currentLocation);
        finish();
	}


	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		
	}

}
