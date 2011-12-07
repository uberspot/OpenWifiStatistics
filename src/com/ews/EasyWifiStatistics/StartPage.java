package com.ews.EasyWifiStatistics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartPage extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startpage);
    }
    
    public void goToMeasureConnection(View v) {
    	Intent i = new Intent(this, MeasureConnectionPage.class);
        startActivity(i);
      }

}