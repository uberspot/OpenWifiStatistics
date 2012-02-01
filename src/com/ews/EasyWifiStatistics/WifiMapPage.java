package com.ews.EasyWifiStatistics;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.ews.EasyWifiStatistics.Services.LocationFinder;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView; 

public class WifiMapPage extends MapActivity {
	
	private MapController mapController;
	private MapView mapView;
	
	/** Autoupdate position on map every time it changes */
	private Handler handler = new Handler(){
        @Override public void handleMessage(Message msg) {
        	switch(msg.what) {
        		case 2:
					if(Globals.service!=null) {
						mapController.setCenter(new GeoPoint((int) (Globals.service.getLatitude()* 1E6), (int) (Globals.service.getLongitude()* 1E6)));
						Toast.makeText(getApplicationContext(), "Location: " + Globals.service.getLatitude() + " " + Globals.service.getLongitude(), Toast.LENGTH_SHORT).show(); 
					}
        			break;
        	}
            super.handleMessage(msg);
        }
    };
	
	 @Override
    public void onCreate(Bundle savedInstanceState) {
		 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifimappage);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.displayZoomControls(true);
        mapController = mapView.getController();
        mapController.setZoom(16);

        if(Globals.service==null) {
        	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Please start the monitoring service first to get your accurate position.");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                return;
            } });
            alertDialog.show();
            mapController.setCenter(new GeoPoint((int) (LocationFinder.defaultLatitude* 1E6), (int) (LocationFinder.defaultLongitude* 1E6)));
        } else {
        	Globals.service.setUIHandler(handler);
        	
	        if(!Globals.service.isProviderDisabled()) {
	        	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	            alertDialog.setTitle("Error");
	            alertDialog.setMessage("No location provider found! Please enable gps and/or wifi location.");
	            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	              public void onClick(DialogInterface dialog, int which) {
	                return;
	            } });
	            alertDialog.show();
	        }
	        mapController.setCenter(new GeoPoint((int) (Globals.service.getLatitude()* 1E6), (int) (Globals.service.getLongitude()* 1E6)));
	    }
        
    }
	
	@Override
	public void onDestroy() {
    	super.onDestroy();
		this.finish();
	}
	 
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}

}
