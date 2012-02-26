<?php
require_once('manuf.php');

function getVendor($mac) {
	global $macdb;
	foreach($macdb as $entry) {
		//entry [ mac ] [ brand ] [ description ] 
		$pos = strpos($mac, $entry['mac']);
		if($pos !== false)
			return $entry;			
	}
	return array('mac' => $mac, 'brand' => 'Unknown', 'description' => '-');
}
?>
