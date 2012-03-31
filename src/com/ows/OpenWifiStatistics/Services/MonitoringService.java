package com.ows.OpenWifiStatistics.Services;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


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
import androidStorageUtils.StorageUtils;

public class MonitoringService extends Service {
	
	/** Holds the current number of wifi scans */
	public int scanCounter;
	
	/** Holds the current recorded number of (non-unique) Access Points scanned */
	public int APCounter;
	
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
        			
        			scanCounter++;
        			
        			for(ScanResult result : results) {
        				APCounter++;
        				if( !scanResults.containsKey(result.BSSID) ) {
        					scanResults.put(result.BSSID, new EScanResult(result, latitude, longitude, lastProvider));
        				} else if(scanResults.get(result.BSSID).level <= result.level)
    						scanResults.put(result.BSSID, new EScanResult(result, latitude, longitude, lastProvider));
        			}
        			if(uiHandler!=null)
        				uiHandler.sendEmptyMessage(1);
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
	
	/* Cached scan results */
	private ConcurrentHashMap<String, EScanResult> scanResults;
	
	public ConcurrentHashMap<String, EScanResult> getScanResults() { return scanResults; }
	
	private boolean autoUpload;
	
	private static String serverURL;
	public static final String defaultServerUrl = "http://195.251.232.92/wifi/";
	
	/** true if the service is currently uploading cached results to a remote
	 * server, false otherwise */
	public static boolean uploading = false;
	
	private StorageUtils storageUtils;
	
	/** Provides access to android's wifi info */
	private WifiManager wifi = null;
	
	private static int scanTimeout, uploadTimeout, locationTimeout, locationTaskTTL; 
	
	/** Listens for results of wifi scans */
	BroadcastReceiver receiver;
	
	/** Schedules autoupload tasks and wifi scan tasks */
	Timer scanTimer, uploadTimer, locationTimer, saveTimer;

	/* Location stuff */
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
		}

		public void onProviderDisabled(String provider) {
			providerDisabled = true;
		}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) { }
		};

	public static MonitoringService service = null;
	
	@Override
	public IBinder onBind(Intent arg0) { return null; }
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate() { 
		super.onCreate();
		
		MonitoringService.service = this;
		
		//Load previous scan results possibly saved in inner storage
		storageUtils = new StorageUtils(getApplicationContext());
		
		loadPreferences();
		
		scanCounter = APCounter = 0;
		
		scanResults = (ConcurrentHashMap<String, EScanResult>) storageUtils.loadObjectFromInternalStorage("scanresults");
		
		if(scanResults==null)
			scanResults = new ConcurrentHashMap<String, EScanResult>(1000);

		//start listening for current location
		locationFinder = new LocationFinder((LocationManager) getSystemService(Context.LOCATION_SERVICE), listener);
		
		latitude = LocationFinder.defaultLatitude;
    	longitude = LocationFinder.defaultLongitude;
    	lastProvider = "network";
		
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		// Register Broadcast Receiver
	    if (receiver == null)
	     	receiver = new WifiBReceiver(wifi, handler);
	
	    registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	    
	    scanTimer = new Timer("Scan Timer");
	    scanTimer.schedule( new ScanTask() , 5000, scanTimeout);
	    
	    uploadTimer = new Timer("Upload Timer");
	    uploadTimer.schedule( new UploadResultsTask() , 10000, uploadTimeout);
	    
	    locationTimer = new Timer("Location Timer");
	    locationTimer.schedule( new GetLocationTask(handler) , 5000, locationTimeout);
	    
	    saveTimer = new Timer("AutoSave Timer");
	    saveTimer.schedule(new TimerTask() {
			@Override public void run() {
				saveInternallyCachedResults();
			}
	    }, 60000, 60000);
	    
	    if(!wifi.isWifiEnabled()) {
	    	Toast.makeText(this,"Please enable wifi first!", Toast.LENGTH_SHORT).show();
	    } else {
	    	Toast.makeText(this,"Monitoring started", Toast.LENGTH_SHORT).show();
	    }
	}
	
	/** Loads user preferences
	 */
	public void loadPreferences() {
		 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		 try {
			 autoUpload = prefs.getBoolean("autoUpload", true);
			 serverURL = prefs.getString("server_url", defaultServerUrl);
			 if(!serverURL.matches("(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]/$")) { 
				 Toast.makeText(this,"Invalid Server url used, using default...", Toast.LENGTH_SHORT).show();
				 serverURL = defaultServerUrl; 
			 }
			 
			 int usageScenario = Integer.parseInt(prefs.getString("usage_scenario", "1"));
			 if(usageScenario == 1) {
				scanTimeout = 30000; uploadTimeout = 420000;
				locationTimeout = 40000; locationTaskTTL = 120000;
			 } else if(usageScenario == 2) {
				scanTimeout = 5000; uploadTimeout = 720000;
				locationTimeout = 10000; locationTaskTTL = Integer.MAX_VALUE; 
			 } else if(usageScenario == 3) {
				scanTimeout = 60000; uploadTimeout = 720000;
				locationTimeout = 120000; locationTaskTTL = 120000;
			 } else {
				 scanTimeout = Integer.parseInt(prefs.getString("wScanPref", "30")) * 1000;
				 uploadTimeout = Integer.parseInt(prefs.getString("uploadPref", "420")) * 1000;
				 locationTimeout = Integer.parseInt(prefs.getString("lScanPref", "60")) * 1000;
				 locationTaskTTL = Integer.parseInt(prefs.getString("lscan_ttl", "120")) * 1000;
			 }
		 } catch (NumberFormatException e) {
             Toast.makeText(this,"Error in loading settings, using defaults", Toast.LENGTH_SHORT).show();
             scanTimeout = 30000; uploadTimeout = 420000;
             locationTimeout = 40000; locationTaskTTL = 120000;
		 }
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		scanTimer.cancel();
		uploadTimer.cancel();
		locationTimer.cancel();
		saveTimer.cancel();
		saveInternallyCachedResults();
		if(locationFinder.startedListening)
			locationFinder.stopListening();
		MonitoringService.service = null;
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
		uploadTimer.schedule(new ForkedUploadTask(), 200);
	}
	
	public WifiInfo getWifiInfo(){
		return (wifi==null) ? null : wifi.getConnectionInfo();
	}
	
	public List<WifiConfiguration> getConfiguredNetworks(){
		return (wifi==null) ? null : wifi.getConfiguredNetworks();
	}
	
	/** Converts the ConcurrentHashMap to a String formatted as a csv.
	 * The first comma separated line contains the names of the columns
	 * (bssid,capabilities,ssid,provider,frequency,level,longitude,latitude)
	 * and each subsequent line holds the scan results
	 * @param results
	 * @return
	 */
	public static String resultsToCSVString(ConcurrentHashMap<String, EScanResult> results) {
		StringBuffer csv = new StringBuffer();
		csv.append("bssid,capabilities,ssid,provider,frequency,level,longitude,latitude\n");
		Iterator<Entry<String, EScanResult>> iterator = results.entrySet().iterator();
		while(iterator.hasNext()) {
			EScanResult result = iterator.next().getValue();
			csv.append("\"" + result.BSSID + "\",");
			csv.append("\"" + result.capabilities + "\",");
			csv.append("\"" + result.SSID + "\",");
			csv.append("\"" + result.provider + "\",");
			csv.append("\"" + result.frequency + "\",");
			csv.append("\"" + result.level + "\",");
			csv.append("\"" + result.longitude + "\",");
			csv.append("\"" + result.latitude + "\"\n");
		}
		return csv.toString();
	}
	
	/* TASKS */
	
	/** Task that just performs a scan. */
     private class ScanTask extends TimerTask {
        @Override public void run() {
        	doScan();
        }
    }
    
    /** Task that uploads the cached results if the wifi is enabled and the 
     * corresponding setting (autoupload) is set to true. */
    private class UploadResultsTask extends TimerTask {
        @Override public void run() {
        	if (wifi!=null && wifi.isWifiEnabled() && autoUpload) {
        		uploadResults();
        	}
        }
    }
    
    /** Task that listens for location changes for <locationTaskTTL> seconds before stopping. */
    private class GetLocationTask extends TimerTask {
    	private Handler handler;
    	public GetLocationTask(Handler handler) { this.handler = handler; }
        @Override public void run() {
        	handler.sendEmptyMessage(1);
        	try { Thread.sleep(locationTaskTTL); } catch (InterruptedException e) { }
        	handler.sendEmptyMessage(2);
        }
    }
    
    /** Task that uploads the results in a different thread so that the user interface doesn't "freeze"
     * on each upload. */
    private class ForkedUploadTask extends TimerTask {
		@Override public void run() {
			if(!MonitoringService.uploading) {
				MonitoringService.uploading = true;
				ResultUploader formUploader = new ResultUploader(serverURL+"wifistats.php");
				formUploader.sendAll(scanResults);
				saveInternallyCachedResults();
				MonitoringService.uploading = false;
			}
		}		
    }
    
    /** Saves the cached results to the internal storage */
    public void saveInternallyCachedResults() {
		storageUtils.saveObjectToInternalStorage(scanResults, "scanresults");
	}
}
