package com.ows.OpenWifiStatistics;

import com.ows.OpenWifiStatistics.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ScanResultsPage extends Activity {
	
	/** Handles scan results from the wifi service */
	private Handler handler = new Handler(){
        @Override public void handleMessage(Message msg) {
        	switch(msg.what) {
        		case 1:
        			resultsView.setText("Scanned " + Globals.service.scanCounter + " times\n" + 
        							    "Found " + Globals.service.APCounter + " total access points\n" + 
        								Globals.service.getScanResults().size() + " unique access points\n" + 
        								"Current gps position: \n" + "Latitude: " + Globals.service.getLatitude() + 
        								" Longitude: " + Globals.service.getLongitude() + "\n");
        			
        			resultsView.append("" + "\n\n"); //todo: display results in a prettier way 
        			
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