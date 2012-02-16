package com.ews.EasyWifiStatistics;

import com.ews.EasyWifiStatistics.Services.MonitoringService;

import Utils.StorageUtils;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartPage extends Activity {
	
	//private boolean monitoringStarted;
	
	private StorageUtils storage;
	private String prefName = "servicestarted";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startpage);
        //this.monitoringStarted = false;
        storage = new StorageUtils(getApplicationContext());
        String pref = storage.getPreference(prefName, prefName);
        if(pref.equalsIgnoreCase("")) 
        	storage.savePreference(prefName, prefName, "false");
        else if(pref.equalsIgnoreCase("true") ){
        	Button button = (Button) findViewById(R.id.toggleMonitoring);
        	button.setText(R.string.stop_monitoring);
        }
    }
    
    @Override
	public void onDestroy() {
    	super.onDestroy();
		this.finish();
	}
    
    public void goToMeasureConnection(View v) {
        startActivity(new Intent(this, MeasureConnectionPage.class));
    }
    
    public void goToStatistics(View v) {
        startActivity(new Intent(this, StatisticsPage.class));
    }
    
    public void goToSettings(View v) {
        startActivity(new Intent(this, SettingsPage.class)); 
    }
    
    public void viewResults(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://uberspot.ath.cx/wifi/results.php")));
    }
    
    public void toggleMonitoring(View v) {
    	Button button = (Button) findViewById(R.id.toggleMonitoring);
    	if(storage.getPreference(prefName, prefName).equalsIgnoreCase("true") ){
    		stopService(new Intent(StartPage.this, MonitoringService.class));
    		button.setText(R.string.start_monitoring);
    		storage.savePreference(prefName, prefName, "false");
    	}else {
            startService(new Intent(StartPage.this, MonitoringService.class));
            button.setText(R.string.stop_monitoring);
            storage.savePreference(prefName, prefName, "true");
            startActivity(new Intent(this, ScanResultsPage.class));
    	}
        
    }

}