/**
 * 
 */
package com.ews.EasyWifiStatistics;

import java.util.List;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 */
public class ScanResultsPage extends Activity {
	
	/** Handles scan results from the wifi service */
	private Handler handler = new Handler(){
        @Override public void handleMessage(Message msg) {
        	switch(msg.what) {
        		case 1:
        			@SuppressWarnings("unchecked")
					List<ScanResult> results = (List<ScanResult>) msg.obj;
        			resultsView.setText("");
        			for (ScanResult result : results) {
        				resultsView.append(result.toString() + "\n"); //todo: display results in a prettier way 
        			}
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
        setContentView(R.layout.scanresultspage);
        
        resultsView = (TextView) findViewById(R.id.results);
        
		resultsView.append("\n\nWiFi Status: " + Globals.service.getWifiInfo().toString());
		
		// List available networks
		List<WifiConfiguration> configs = Globals.service.getConfiguredNetworks();
		for (WifiConfiguration config : configs) {
			resultsView.append("\n\n" + config.toString());
		}
    }
    
    @Override
    public void onResume(){ 
        super.onResume();
        Globals.service.setUIHandler(handler);
    }
    
    /** Called when the Scan Button is clicked
     * @param v
     */
    public void onScanClick(View v) {
    	if(!Globals.service.doScan()){
    		Toast.makeText(this, "Please enable wifi first!", Toast.LENGTH_SHORT).show();
    	}
    }
    
    @Override
	public void onDestroy() {
    	super.onDestroy();
		this.finish();
	}
}