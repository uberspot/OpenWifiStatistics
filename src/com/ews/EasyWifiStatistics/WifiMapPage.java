package com.ews.EasyWifiStatistics;

import android.os.Bundle;

import com.google.android.maps.MapActivity;

public class WifiMapPage extends MapActivity {
	
	 @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifimappage);
    }
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}

}
