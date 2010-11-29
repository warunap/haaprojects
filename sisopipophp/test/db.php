<?php
define('DB_USER','root');
define('DB_PASS','password');
echo DB_PASS;
try {
	$db = new PDO('mysql:host=localhost;dbname=test;charset=UTF-8', DB_USER, DB_PASS);
	foreach($db->query('SELECT * from IP') as $row) {
		print($row[0]);
	}
	$db = null;
} catch (PDOException $e) {
	print "Error!: " . $e->getMessage() . "<br/>";
	die();
}
?>
