package com.ews.EasyWifiStatistics.Services;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationFinder  {
	
	/*
	 * For now it listens all the time for a location change.
	 * We could add a timer task in monitoringService that would update
	 * our location every x seconds as to consume less power.
	 */
	public static final double defaultLatitude = 37.98, defaultLongitude = 23.73;
	
	public boolean startedListening;
	
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Criteria criteria = new Criteria();
	private LocationListener innerLocationManager = new LocationListener(){

		public void onLocationChanged(Location location) {
			/*Location loc = null;
			//Getting the best provider doesn't work for some reason. Maybe the locationManager 
			//doesn't get the correct last known location. <- for now location retrieving works 
			//quite fine but it should be fixed
			//String bestProvider = locationManager.getBestProvider(criteria, true); 
			if (bestProvider==null ||  (loc = locationManager.getLastKnownLocation(bestProvider)) == null) {
				locationListener.onLocationChanged(new Location("No Provider Available"));
			} else {
				locationListener.onLocationChanged(location);
			}
			 */
			locationListener.onLocationChanged(location);
		}
		public void onProviderDisabled(String provider) {
			locationListener.onProviderDisabled(provider);	
		}
		public void onProviderEnabled(String provider) {
			locationListener.onProviderEnabled(provider);	
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {
			locationListener.onStatusChanged(provider, status, extras);
		}
		};
	
	public LocationFinder(LocationManager lmngr, LocationListener listener) {
		locationManager = lmngr;//(LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
		locationListener = listener;
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		criteria.setSpeedRequired(false);
		startedListening = false;
	}

	public void startListening() {
		startListening(0,0,0,0);
	}
	
	public void startListening(int netmintimeinterval, int netmindist, int gpsmintimeinterval, int gpsmindist) {
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, gpsmintimeinterval, gpsmindist, innerLocationManager);
		if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ) 
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, netmintimeinterval, netmindist, innerLocationManager);
		startedListening = true;
	}
	
	public void stopListening() {
		locationManager.removeUpdates(innerLocationManager);
		startedListening = false;
	}	
}
