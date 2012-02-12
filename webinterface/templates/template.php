<?php

class Template {
	
	public static function header($title="",$script="") {
		
		if(!empty($title))
			$title = ' &bull; '.$title; 
		
		$head = <<<HEAD
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
		<title>Easy Wifi Statistics$title</title>
		<link rel="stylesheet" href="templates/style.css" type="text/css" />
		$script
	</head>
	<body>
	<div id="menu">
		<img src="templates/Wifi_logo.png" alt="logo" height="60px" />
		<a href="results.php" class="abox">Results</a>
		<a href="map.php" class="abox">Map</a>
		<a href="stats.php" class="abox">Stats</a>
	</div>
HEAD;
		return $head;
	}
	
	public static function footer() {
		$foot = <<<FOOT
	</body>
</html>
FOOT;
		return $foot;
	}

	public static function contentStart() {
		return "<div id='content'>";
	}
	
	public static function contentEnd() {
		return "</div>";
	}
	
}

?>
