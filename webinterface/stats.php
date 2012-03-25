<?php

$cache = 'cache/stats.html';
if(file_exists($cache)) {
	$fromcreation = date('U')-date ('U',filemtime($cache));
	//			hours	min		sec
	$time =    	1   *	30	*	60;//15min
	if($fromcreation < $time) {
		echo file_get_contents($cache);
		exit();
	}
}

require('statsModel.php');
require('templates/template.php');
require('vendors.php');

	$script = '<script type="text/javascript" src="js/awesomechart.js"></script>';
	$out = Template::header("Statistics",$script);
	$out .= Template::contentStart();

	$results = new statsModel();
	$stats = $results->getStats();
	$out .= "<h1>General Info</h1><br/>";
	$out .= "<strong>Total scans: </strong>".$stats['total'];
	$out .= "<strong> Distinct wifis: </strong>".$stats['totalwifi']."<hr/>";
	$out .= '<h1>Frequency Statistics</h1><br/>
		<div class="charts_container">
            <canvas id="frequencyCanvas" width="600" height="400">
                Your web-browser does not support the HTML 5 canvas element.
            </canvas>
		</div>';

	$datas = "";
	$labels = "";
	$i=0;
    foreach($stats['frequency'] as $frequency=>$data) {
		if($i > 9) break; $i++;
		if ($datas == "") {
			$datas .= $data;
			$labels .= "$frequency";
		}
		else {			
			$datas .= ','.$data;
			$labels .= ','."$frequency";
		}
	}
	$out .= '
	<script type="text/javascript">
	var freq = new AwesomeChart(\'frequencyCanvas\');
            freq.title = "10 Most Popular Frequency Settings";
            freq.data = ['.$datas.'];
            freq.labels = ['.$labels.'];
            freq.draw();
	</script>';
	
	$out .= '<hr><h1>Access Point Vendors</h1><br/>
		<div class="charts_container">
            <canvas id="vendorsCanvas" width="600" height="600">
                Your web-browser does not support the HTML 5 canvas element.
            </canvas>
		</div>';
	
	$datas = "";
	$labels = "";
	$vendors = array();
	$vendorinfo = array();
	$i=0;
    foreach($stats['macprefixes'] as $mac=>$count) {
		if($i > 9) break;
		$vendor = getVendor($mac);
		if(empty($vendors[$vendor['brand']])) {
			$i++;
			$vendors[$vendor['brand']] = $count;
		}
		else
			$vendors[$vendor['brand']] += $count;
		$vendorinfo[$vendor['brand']] = $vendor['description'];
	}
	
	array_multisort($vendors, SORT_DESC);
	
	foreach($vendors as $vendor=>$count) {
		if ($datas == "") {
			$datas .= $count;
			$labels .= "'$vendor'";
		}
		else {			
			$datas .= ','.$count;
			$labels .= ','."'$vendor'";
		}
	}
	
	$out .= '<script type="text/javascript">
	var vendors = new AwesomeChart(\'vendorsCanvas\');
	vendors.chartType = "pie";
        vendors.title = "10 Most Popular AP Vendors";
        vendors.colors = [\'#006CFF\', \'#FF6600\', \'#34A038\', \'#945D59\', \'#93BBF4\', \'#F493B8\' ,\'#e3e123\',\'#f123cc\',\'#ccc\'];
        vendors.data = ['.$datas.'];
        vendors.labels = ['.$labels.'];
        vendors.draw();
	</script>';
	
	$out .= '<table><tr class=\'vendors\'><th>Vendor</th><th>Description</th><th>Count</th></tr>';
	$i=0;
	foreach($vendors as $vendor=>$count) {
		$out .= "<tr id='vendor$i' class='vendors'><td>$vendor</td><td>$vendorinfo[$vendor]</td><td>$count</td></tr>";
		$i++;
	}
	$out .= '</table><hr/>';
	
	$out .= '<h1>Security</h1><br/>
		<div class="charts_container">
            <canvas id="secCanvas" width="800" height="800">
                Your web-browser does not support the HTML 5 canvas element.
            </canvas>
		</div>';
		
	$datas = "";
	$labels = "";
	$i=0;
    foreach($stats['capabilities'] as $capabilities=>$data) {
		if($i > 7) break; $i++;
		if ($datas == "") {
			$datas .= $data;
			$labels .= "'$capabilities'";
		}
		else {			
			$datas .= ','.$data;
			$labels .= ','."'$capabilities'";
		}
	}
		
	$out .= '<script type="text/javascript">
	var sec = new AwesomeChart(\'secCanvas\');
	sec.chartType = "horizontal bars";
        sec.title = "8 Most Popular Security Settings";
        sec.data = ['.$datas.'];
        sec.labels = ['.$labels.'];
        sec.draw();
	</script>';
	
	$out .= "<hr/>";
	
	$out .= '<div class="charts_container">
            <canvas id="openCanvas" width="500" height="500">
                Your web-browser does not support the HTML 5 canvas element.
            </canvas>
		</div>';
		
	$out .= '<script type="text/javascript">
	var open = new AwesomeChart(\'openCanvas\');
        open.title = "Open wifi vs Protected";
        open.data = ['.$stats['totalopen'].','.($stats['totalwifi']-$stats['totalopen']).'];
        open.labels = [\'Open\',\'Protected\'];
        open.draw();
	</script>';
	
	$out .= '<p><strong>'.(number_format(($stats['totalopen']/$stats['totalwifi'])*100, 3, '.', '')).'%</strong> totaly unprotected wifis</p>';
	
	$out .= Template::contentEnd();
	$out .= Template::footer();
	
	$file = fopen($cache,'w');
    fwrite($file,$out);
    fclose($file);
    echo file_get_contents($cache);

?>
