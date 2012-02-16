package com.ews.EasyWifiStatistics.Services;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class ResultUploader {
	private HttpClient httpclient;
	private String url;
	
	public ResultUploader(String url){
		httpclient = new DefaultHttpClient();
		this.url = url;
	}
	
	public void setURL(String url) {
		this.url = url;
	}
	
	/** Sends the given result to the URL via http connection
	 * @param result
	 * @return true if the upload was successful, false if the url was null or an IOException occurred
	 */
	public boolean send(EScanResult result) {
			if(url==null)
				return false;
			try {	            
	            List<NameValuePair> nameValuePairs = resultToNVPairs(result);
	            HttpPost httppost = new HttpPost(url);
	            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            HttpResponse response = httpclient.execute(httppost);
	            if(response.getStatusLine().getStatusCode() != 200)
	            	return false;
	            //consume answer to avoid errors
	            HttpEntity entity = response.getEntity();
	            entity.consumeContent();
			} catch (Exception e) {
				e.printStackTrace(System.out);
				return false;
			}
			return true;
	}
	
	public static List<NameValuePair> resultToNVPairs(EScanResult result) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("bssid", result.BSSID));
		nameValuePairs.add(new BasicNameValuePair("capabilities", result.capabilities));
		nameValuePairs.add(new BasicNameValuePair("ssid", result.SSID));
		nameValuePairs.add(new BasicNameValuePair("frequency", Integer.toString(result.frequency) ));
		nameValuePairs.add(new BasicNameValuePair("level", Integer.toString(result.level) ));
		nameValuePairs.add(new BasicNameValuePair("provider", result.provider ));
		nameValuePairs.add(new BasicNameValuePair("longitude", result.longitude+"" ));
		nameValuePairs.add(new BasicNameValuePair("latitude", result.latitude+"" ));
		nameValuePairs.add(new BasicNameValuePair("submit", "submit" ));
		return nameValuePairs;
	} 
		
	/** 
	 * @param result
	 * @return a UTF-8 String representation of the given result 
	 */
	public static String resultToString(EScanResult result) {
		try {
			return  URLEncoder.encode("BSSID", "UTF-8")+"="+URLEncoder.encode(result.BSSID, "UTF-8")+"&"
					+ URLEncoder.encode("SSID", "UTF-8")+"="+URLEncoder.encode(result.capabilities, "UTF-8")+"&"
					+ URLEncoder.encode("capabilities", "UTF-8")+"="+URLEncoder.encode(result.SSID, "UTF-8")+"&"
					+ URLEncoder.encode("frequency", "UTF-8")+"="+URLEncoder.encode(result.frequency+"", "UTF-8")+"&"
					+ URLEncoder.encode("level", "UTF-8")+"="+URLEncoder.encode( (result.level+""), "UTF-8")+"&"
					+ URLEncoder.encode("provider", "UTF-8")+"="+URLEncoder.encode(result.provider, "UTF-8")+"&"
					+ URLEncoder.encode("latitude", "UTF-8")+"="+URLEncoder.encode(result.latitude+"", "UTF-8")+"&"
					+ URLEncoder.encode("longtitude", "UTF-8")+"="+URLEncoder.encode( (result.longitude+""), "UTF-8")+"&"
					+ URLEncoder.encode("submit", "UTF-8")+"="+URLEncoder.encode("submit", "UTF-8");
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return "";
		}
	}
}
