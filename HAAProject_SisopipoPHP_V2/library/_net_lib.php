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

/** -----------------------------
 * This function returns the real hostname of an ip address.
 *
 * @param: $ip - the ip address in format x.x.x.x where x are
 *         numbers (0-255) or the hostname you want to lookup
 * @return: returns the hostname as string. Something like 'user-id.isp-dialin.tld'
 *
 * Warning: $ip must be validated before calling this function.
 ----------------------------*/
function nslookup($ip) {
	$dns='';
	// execute nslookup command
	exec('nslookup '.$ip, $op);

	// php is running on windows machine
	if (substr(php_uname(), 0, 7) == "Windows") {
		$dns= substr($op[3], 6);
	}
	else {
		// on linux nslookup returns 2 diffrent line depending on
		// ip or hostname given for nslookup
		if (strpos($op[4], 'name = ') > 0)
		$dns = substr($op[4], strpos($op[4], 'name =') + 7, -1);
		else
		$dns = substr($op[4], strpos($op[4], 'Name:') + 6);
	}

	return trim($dns);
}
?>