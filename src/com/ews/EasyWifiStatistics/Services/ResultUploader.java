package com.ews.EasyWifiStatistics.Services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.net.wifi.ScanResult;

public class ResultUploader {
	
	private HttpClient httpclient;
	private HttpPost httppost;
	
	/** Initial Constructor
	 * @param url the url of the http form that will receive the data
	 */
	public ResultUploader(String url){
		httpclient = new DefaultHttpClient();
		httppost = new HttpPost(url);
	}

	public ArrayList<Boolean> uploadResults(List<ScanResult> results) {
		ArrayList<Boolean> validUploads = new ArrayList<Boolean>();
		for(ScanResult result : results){
			validUploads.add( uploadResult(result) );
		}
		return validUploads;
	}
	
	public boolean uploadResult(ScanResult result) {
		// Add your data
		List<NameValuePair> nameValuePairs = resultToNVPairs(result);
		try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
				// Execute HTTP Post Request
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(httppost, responseHandler);
                
                if (responseBody.toString().equals("Whatever the succesfull answer is"))
                    return true;
                return false;
		} catch (ClientProtocolException e) {
            	System.out.println(e.toString());
	    } catch (IOException e) {
	    	System.out.println(e.toString());
	    }
		return false;
	}

	/**
	 * @param result
	 * @return 
	 */
	public static List<NameValuePair> resultToNVPairs(ScanResult result) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("bssid", result.BSSID));
        nameValuePairs.add(new BasicNameValuePair("capabilities", result.capabilities));
        nameValuePairs.add(new BasicNameValuePair("ssid", result.SSID));
        nameValuePairs.add(new BasicNameValuePair("frequency", Integer.toString(result.frequency) ));
        nameValuePairs.add(new BasicNameValuePair("level", Integer.toString(result.level) ));
        return nameValuePairs;
	}
}
