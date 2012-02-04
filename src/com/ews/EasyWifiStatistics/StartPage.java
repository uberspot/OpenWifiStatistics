package com.ews.EasyWifiStatistics;

import com.ews.EasyWifiStatistics.Services.MonitoringService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartPage extends Activity {
	
	private boolean monitoringStarted;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startpage);
        this.monitoringStarted = false;
    }
    
    @Override
	public void onDestroy() {
    	if(monitoringStarted){ //temporary
    	//	stopService(new Intent(StartPage.this, MonitoringService.class));
    	}
    	super.onDestroy();
		this.finish();
	}
    
    public void goToMeasureConnection(View v) {
    	Intent i = new Intent(this, MeasureConnectionPage.class);
        startActivity(i);
    }
    
    public void goToStatistics(View v) {
    	Intent i = new Intent(this, StatisticsPage.class);
        startActivity(i);
    }
    
    public void goToSettings(View v) {
    	Intent i = new Intent(this, SettingsPage.class);
        startActivity(i); 
    }
    
    public void goToMap(View v) {
    	Intent i = new Intent(this, WifiMapPage.class);
        startActivity(i);
    }
    
    public void toggleMonitoring(View v) {
    	Button button = (Button) findViewById(R.id.toggleMonitoring);
    	if(monitoringStarted){
    		stopService(new Intent(StartPage.this, MonitoringService.class));
    		button.setText(R.string.start_monitoring);
    	}else {
            startService(new Intent(StartPage.this, MonitoringService.class));
            button.setText(R.string.stop_monitoring);
            startActivity(new Intent(this, ScanResultsPage.class));
    	}
    	monitoringStarted = !monitoringStarted;
        
    }

}