package com.ows.OpenWifiStatistics;

import com.ows.OpenWifiStatistics.R;
import com.ows.OpenWifiStatistics.Services.MonitoringService;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class SettingsPage extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
	@Override
	public void onDestroy() {
    	super.onDestroy();
    	if(MonitoringService.service!=null) {
        	MonitoringService.service.loadPreferences();
        }
    	Toast.makeText(this,"Restart monitoring for the changes to take effect", Toast.LENGTH_SHORT).show();
		this.finish();
	}
}