<?php
/** ===============================================
 * 2010&copy;SisoPipo.com
 * Website Project
 *
 * @author Geln Yang
 * @version 1.0
 ==================================================== */
header('Content-type:text/xml; charset=UTF-8');
$link=$_REQUEST["link"];

if($link!=null){
	$link = urldecode($link);
	$xmlDoc = new DOMDocument();
	$xmlDoc->load($link);
	$channel=$xmlDoc->getElementsByTagName('channel')->item(0);
	print($xmlDoc->saveXML());
}else{
	print('<MSG>No parameter "link"!</MSG>');
}
?>