<?php
/** ===============================================
 * 2010&copy;SisoPipo.com
 * Website Project
 * 
 * @author Geln Yang
 * @version 1.0
 ==================================================== */
require_once '_initialization_rss.php';
startPage('Add RSS Resource');

$link=$_POST["link"];
$tags=$_POST["tags"];


if($link == null || $tags==null ){
	print '<form method="post">';
	if($link == null){
		print "Please input link:<br/>";
	}
	print 'URL:<input name="link" size="100" maxlength="200" value="' . $link . '" /><br />';
	if($tags == null){
		print "Please input tags:<br/>";
	}
	print 'Tags:<input name="tags" size="100" maxlength="200" value="' . $tags . '"  /><br />';
	print '<input type="submit" value="submit" />';
	print '</form>';
}
else{
	addRSS($link, $tags);
}
print '<hr/>';
showRSS();
closeDB();
endPage();
?>