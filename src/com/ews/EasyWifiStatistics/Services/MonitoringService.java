package com.ews.EasyWifiStatistics.Services;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.ews.EasyWifiStatistics.Globals;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class MonitoringService extends Service {
	
	/* Handler stuff */
	private Handler uiHandler=null;
	
	public void setUIHandler(Handler uiHandler){ this.uiHandler = uiHandler; }
	
	/** Handles responses from the wifi scan receiver */
	private Handler handler = new Handler(){
        @Override public void handleMessage(Message msg) {
        	switch(msg.what) {
        		case 0: 
        			@SuppressWarnings("unchecked")
					List<ScanResult> results = (List<ScanResult>) msg.obj;
        			
        			scanResults.add(results);
        			
        			/* save scan results (either internally or in a database[better]) to preserve in case the service stops
        			 * or possibly save the results in the onDestroy() function 
	    			*/
        			
        			if(uiHandler!=null) {
	        			Message message = new Message();
	        			message.what = 1;
	        			message.obj = results;
	        			uiHandler.sendMessage(message);
        			}
        			break;
        	}
            super.handleMessage(msg);
        }
    };
	
	/* Cached scanResults */
	private ArrayList<List<ScanResult>> scanResults;
	
	private ResultUploader formUploader;
	
	/** Provides access to android's wifi info */
	private WifiManager wifi = null;
	
	private static final int scanTimeout = 5000; //5 seconds
	private static final int uploadTimeout = 300000; //5 minutes
	
	/** Listens for results of wifi scans */
	BroadcastReceiver receiver;
	
	/** Schedules autoupload tasks and wifi scan tasks */
	Timer timer;

	/** Location stuff */
	private LocationFinder locationFinder;
	
	private boolean providerDisabled = true;
	private double longitude, latitude;
	
	public double getLongitude() { return longitude; }
	public double getLatitude() { return latitude; }
	
	public boolean isProviderDisabled() { return providerDisabled; }
	
	LocationListener listener = new LocationListener(){
		public void onLocationChanged(Location location) {
			if(!(location.getProvider().equalsIgnoreCase("gps") || location.getProvider().equalsIgnoreCase("network"))) {
				latitude = LocationFinder.defaultLatitude;
	        	longitude = LocationFinder.defaultLongitude;
	        } else {
	        	latitude = location.getLatitude();
	        	longitude = location.getLongitude();
	        	providerDisabled = false;
    			if(uiHandler!=null)
    				uiHandler.sendEmptyMessage(2);
	        }
			System.out.println("Location: " + latitude + " " + longitude);
		}

		public void onProviderDisabled(String provider) {
			providerDisabled = true;
		}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) { }
		};
	
	@Override
	public IBinder onBind(Intent arg0) { return null; }
	
	@Override
	public void onCreate() { 
		super.onCreate();
		
		Globals.service = this;
		
		scanResults = new ArrayList<List<ScanResult>>();
		
		timer = new Timer();
		
		//start listening for current location
		locationFinder = new LocationFinder((LocationManager) getSystemService(Context.LOCATION_SERVICE), listener);
		locationFinder.startListening();
		latitude = LocationFinder.defaultLatitude;
    	longitude = LocationFinder.defaultLongitude;
		
		formUploader = new ResultUploader();
		try {
			formUploader.setURL("http://formurl.com/something.php");
		} catch (MalformedURLException e) {
			System.out.println("Malformed URL: " + e);
		}
		
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		// Register Broadcast Receiver
	    if (receiver == null)
	     	receiver = new WifiBReceiver(wifi, handler);
	
	    registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	    
	    timer.schedule( new ScanTask() , scanTimeout, scanTimeout);
	    timer.schedule( new UploadResultsTask() , uploadTimeout, uploadTimeout);
		Toast.makeText(this,"Service created...", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		timer.cancel();
		locationFinder.stopListening();
		Globals.service = null;
		Toast.makeText(this, "Service destroyed...", Toast.LENGTH_SHORT).show();
	}
	
	/** Performs a scan via WifiManagers interface
	 * @return true if the scan can be performed, false otherwise
	 */
	public boolean doScan(){
		if (wifi!=null && wifi.isWifiEnabled()){
			wifi.startScan();
			return true;
		}
		return false;
	}
	
	/** Uploads the currently cached lists of scan results.
	 * After the upload it checks each result to see if it was uploaded successfully.
	 * If it was it removes it from its list.
	 * If a list of results is fully uploaded (meaning all the results are successfully uploaded)
	 * it deletes it from the cached lists.
	 */
	public void uploadResults() {
		List<ScanResult> results;
		ArrayList<Boolean> validUploads;
		for(int i = 0; i < scanResults.size(); i++){
			results = scanResults.get(i);
			validUploads = formUploader.sendAll(results);
    		for(int j = 0; j <validUploads.size(); j++){
    			if( validUploads.get(j) ){
    				validUploads.remove(j);
    				results.remove(j--);
    			}
    		}
    		if(results.isEmpty())
    			scanResults.remove(i--);
    	}
	}
	
	public WifiInfo getWifiInfo(){
		return (wifi==null) ? null : wifi.getConnectionInfo();
	}
	
	public List<WifiConfiguration> getConfiguredNetworks(){
		return (wifi==null) ? null : wifi.getConfiguredNetworks();
	}
	
	/* TASKS */
	
	/** Task that just performs a scan. */
    private class ScanTask extends TimerTask {
        @Override public void run() {
        	doScan();
        }
    }
    
    /** Task that just performs a scan. */
    private class UploadResultsTask extends TimerTask {
        @Override public void run() {
        	if (wifi!=null && wifi.isWifiEnabled()){ //also check if is currently connected to internet
        		uploadResults();
        	}
        }
    }
}
