<?php
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
	print '<script type="text/javascript" src="' . BASEURL . 'js/init.js' . '"></script>';
	print '<script type="text/javascript" src="' . BASEURL . 'js/jquery.js' . '"></script>';
}
function loadModuleJS($jspath){
	$pathInfo = pathinfo($_SERVER['PHP_SELF']);
	$path=	$pathInfo['dirname'] . '/';
	loadGlobalJs(substr($path,1) . $jspath);
}
function loadGlobalJs($jsabspath){
	if(is_array($jsabspath)){
		print '<script type="text/javascript">script([';
		$last_item = end($jsabspath);
		$last_value=$last_item['value'];
		foreach ($jsabspath as $path){
			print '"' . BASEURL . $path . '"';
			if($path!=$last_value){
				print ',';
			}
		}
		print ']);</script>';
	}else{
		print '<script type="text/javascript">script(["' . BASEURL . $jsabspath . '"]);</script>';
	}
}
function currentURLDir(){
	$pathInfo = pathinfo($_SERVER['PHP_SELF']);
	$path=	$pathInfo['dirname'] . '/';
	return BASEURL . substr($path,1);
}
/** -----------------------------
 * load CSS libaray
 ----------------------------*/
function initCSS(){
	addCSS('css/reset.css');
	addCSS('css/common.css');
}
function addCSS($csspath){
	print '<link type="text/css" rel="stylesheet" href="' . BASEURL . $csspath . '" >';
}
function loadModuleCSS($csspath){
	$pathInfo = pathinfo($_SERVER['PHP_SELF']);
	$path=	$pathInfo['dirname'] . '/';
	loadGlobalCSS(substr($path,1) . $csspath);
}
function loadGlobalCSS($csspath){
	if(is_array($csspath)){
		print '<script type="text/javascript">link([';
		$last_item = end($csspath);
		$last_value=$last_item['value'];
		foreach ($csspath as $path){
			print '"' . BASEURL . $path . '"';
			if($path!=$last_value){
				print ',';
			}
		}
		print ']);</script>';
	}else{
		print '<script type="text/javascript">link(["' . BASEURL . $csspath . '"]);</script>';
	}
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
	
	try{
		closeDB(); //not care about the exception
	} catch (Exception $e) {
	}
}
?>