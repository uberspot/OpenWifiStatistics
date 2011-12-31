package com.ews.EasyWifiStatistics.Services;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.ews.EasyWifiStatistics.Globals;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class MonitoringService extends Service {
	
	private Handler uiHandler=null;
	
	public void setUIHandler(Handler uiHandler){ this.uiHandler = uiHandler; }
	
	/** Handles responses from the wifi scan receiver */
	private Handler handler = new Handler(){
        @Override public void handleMessage(Message msg) {
        	switch(msg.what) {
        		case 0: 
        			List<ScanResult> results = (List<ScanResult>) msg.obj;
        			for (ScanResult result : results) {
        				//cache scan results (either internally or in a database[better])
        			}
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
	
	/** Provides access to android's wifi info */
	private WifiManager wifi = null;
	
	private static final int scanTimeout = 5000;
	
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
	     	receiver = new WifiBReceiver(wifi, handler);
	
	    registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	    
		//Todo: schedule autoupload of stats to server task every x minutes
	    timer.schedule( new ScanTask() , scanTimeout, scanTimeout);
		Toast.makeText(this,"Service created...", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();  
		unregisterReceiver(receiver);
		timer.cancel();
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
	
	public WifiInfo getWifiInfo(){
		return (wifi==null) ? null : wifi.getConnectionInfo();
	}
	
	public List<WifiConfiguration> getConfiguredNetworks(){
		return (wifi==null) ? null : wifi.getConfiguredNetworks();
	}
	
	/** Task that just performs a scan. */
    private class ScanTask extends TimerTask {
    	ScanTask() {}

        @Override public void run() {
        	doScan();
        }
    }
}
