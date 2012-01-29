package com.ews.EasyWifiStatistics;

import android.os.Bundle;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView; 

public class WifiMapPage extends MapActivity {
	
	MapView mapView;
	
	 @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifimappage);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
    }
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}

}
