<?php
/** ===============================================
 * 2010&copy;SisoPipo.com
 * Website Project
 *
 * @author Geln Yang
 * @version 1.0
 ==================================================== */
$link=$_POST["link"];
header('Content-type:text/xml; charset=UTF-8');
if($link!=null){
	$xmlDoc = new DOMDocument();
	$xmlDoc->load($link);
	$channel=$xmlDoc->getElementsByTagName('channel')->item(0);
	print($xmlDoc->saveXML());
}else{
	print('<MSG>No parameter "link"!</MSG>');
}
?>