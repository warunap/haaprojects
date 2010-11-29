<?php
require_once '../_initialization.php';
define('RSS_LINK_TABLE','reader_rss');

function showRSS(){
	$db = connectDB();
	foreach($db->query('SELECT * from ' . RSS_LINK_TABLE) as $row) {
		print($row[0] . ','. $row[1] . ',' .$row[2]);
		print '<br/>';
	}
}

function getRSSJson(){
	$db = connectDB();
	$s='[';
	foreach($db->query('SELECT * from reader_rss') as $row) {
		$s .= '["' . $row[0] . '","'. $row[1] . '","' .$row[2] . '"],';
	}
	$s = substr($s,0,strlen($s)-1);
	$s.=']';
	return $s;
}

function addRSS($link, $tags){
	$xml=($link);
	$xmlDoc = new DOMDocument();
	$xmlDoc->load($xml);

	//get elements from "<channel>"
	$channel=$xmlDoc->getElementsByTagName('channel')->item(0);
	$channel_title = $channel->getElementsByTagName('title')->item(0)->childNodes->item(0)->nodeValue;
	$channel_link = $channel->getElementsByTagName('link')->item(0)->childNodes->item(0)->nodeValue;
	$channel_desc = $channel->getElementsByTagName('description')->item(0)->childNodes->item(0)->nodeValue;

	try {
		$db = connectDB();
		$sql="insert into ' . RSS_LINK_TABLE . '(rlink,rtitle,tags) values('" .$link ."','" . $channel_title ."','" . $tags ."')";
		$count = $db->exec($sql);
		print $sql;
		print  "<br/>";

	} catch (PDOException $e) {
		print "Error!: " . $e->getMessage() . "<br/>";
		die();
	}
}
?>