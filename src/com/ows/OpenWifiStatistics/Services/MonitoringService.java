package com.ows.OpenWifiStatistics.Services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import com.ows.OpenWifiStatistics.Globals;

import Utils.StorageUtils;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.preference.PreferenceManager;
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
        			
        			for(ScanResult result : results) {
        				if( !scanResults.containsKey(result.BSSID) ) {
        					scanResults.put(result.BSSID, new EScanResult(result, latitude, longitude, lastProvider));
        				} else if(scanResults.get(result.BSSID).level <= result.level)
    						scanResults.put(result.BSSID, new EScanResult(result, latitude, longitude, lastProvider));
        			}
        			
        			storageUtils.saveObjectToInnerStorage(scanResults, "scanresults");
        			
        			if(uiHandler!=null) {
	        			Message message = new Message();
	        			message.what = 1;
	        			message.obj = results;
	        			uiHandler.sendMessage(message);
        			}
        			break;
        		case 1:
        			locationFinder.startListening();
        			break;
        		case 2:
        			if(locationFinder.startedListening)
        				locationFinder.stopListening();
        			break;
        	}
            super.handleMessage(msg);
        }
    };
	
	/* Cached scanResults */
	private HashMap<String, EScanResult> scanResults;
	
	public HashMap<String, EScanResult> getScanResults() { return scanResults; }
	
	private ResultUploader formUploader;
	
	private boolean autoUpload;
	
	private StorageUtils storageUtils;
	
	/** Provides access to android's wifi info */
	private WifiManager wifi = null;
	
	private static int scanTimeout, uploadTimeout, locationTimeout, locationTaskTTL; 
	
	/** Listens for results of wifi scans */
	BroadcastReceiver receiver;
	
	/** Schedules autoupload tasks and wifi scan tasks */
	Timer timer;

	/** Location stuff */
	private LocationFinder locationFinder;
	
	private boolean providerDisabled = true;
	private double longitude, latitude;
	
	private String lastProvider;
	
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
	        	lastProvider = location.getProvider();
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate() { 
		super.onCreate();
		
		Globals.service = this;
		
		//Load previous scan results possibly saved in inner storage
		storageUtils = new StorageUtils(getApplicationContext());
		
		loadPreferences();
		
		scanResults = (HashMap<String, EScanResult>) storageUtils.loadObjectFromInnerStorage("scanresults");
		
		if(scanResults==null)
			scanResults = new HashMap<String, EScanResult>();

		timer = new Timer("Service Timer");
		
		//start listening for current location
		locationFinder = new LocationFinder((LocationManager) getSystemService(Context.LOCATION_SERVICE), listener);
		
		latitude = LocationFinder.defaultLatitude;
    	longitude = LocationFinder.defaultLongitude;
    	lastProvider = "network";
    	
		formUploader = new ResultUploader("http://uberspot.ath.cx/wifistats.php");
		
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		// Register Broadcast Receiver
	    if (receiver == null)
	     	receiver = new WifiBReceiver(wifi, handler);
	
	    registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	    
	    timer.schedule( new ScanTask() , 5000, scanTimeout);
	    timer.schedule( new UploadResultsTask() , 10000, uploadTimeout);
	    timer.schedule( new GetLocationTask(handler) , 5000, locationTimeout);
	    
		Toast.makeText(this,"Monitoring started", Toast.LENGTH_SHORT).show();
	}
	
	public void loadPreferences() {
		 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		 try {
			 autoUpload = prefs.getBoolean("autoUpload", true);
			 int usageScenario = Integer.parseInt(prefs.getString("usage_scenario", "1"));
			 if(usageScenario == 1) {
				scanTimeout = 30000; uploadTimeout = 420000;
				locationTimeout = 120000; locationTaskTTL = 40000;
			 } else if(usageScenario == 2) {
				scanTimeout = 5000; uploadTimeout = 720000;
				locationTimeout = 420000; locationTaskTTL = 10000; 
			 } else if(usageScenario == 3) {
				scanTimeout = 60000; uploadTimeout = 720000;
				locationTimeout = 120000; locationTaskTTL = 120000;
			 } else {
				 scanTimeout = Integer.parseInt(prefs.getString("wScanPref", "30")) * 1000;
				 uploadTimeout = Integer.parseInt(prefs.getString("uploadPref", "420")) * 1000;
				 locationTimeout = Integer.parseInt(prefs.getString("lScanPref", "120")) * 1000;
				 locationTaskTTL = Integer.parseInt(prefs.getString("lscan_ttl", "60")) * 1000;
			 }
		 } catch (NumberFormatException e) {
             Toast.makeText(this,"Error in loading settings, using defaults", Toast.LENGTH_SHORT).show();
             scanTimeout = 30000; uploadTimeout = 420000;
             locationTimeout = 120000; locationTaskTTL = 40000;
		 }
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		timer.cancel();
		if(locationFinder.startedListening)
			locationFinder.stopListening();
		Globals.service = null;
		Toast.makeText(this, "Stopped monitoring", Toast.LENGTH_SHORT).show();
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
		Iterator<Entry<String, EScanResult>> iterator = scanResults.entrySet().iterator();
		while(iterator.hasNext()) {
			EScanResult result = iterator.next().getValue();
			if(formUploader.send(result)) {
				iterator.remove();
			}
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
        	if (wifi!=null && wifi.isWifiEnabled() && autoUpload){ //also check if is currently connected to internet
        		uploadResults();
        	}
        }
    }
    
    /** Task that listens for location changes for 60 seconds before powering off. */
    private class GetLocationTask extends TimerTask {
    	private Handler handler;
    	public GetLocationTask(Handler handler) { this.handler = handler; }
        @Override public void run() {
        	handler.sendEmptyMessage(1);
        	try { Thread.sleep(locationTaskTTL); } catch (InterruptedException e) { }
        	handler.sendEmptyMessage(2);
        }
    }
}
