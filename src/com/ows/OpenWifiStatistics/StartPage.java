package com.ows.OpenWifiStatistics;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.ows.OpenWifiStatistics.R;
import com.ows.OpenWifiStatistics.Services.EScanResult;
import com.ows.OpenWifiStatistics.Services.MonitoringService;
import com.ows.OpenWifiStatistics.Services.ResultUploader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidStorageUtils.StorageUtils;

public class StartPage extends Activity {
	
	private StorageUtils storage;
	private String prefName = "servicestarted";
	private static String serverURL = null;
	
	/* Handles the messages sent from the BuzzService */
    private Handler handler = new Handler(){
        @Override public void handleMessage(Message msg) {
        	switch(msg.what) {
			        case 0:
			        	String text = (String) msg.obj;
			        	if (text!=null)
			        		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
			        	break;
			        case 1:
	        			resultsView.setText("Scanned " + Globals.service.scanCounter + " times\n" + 
	        							    "Found " + Globals.service.APCounter + " total access points\n" + 
	        								Globals.service.getScanResults().size() + " unique access points\n" + 
	        								"Current gps position: \n" + "Latitude: " + Globals.service.getLatitude() + 
	        								" Longitude: " + Globals.service.getLongitude() + "\n");
	        			
	        			resultsView.append("" + "\n\n"); //todo: display results in a prettier way 
	        			break;
			        default:
			            break;
	        }
            super.handleMessage(msg);
        }
    };
	
    private TextView resultsView;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startpage);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        serverURL = prefs.getString("server_url", MonitoringService.defaultServerUrl);
		 if(!serverURL.matches("(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]/$")) { 
			 Toast.makeText(this,"Invalid Server url used, using default...", Toast.LENGTH_SHORT).show();
			 serverURL = MonitoringService.defaultServerUrl; 
		 }
        
        storage = new StorageUtils(getApplicationContext());
        String pref = storage.getPreference(prefName, prefName);
        if(pref.equalsIgnoreCase("")) 
        	storage.savePreference(prefName, prefName, "false");
        else if(pref.equalsIgnoreCase("true") ){
        	Button button = (Button) findViewById(R.id.toggleMonitoring);
        	button.setText(R.string.stop_monitoring);
        }
        
        resultsView = (TextView) findViewById(R.id.results);
    }
    
    @Override
	public void onDestroy() {
    	super.onDestroy();
		this.finish();
	}
    
    public void exportResults(View v) {
    	if(Globals.service!=null) {
        	Globals.service.cacheInternallyResults();
        }
    	(new Timer("SD Saving Timer")).schedule(new TimerTask() {
    		@SuppressWarnings("unchecked")
			@Override public void run() {
				ConcurrentHashMap<String, EScanResult> scanResults = 
						(ConcurrentHashMap<String, EScanResult>) storage.loadObjectFromInternalStorage("scanresults");
				if(StorageUtils.hasExternalStorage(true)) {
					int i = 0;
					while(!storage.saveStringToExternalStorage(MonitoringService.resultsToCSVString(scanResults), 
														"OpenWifiStatistics", ++i + ".csv", false) && i < 999) { }
					notifyAbout("Saved stats is SD as OpenWifiStatistics" + File.separator + i + ".csv");
				} else 
					notifyAbout("Can't find SD card!");
    		}
    	}, 300);
    }
    
    public void uploadResults(View v) {
		Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();
        if(Globals.service!=null) {
        	Globals.service.uploadResults();
        } else {
        	(new Timer("Temp Upload Timer")).schedule(new TimerTask() {
        		@SuppressWarnings("unchecked")
				@Override public void run() {
        			if(!MonitoringService.uploading) {
        				MonitoringService.uploading = true;
        				ConcurrentHashMap<String, EScanResult> scanResults = 
        						(ConcurrentHashMap<String, EScanResult>) storage.loadObjectFromInternalStorage("scanresults");
        				ResultUploader formUploader = new ResultUploader(serverURL+"wifistats.php");
        				formUploader.sendAll(scanResults);
        				storage.saveObjectToInternalStorage(scanResults, "scanresults");
        				MonitoringService.uploading = false;
        			}
        		}
        	}, 500);
        }
    }
    
    public void goToSettings(View v) {
        startActivity(new Intent(this, SettingsPage.class)); 
    }
    
    public void viewResults(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(serverURL+"results.php")));
    }
    
    public void toggleMonitoring(View v) {
    	Button button = (Button) findViewById(R.id.toggleMonitoring);
    	if(storage.getPreference(prefName, prefName).equalsIgnoreCase("true") ){
    		stopService(new Intent(StartPage.this, MonitoringService.class));
    		button.setText(R.string.start_monitoring);
    		storage.savePreference(prefName, prefName, "false");
    		if(resultsView!=null) {
    			resultsView.setText("");
    		}
    	}else {
            startService(new Intent(StartPage.this, MonitoringService.class));
            button.setText(R.string.stop_monitoring);
            storage.savePreference(prefName, prefName, "true");
            
            //Set UI handler for service after it starts
            new Timer().schedule(new TimerTask(){
    			@Override
    			public void run() {
    				if(Globals.service!=null)
    		        	Globals.service.setUIHandler(handler);
    			}}, 4000);
    	}
    	
    	
    }
    
    private void notifyAbout(String message) {
		if(handler!=null){
			Message msg = Message.obtain();
			msg.obj = message;
    		handler.sendMessage(msg);
    	}
	}

    public void clearResults(View v) {
    	new AlertDialog.Builder(this)
    	.setTitle("Confirm")
    	.setMessage("Are you sure you want to clear cached wifi stats?")
    	.setIcon(android.R.drawable.ic_dialog_alert)
    	.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int whichButton) {
    	    	storage.saveObjectToInternalStorage(new ConcurrentHashMap<String, EScanResult>(1000), "scanresults");
    	    }})
    	 .setNegativeButton(android.R.string.no, null).show();
    }
} 