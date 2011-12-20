package com.ews.EasyWifiStatistics;

import java.util.Timer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class MonitoringService extends Service {

	Timer timer;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() { 
		super.onCreate();
		timer = new Timer();
		//schedule autoupload of stats to server task every x minutes
		//schedule measurement of wifi stats task every x seconds
		Toast.makeText(this,"Service created...", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();  
		timer.cancel();
		Toast.makeText(this, "Service destroyed...", Toast.LENGTH_SHORT).show();
	}
}
