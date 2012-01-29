package com.ews.EasyWifiStatistics;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.ews.EasyWifiStatistics.Services.LocationFinder;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView; 

public class WifiMapPage extends MapActivity {
	
	private MapController mapController;
	private MapView mapView;
	
	 @Override
    public void onCreate(Bundle savedInstanceState) {
		//start listening for current location
        LocationFinder lf = new LocationFinder((LocationManager) this.getSystemService(Context.LOCATION_SERVICE));
        lf.startListening();
		 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifimappage);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        mapController.setZoom(16);
        
        //get current location if possible       
        Location location = lf.getLocation();
        lf.stopListening();
        GeoPoint point;
        //if location wasn't retrieved successfully center the screen to Athens, Greece
        if(!(location.getProvider().equalsIgnoreCase("gps") || location.getProvider().equalsIgnoreCase("network"))) {
        	point = new GeoPoint((int) (37.98 * 1E6), (int) (23.73 * 1E6));
        } else {
        	point = new GeoPoint((int) (location.getLatitude()* 1E6), (int) (location.getLongitude()* 1E6));
        }
        mapController.setCenter(point);
        
    }
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}

}
