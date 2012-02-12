<?php

class statModel {
	
	private $timestamp;
	private $bssid;
	private $ssid;
	private $capabilities;
	private $level;
	private $frequency;
	private $provider;
	private $latitude;
	private $longitude;
	
	public function __construct($row) {
		$this->timestamp = $row['timestamp'];
		$this->bssid = $row['bssid'];
		$this->ssid = $row['ssid'];
		$this->capabilities = $row['capabilities'];
		$this->level = $row['level'];
		$this->frequency = $row['frequency'];
		$this->provider = $row['provider'];
		$this->latitude = $row['latitude'];
		$this->longitude = $row['longitude'];
	}
	
	function getTime() {
		return $this->timestamp;
	}
	
	function getBssid() {
		return $this->bssid;
	}
	
	function getSsid() {
		return $this->ssid;
	}
	
	function getCapabilities() {
		return $this->capabilities;
	}
	
	function getLevel() {
		return $this->level;
	}
	
	function getFrequency() {
		return $this->frequency;
	}
	
	function getProvider() {
		return $this->provider;
	}
	
	function getLatitude() {
		return $this->latitude;
	}
	
	function getLongitude() {
		return $this->longitude;
	}	
}
?>
