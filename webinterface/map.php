<?php

require_once('statsModel.php');
require_once('statModel.php');
require_once('templates/template.php');
require_once('configuration.php');

	$results = new statsModel();

	/* Presentation */
	
	/**
	 * This is a little messy, maybe move it to a separate .js
	 */
	  	    
    $script = '<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <script src="js/markerclusterer.js"></script>
    <style type="text/css">
      body { height: 100%; margin: 0; padding: 0 }
      #map_canvas { height: 100% }
    </style>
    <script type="text/javascript"
      src="http://maps.googleapis.com/maps/api/js?key='.$GLOBALS['googlemapapikey'].'&amp;sensor=false">
    </script>
    <script type="text/javascript">
      function initialize() {
        var myOptions = {
          center: new google.maps.LatLng(37.98, 23.73),
          zoom: 13,
          mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        var map = new google.maps.Map(document.getElementById("map_canvas"),
            myOptions);
		var markers = [];';
     $latitude = 0;
     $longtitude = 0;
     foreach($results->getResults(1) as $result) {
		 $script .= '
        markers.push(new google.maps.Marker({
	position: new google.maps.LatLng('.$result->getLatitude().', '.$result->getLongitude().'), 
        title:"<b>'.$result->getSsid().'</b> Power:'.$result->getLevel().' Security:'.$result->getCapabilities().'"
		}));';
	}
	$script .= '
		var markerClusterer = new MarkerClusterer(map, markers);
		// Listen for a cluster to be clicked
		google.maps.event.addListener(markerClusterer, \'clusterclick\', function(cluster) {
			var content = \'\';

			// Convert lat/long from cluster object to a usable MVCObject
			var info = new google.maps.MVCObject;
			info.set(\'position\', cluster.center_);

			//----
			//Get markers
			var markers = cluster.getMarkers();

			var titles = "";
			//Get all the titles
			for(var i = 0; i < markers.length; i++) {
				titles += markers[i].getTitle() + "</br>";
			}
			//----


			var infowindow = new google.maps.InfoWindow();
			infowindow.close();
			infowindow.setContent(titles); //set infowindow content to titles
			infowindow.open(map, info);

		});
		  }
    </script>';
	
    echo Template::header("Map",$script);
    echo '<div id="map">';
    echo '<div id="map_canvas" style="width:100%; height:100%"></div>';
    echo '</div>';
    
    echo '<script type="text/javascript">initialize()</script>';
        
    echo Template::footer();
    
?>
