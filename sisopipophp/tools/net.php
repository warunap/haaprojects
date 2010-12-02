<?php
/** ===============================================
 * 2010&copy;SisoPipo.com
 * Website Project
 * 
 * @author Geln Yang
 * @version 1.0
 ==================================================== */
/** -----------------------------
 * get current URL
 ----------------------------*/
function getCurrentURL() {
	$currentURL = basename($_SERVER["PHP_SELF"]);
	$i = 0;
	foreach($_GET as $key => $value) {
		$i++;
		if($i == 1) { $currentURL .= "?"; }
		else { $currentURL .= "&amp;"; }
		$currentURL .= $key."=".$value;
	}
	return $currentURL;
}
?>