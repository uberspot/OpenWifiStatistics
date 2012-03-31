package com.ows.OpenWifiStatistics.Services;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/** Class used for listening for location changes via a LocationManager. 
 * The location providers used are GPS_PROVIDER and NETWORK_PROVIDER.
 */
public class LocationFinder  {
	
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
	
	/** Default Constructor
	 * @param lmngr the LocationManager to listen to
	 * @param listener the LocationListener that will receive location events.
	 */
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
	
	/** Starts listening for location changes from GPS_PROVIDER and NETWORK_PROVIDER if they are enabled.
	 * @param netmintimeinterval minimum time interval between updates from network provider, 
	 * actual time between location updates may be greater or lesser than this value.
	 * @param netmindist the minimum distance interval for notifications from network provider, in meters
	 * @param gpsmintimeinterval minimum time interval between updates from gps provider, 
	 * actual time between location updates may be greater or lesser than this value.
	 * @param gpsmindist the minimum distance interval for notifications from gps provider, in meters
	 */
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
