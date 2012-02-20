/**
 * 
 */
package com.ows.OpenWifiStatistics.Services;

import java.io.Serializable;

import android.net.wifi.ScanResult;

/**
 * @author code
 *
 */
public class EScanResult implements Serializable {

	private static final long serialVersionUID = -5651752444215860401L;

	public String BSSID, capabilities, SSID, provider; 
	public int frequency, level;
	public double longitude, latitude;
	
	public EScanResult(ScanResult result, double latitude, double longitude, String provider) {
		BSSID = result.BSSID.trim();
		capabilities = result.capabilities.trim();
		SSID = result.SSID.trim();
		frequency = result.frequency;
		level = result.level;
		this.latitude = latitude;
		this.longitude = longitude;
		this.provider = provider.trim();
	}
	
	 /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (31 * (31 + ((BSSID == null) ? 0 : BSSID.hashCode()))) + (43 * (((SSID == null)?0:SSID.hashCode())));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if (o == null || !o.getClass().equals(getClass()))
            return false;
        EScanResult result = (EScanResult) o;
        return result.BSSID.equalsIgnoreCase(BSSID);
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
        return  "BSSID: "  + BSSID +
        		" SSID: " + SSID + 
        		" capabilities: " + capabilities +
        		" frequency: " + frequency + 
        		" level " + level + 
        		" latitude: " + latitude + 
        		" longitude: " + longitude + 
        		" provider: " + provider;
    }
}
