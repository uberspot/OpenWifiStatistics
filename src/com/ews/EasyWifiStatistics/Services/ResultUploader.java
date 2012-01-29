package com.ews.EasyWifiStatistics.Services;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class ResultUploader {
	
    public static class HostNameVerifierAllowAll implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
	}
	
	private URL url = null;
	
	public void setURL(String url) throws MalformedURLException {
		this.url = new URL(url);
	}
	
	/** Sends the given result to the URL via http connection
	 * @param verifier
	 * @param result
	 * @return true if the upload was successful, false if the url was null or an IOException occurred
	 */
	public boolean send(EScanResult result) {
			if(url==null)
				return false;
        	HttpURLConnection connection;
			try {
				connection = (HttpURLConnection) url.openConnection();
	            connection.setDoOutput(true);
	            connection.setRequestMethod("POST");
	            
	            String content = ResultUploader.resultToString(result); 
	            connection.setFixedLengthStreamingMode(content.getBytes().length);
	
	            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
	            out.write(content);
	            out.flush();
	            out.close();
	            connection.disconnect();
			} catch (IOException e) {
				return false;
			}
			return true;
	}
	
	/** Sends the given result to the URL via https connection
	 * @param verifier
	 * @param result
	 * @return true if the upload was successful, false if the url was null or an IOException occurred
	 */
	public boolean sendHTTPS(HostnameVerifier verifier, EScanResult result) {
			if(url==null)
				return false;
        	HttpsURLConnection connection;
			try {
				connection = (HttpsURLConnection) url.openConnection();
	            connection.setHostnameVerifier(verifier);
	            connection.setDoOutput(true);
	            connection.setRequestMethod("POST");
	            
	            String content = ResultUploader.resultToString(result);
	            connection.setFixedLengthStreamingMode(content.getBytes().length);
	
	            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
	            out.write(content);
	            out.flush();
	            out.close();
	            connection.disconnect();
			} catch (IOException e) {
				return false;
			}
            return true;
	}
	
	/** 
	 * @param result
	 * @return a UTF-8 String representation of the given result 
	 */
	public static String resultToString(EScanResult result) {
		try {
			return  URLEncoder.encode("bssid", "UTF-8")+"="+URLEncoder.encode(result.BSSID, "UTF-8")
					+ URLEncoder.encode("capabilities", "UTF-8")+"="+URLEncoder.encode(result.capabilities, "UTF-8")
					+ URLEncoder.encode("ssid", "UTF-8")+"="+URLEncoder.encode(result.SSID, "UTF-8")
					+ URLEncoder.encode("frequency", "UTF-8")+"="+URLEncoder.encode(result.frequency+"", "UTF-8")
					+ URLEncoder.encode("level", "UTF-8")+"="+URLEncoder.encode( (result.level+""), "UTF-8")
					+ URLEncoder.encode("latitude", "UTF-8")+"="+URLEncoder.encode(result.latitude+"", "UTF-8")
					+ URLEncoder.encode("longitude", "UTF-8")+"="+URLEncoder.encode( (result.longitude+""), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/** Sends all the given results via http 
	 * @param results
	 * @return an array of boolean indicating the status of each results' upload. 
	 * True if it was uploaded successfully, false otherwise.
	 */
	public ArrayList<Boolean> sendAll(List<EScanResult> results) {
		ArrayList<Boolean> validUploads = new ArrayList<Boolean>();
		for(EScanResult result : results) {
			validUploads.add( send(result) );
		}
		return validUploads;
	}
	
	/** Sends all the given results via https
	 * @param results
	 * @return an array of boolean indicating the status of each results' upload. 
	 * True if it was uploaded successfully, false otherwise.
	 */
	public ArrayList<Boolean> sendAllHTTPS(List<EScanResult> results) {
		ArrayList<Boolean> validUploads = new ArrayList<Boolean>();
		for(EScanResult result : results) {
			validUploads.add( sendHTTPS(new HostNameVerifierAllowAll(), result) );
		}
		return validUploads;
	}
}
