<?php
/** ===============================================
 * 2010&copy;SisoPipo.com
 * Website Project
 * 
 * @author Geln Yang
 * @version 1.0
 ==================================================== */
define('DB_USER','root');
define('DB_PASS','password');
define('DB_HOST','localhost');
define('DB_DATABASE','test');

$DAO=null;
function connectDB(){
	if($DAO==NULL){
		$DAO=new PDO('mysql:host='. DB_HOST .';dbname=' . DB_DATABASE . ';charset=UTF-8', DB_USER, DB_PASS,array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES 'utf8';"));
	}
	RETURN $DAO;
}

function closeDB(){
	try{
		if($DAO!=NULL){
			$DAO=NULL;
		}
	}catch (PDOException $e) {
		print "Error!: " . $e->getMessage() . "<br/>";
	}
}

?>