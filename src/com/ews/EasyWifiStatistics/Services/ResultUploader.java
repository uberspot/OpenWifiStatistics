package com.ews.EasyWifiStatistics.Services;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class ResultUploader {
	
    public static class HostNameVerifierAllowAll implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
	}
	
	private URL url;
	/* Maybe use NameValuePair instead of String String */
	private ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
	
	public void setURL(String url) throws MalformedURLException {
		this.url = new URL(url);
	}
	
	public void setProperties(HashMap<String,String> properties) {
		this.data.add(properties);
	}
	
	public void clearProperties() {
		data.clear();
	}
	
	public void send() throws IOException {                            
		HttpURLConnection connection;        
	        String content;
	        OutputStreamWriter out;               
	        
	        for (Iterator<HashMap<String, String>> it = data.iterator(); it.hasNext();) {
	            connection = (HttpURLConnection) url.openConnection();
	            connection.setDoOutput(true);
	            connection.setRequestMethod("POST");
	            content = this.getContents(it.next());
	            
	            connection.setFixedLengthStreamingMode(content.getBytes().length);
	
	
	            out = new OutputStreamWriter(connection.getOutputStream());
	            out.write(content);
	            out.flush();
	            out.close();
	            connection.disconnect();
	        }
	}
	
	public void sendHTTPS(HostnameVerifier verifier) throws IOException {                            
		HttpsURLConnection connection;        
	        String content;
	        OutputStreamWriter out;               
	        
	        for (Iterator<HashMap<String, String>> it = data.iterator(); it.hasNext();) {
	            connection = (HttpsURLConnection) url.openConnection();
	            connection.setHostnameVerifier(verifier);
	            connection.setDoOutput(true);
	            connection.setRequestMethod("POST");
	            content = this.getContents(it.next());
	            
	            connection.setFixedLengthStreamingMode(content.getBytes().length);
	
	
	            out = new OutputStreamWriter(connection.getOutputStream());
	            out.write(content);
	            out.flush();
	            out.close();
	            connection.disconnect();
	        }
	}
	
	private String getContents(HashMap<String,String> data) throws IOException {
		String content="";
		String key;
		Iterator<String> it = data.keySet().iterator();
		while(it.hasNext()) {
			if(!content.equals(""))
				content += "&";
			key = it.next();
			content += URLEncoder.encode(key, "UTF-8")+"="+URLEncoder.encode(data.get(key), "UTF-8");
		}
		return content;
	}
}
