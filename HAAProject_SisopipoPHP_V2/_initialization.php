<?php
/** ===============================================
 * 2010&copy;SisoPipo.com
 * Website Project
 ==================================================== */
/** -------------------------
 Initial abstract path
 ----------------------------*/
if ( !defined('ABSPATH') ){
	define('ABSPATH', dirname(__FILE__) . '/');
}
/** -----------------------------
 Initial Base URL path
 ----------------------------*/
if (!defined('BASEURL')){
	$url='http://' . $_SERVER['HTTP_HOST'];
	define('BASEURL',$url . '/');
}

require_once(ABSPATH . '_config_site.php');
require_once(ABSPATH .'_config_db.php');
require_once(ABSPATH .'/library/_html_content_lib.php');


?>