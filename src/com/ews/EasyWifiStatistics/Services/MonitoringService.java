package com.ews.EasyWifiStatistics.Services;

import java.util.List;
import java.util.Timer;

import com.ews.EasyWifiStatistics.Globals;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.widget.Toast;

public class MonitoringService extends Service {
	
	
	
	/** Provides access to android's wifi info */
	private WifiManager wifi = null;
	
	/** Listens for results of wifi scans */
	BroadcastReceiver receiver;
	
	/** Schedules autoupload tasks and wifi scan tasks */
	Timer timer;

	@Override
	public IBinder onBind(Intent arg0) { return null; }
	
	@Override
	public void onCreate() { 
		super.onCreate();
		
		Globals.service = this;
		
		timer = new Timer();
		
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		// Register Broadcast Receiver
	    if (receiver == null)
	     	receiver = new WifiBReceiver(wifi);
	
	    registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	    
	    wifi.startScan();
		//Todo: schedule autoupload of stats to server task every x minutes
		//Todo: schedule measurement of wifi stats task every x seconds  with wifi.startScan();
		Toast.makeText(this,"Service created...", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();  
		unregisterReceiver(receiver);
		timer.cancel();
		Toast.makeText(this, "Service destroyed...", Toast.LENGTH_SHORT).show();
	}
	
	public WifiInfo getWifiInfo(){
		return (wifi==null) ? null : wifi.getConnectionInfo();
	}
	
	public List<WifiConfiguration> getConfiguredNetworks(){
		return (wifi==null) ? null : wifi.getConfiguredNetworks();
	}
}
