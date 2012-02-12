<?php

require_once('statModel.php');
require_once('configuration.php');

class statsModel {
	
	public function __construct() {
	}
	
	public function getResults($mode=0) {
		$results = array();
		
		mysql_connect($GLOBALS['host'], $GLOBALS['user']);

		mysql_select_db($GLOBALS['database']) or die("Unable to select database");

		$query = "SELECT * FROM `scan_results`";
		
		switch($mode) {
			case 1: $query .= " GROUP BY `ssid`"; break;
			case 2: $query .= " ORDER BY `timestamp`"; break;
			case 3: $query .= " ORDER BY `timestamp` DESC"; break;
			case 4: $query .= " ORDER BY `capabilities`"; break;
			case 5: $query .= " ORDER BY `capabilities` DESC"; break;
			case 6: $query .= " ORDER BY `level`"; break;
			case 7: $query .= " ORDER BY `level` DESC"; break;
		}
		
		$result = mysql_query($query);
		
		while($row = mysql_fetch_array($result)) {
			$results[] = new statModel($row);
		}
		mysql_close();
		
		return $results;
		
	}
	
	public function getStats() {
		
		$stats = array();
		
		mysql_connect($GLOBALS['host'], $GLOBALS['user']);
		mysql_select_db($GLOBALS['database']) or die("Unable to select database");
		$result = mysql_query("SELECT COUNT(*) FROM `scan_results`");
		$row = mysql_fetch_array($result);
		$stats['total'] = $row[0];
		$result = mysql_query("SELECT COUNT(DISTINCT `bssid`) FROM `scan_results`");
		$row = mysql_fetch_array($result);
		$stats['totalwifi'] = $row[0];
		$frequencies = array();
		$result = mysql_query("SELECT `frequency`,COUNT(*) FROM `scan_results` GROUP BY `frequency` ORDER BY COUNT(*) DESC");
		while($row = mysql_fetch_array($result)) {
			$frequencies[$row[0]] = $row[1];
		}
		$stats['frequency'] = $frequencies;		
		$capabilities = array();
		$result = mysql_query("SELECT `capabilities`,COUNT(*) FROM `scan_results` GROUP BY `capabilities` ORDER BY COUNT(*) DESC");
		while($row = mysql_fetch_array($result)) {
			$capabilities[$row[0]] = $row[1];
		}
		$stats['capabilities'] = $capabilities;
		$macprefixes = array();
		$result = mysql_query("SELECT *,COUNT(*) FROM (SELECT SUBSTR(`bssid`,1,8) AS `MACPREFIX` FROM `scan_results` GROUP BY `bssid`) AS subquery GROUP BY `MACPREFIX` ORDER BY COUNT(*) DESC");
		while($row = mysql_fetch_array($result)) {
			$macprefixes[$row[0]] = $row[1];
		}
		$stats['macprefixes'] = $macprefixes;
		mysql_close();
		return $stats;
	}
	
}


?>
