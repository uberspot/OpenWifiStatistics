<?php

/* TODO: add proper html5 tags, organize results to tables, possibly create graphs from results etc */
$host = "localhost";
$user = "le user";
$password = "le pass";
$database = "le dbname";

    mysql_connect($host, $user, $password);

    mysql_select_db($database) or die("Unable to select database");

    $result = mysql_query("SELECT * FROM `scan_results`");
    
    while($row = mysql_fetch_array($result)) {
  	echo $row['bssid'] . " " . $row['ssid'] . " " . $row['capabilities'] . " " . $row['level'] 
        . " " . $row['frequency'] . " " . $row['provider'] . " " . $row['latitude'] . " " . $row['longitude'];
  	echo "<br />";
    }
    mysql_close();
?>
