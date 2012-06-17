<?php
/** ===============================================
 * 2010&copy;SisoPipo.com
 * Website Project
 * 
 * @author Geln Yang
 * @version 1.0
 ==================================================== */
define('DB_USER','root');
define('DB_PASS','PASSWORD');
define('DB_HOST','localhost');
define('DB_DATABASE','sp');

$PDO_CONN_OBJ=NULL;

function connectDB(){
	global $PDO_CONN_OBJ; 
	if($PDO_CONN_OBJ==NULL){
		$PDO_CONN_OBJ = new PDO('mysql:host='. DB_HOST .';dbname=' . DB_DATABASE . ';charset=UTF-8', DB_USER, DB_PASS,array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES 'utf8';"));
	}
	return $PDO_CONN_OBJ;
}

function closeDB(){
	global $PDO_CONN_OBJ; 
	try{
		if($PDO_CONN_OBJ!=NULL){
			$PDO_CONN_OBJ=NULL;
		}
	}catch (PDOException $e) {
		print "Error!: " . $e->getMessage() . "<br/>";
	}
}

?>