package com.ows.OpenWifiStatistics.Services;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

public class WifiBReceiver extends BroadcastReceiver {

	private WifiManager manager;
	private Handler handler = null;
	
	public WifiBReceiver(WifiManager manager, Handler handler) {
	  super();
	  this.manager = manager;
	  this.handler = handler;
	}
	
	@Override
	public void onReceive(Context c, Intent intent) {
		List<ScanResult> results = manager.getScanResults();
		Message message = new Message();
		message.what = 0;
		message.obj = results;
		handler.sendMessage(message);
	}
}
