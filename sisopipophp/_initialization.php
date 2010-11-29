<?php

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
	$port=$_SERVER['SERVER_PORT'];
	$url='http://' . $_SERVER['SERVER_ADDR'];
	if($port!='80')	{
		$url.= ':'.$port;
	}
	define('BASEURL',$url . '/');
}
/** -----------------------------
 * Load website configuration
 ----------------------------*/
require_once(ABSPATH . '_config_site.php');
/** -----------------------------
 * Load db configuration
 */
require_once(ABSPATH .'_config_db.php');

/** -----------------------------
 * set page title
 * @param $title page title
 * @param
 ----------------------------*/
function startPage($title){
	print '<!DOCTYPE HTML>';
	print '<html lang="zh">';
	print '<head>';
	print '<meta http-equiv="Content-Type" content="text/html; charset=' . PAGE_ENCODING . '">';
	print '<title>' .$title . '</title>';
	initJSLibrary();
	initCSS();
	print '</head>';
	print '<body>';
	setHTMLFlag();
}
/** -----------------------------
 * load base js libaray
 ----------------------------*/
function initJSLibrary(){
	loadGlobalJs('js/init.js');
}
function loadGlobalJs($jsabspath){
	print '<script type="text/javascript" src="' . BASEURL . $jsabspath . '"></script>';
}
function loadJS($jspath){
	$pathInfo = pathinfo($_SERVER['PHP_SELF']);
	$path=	$pathInfo['dirname'] . '/';
	lazyLoadJs(substr($path,1) . $jspath);
}
function lazyLoadJs($jsabspath){
	print '<script type="text/javascript">script(["' . BASEURL . $jsabspath . '"]);</script>';
}
/** -----------------------------
 * load CSS libaray
 ----------------------------*/
function initCSS(){
	loadGlobalCSS('css/reset.css');
	loadGlobalCSS('css/common.css');
}
function loadGlobalCSS($csspath){
	print '<link type="text/css" rel="stylesheet" href="' . BASEURL . $csspath . '" >';
}
function loadCSS($csspath){
	$pathInfo = pathinfo($_SERVER['PHP_SELF']);
	$path=	$pathInfo['dirname'] . '/';
	lazyLoadCSS(substr($path,1) . $csspath);
}
function lazyLoadCSS($csspath){
	print '<script type="text/javascript">link(["' . BASEURL . $csspath . '"]);</script>';
}
/** -----------------------------
 * set page content type and charset
 ----------------------------*/
function setHTMLFlag(){
	header('Content-type:text/html; charset=' . PAGE_ENCODING . '');
}
function setJsonFlag(){
	header('Content-type:text/x-json; charset=' . PAGE_ENCODING . '');
}
/** -----------------------------
 * add page ending tags
 ----------------------------*/
function endPage(){
	print '</body></html>';
}
?>