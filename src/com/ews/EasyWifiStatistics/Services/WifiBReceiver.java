package com.ews.EasyWifiStatistics.Services;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class WifiBReceiver extends BroadcastReceiver {

	WifiManager manager;

	public WifiBReceiver(WifiManager manager) {
	  super();
	  this.manager = manager;
	}
	
	@Override
	public void onReceive(Context c, Intent intent) {
		List<ScanResult> results = manager.getScanResults();
		for (ScanResult result : results) {
				System.out.println("BSSID: " + result.BSSID + " freq: " + result.frequency );      
				//Todo: print statistics to ScanResultsPage via Handler...
		}
	    
	}

}
