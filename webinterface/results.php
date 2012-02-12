<?php

require_once('statsModel.php');
require_once('statModel.php');
require_once('templates/template.php');

    $results = new statsModel();
    
    $order=0;
    if (isset($_GET['order'])) {
        $order = $_GET['order'];
    }
    
	
    /* Presentation */
	    
    echo Template::header("Results");
    
    echo Template::contentStart();
    
    echo "
	<table>";
	switch($order) {
		case 0: echo "<tr><th><a href='?order=2'>Date ↺</a></th>
		<th>BSSID</th><th>SSID</th>
		<th><a href='?order=4'>Capabilities ↺</a></th><th>Frequency</th>
		<th><a href='?order=6'>Power ↺</a></th></tr>";break;
		case 2: echo "<tr><th><a href='?order=3'>Date ↓</a></th>
		<th>BSSID</th><th>SSID</th>
		<th><a href='?order=4'>Capabilities ↺</a></th><th>Frequency</th>
		<th><a href='?order=6'>Power ↺</a></th></tr>";break;
		case 3: echo "<tr><th><a href='?order=2'>Date ↑</a></th>
		<th>BSSID</th><th>SSID</th>
		<th><a href='?order=4'>Capabilities ↺</a></th><th>Frequency</th>
		<th><a href='?order=6'>Power ↺</a></th></tr>";break;
		case 4: echo "<tr><th><a href='?order=2'>Date ↺</a></th>
		<th>BSSID</th><th>SSID</th>
		<th><a href='?order=5'>Capabilities ↓</a></th><th>Frequency</th>
		<th><a href='?order=6'>Power ↺</a></th></tr>";break;
		case 5: echo "<tr><th><a href='?order=2'>Date ↺</a></th>
		<th>BSSID</th><th>SSID</th>
		<th><a href='?order=4'>Capabilities ↑</a></th><th>Frequency</th>
		<th><a href='?order=6'>Power ↺</a></th></tr>";break;
		case 6: echo "<tr><th><a href='?order=2'>Date ↺</a></th>
		<th>BSSID</th><th>SSID</th>
		<th><a href='?order=4'>Capabilities ↺</a></th><th>Frequency</th>
		<th><a href='?order=7'>Power ↓</a></th></tr>";break;
		case 7: echo "<tr><th><a href='?order=2'>Date ↺</a></th>
		<th>BSSID</th><th>SSID</th>
		<th><a href='?order=4'>Capabilities ↺</a></th><th>Frequency</th>
		<th><a href='?order=6'>Power ↑</a></th></tr>";break;
	}
    $flag=true;
    foreach($results->getResults($order) as $result) {
			if($flag) {
				echo "<tr class='bg'>";
				$flag=false;
			} else {
				echo "<tr>";
				$flag=true;
			}
			echo '<td>'.$result->getTime().'</td>';
			echo '<td>'.$result->getBssid().'</td>';
			echo '<td>'.$result->getSsid().'</td>';
			echo '<td>'.$result->getCapabilities().'</td>';
			echo '<td>'.$result->getFrequency().'</td>';
			echo '<td>'.$result->getLevel().'</td>';
			echo "</tr>";
    }
    echo "</table>";
	
    echo Template::contentEnd();
    echo Template::footer();
    
?>
