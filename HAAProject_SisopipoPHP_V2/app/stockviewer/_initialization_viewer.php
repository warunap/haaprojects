<?php
/** ===============================================
 * 2010&copy;SisoPipo.com
 * Website Project
 *
 * @author Geln Yang
 * @version 1.0
 ==================================================== */
require_once '../../_initialization.php';
define('TABLE_QUERY_COMMAND','STOCK_QUERY_COMMAND');

function getTopBuy($size){
	$db = connectDB();
	$s='{list:[';
	$result =$db->query('SELECT CKEY,COMMAND from ' . TABLE_QUERY_COMMAND . ' where CKEY="STOCK_TOP_BUY" ');
	$row = $result->fetch(PDO::FETCH_ASSOC);
	$sql = $row['COMMAND'] . ' limit ' . $size;
	foreach($db->query($sql) as $stock) {
		$s .= '{';
		$s .= 'code:' . $stock['code'];		
		$s .= '},';
	}
	$s = substr($s,0,strlen($s)-1);
	$s.=']}';
	return $s;
}

?>