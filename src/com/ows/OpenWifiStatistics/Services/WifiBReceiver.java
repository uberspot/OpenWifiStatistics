package com.ows.OpenWifiStatistics.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

/** Class that listens for broadcast messages from the Wifi Service and forwards 
 * those messages to a handler. 
 */
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
		Message message = new Message();
		message.what = 0;
		message.obj = manager.getScanResults();
		handler.sendMessage(message);
	}
}
