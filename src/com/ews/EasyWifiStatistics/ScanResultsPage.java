/**
 * 
 */
package com.ews.EasyWifiStatistics;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 */
public class ScanResultsPage extends Activity {
	
	
	TextView resultsView;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanresultspage);
        
        resultsView = (TextView) findViewById(R.id.results);
        
		resultsView.append("\n\nWiFi Status: " + Globals.service.getWifiInfo().toString());
		
		// List available networks
		List<WifiConfiguration> configs = Globals.service.getConfiguredNetworks();
		for (WifiConfiguration config : configs) {
			resultsView.append("\n\n" + config.toString());
		}
    }
    
    public void onScanClick(View v) {
    	System.out.println("wifi.startScan()");
    }
    
    @Override
	public void onDestroy() {
    	super.onDestroy();
		this.finish();
	}
}
