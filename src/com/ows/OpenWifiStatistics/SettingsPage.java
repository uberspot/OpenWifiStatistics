package com.ows.OpenWifiStatistics;

import com.ows.OpenWifiStatistics.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsPage extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
	@Override
	public void onDestroy() {
    	super.onDestroy();
    	if(Globals.service!=null) {
        	Globals.service.loadPreferences();
        }
		this.finish();
	}
}