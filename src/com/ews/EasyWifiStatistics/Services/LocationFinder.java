package com.ews.EasyWifiStatistics.Services;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationFinder  {
	
	/*
	 * 2 possible ways of using it
	 * 
	 * == 1st way ==
	 * Add a listener that will listen all the time for new locations
	 * (every x seconds or something like that).
	 * So when we'll need a location we'll use the most current.
	 * However, this is very power consuming.
	 * 
	 * == 2nd way ==
	 * Start to listen before collect wifi data. Stop after that.
	 * This way we use less battery but we have to be sure to
	 * have an accurate location.
	 */
	
	private class customLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			// Called when a new location is found by the location provider.
			// We can add custom location filtering here.
		}

		public void onProviderDisabled(String provider) {
			
		}

		public void onProviderEnabled(String provider) {
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
	}
	
	private LocationManager locationManager;
	private LocationListener locationListenerNet = new customLocationListener();
	private LocationListener locationListenerGps = new customLocationListener();
	private Criteria criteria = new Criteria();

	public LocationFinder(LocationManager lmngr) {
		locationManager = lmngr;//(LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		criteria.setSpeedRequired(false);
	}

	public void startListening() {
		// not sure if this check is needed! exception not thrown but maybe
		// undue battery consumption is avoided
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNet);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
		}
	}
	
	public void startListening(int netmintimeinterval,int netmindist,int gpsmintimeinterval,int gpsmindist) {
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, netmintimeinterval, netmindist, locationListenerNet);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, gpsmintimeinterval, gpsmindist, locationListenerGps);
		}
	}
	
	public void stopListening() {
		locationManager.removeUpdates(locationListenerNet);
		locationManager.removeUpdates(locationListenerGps);
	}
	
	public Location getLocation() {
		if(!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)))
			return new Location("No Provider Available");
		Location loc;
		if ((loc=locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true))) == null)
			return new Location("No Location Available");
		return loc;
	}
	
}
