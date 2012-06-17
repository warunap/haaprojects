<?php
/** ===============================================
 * 2010&copy;SisoPipo.com
 * Website Project
 *
 * @author Geln Yang
 * @version 1.0
 ==================================================== */
require_once ('_initialization_rss.php');
startPage('RSS Reader');
loadModuleCSS('css/rssreader.css');
loadGlobalJs('js/jtool.js');
loadModuleJS('js/rssreader.js');
$db = connectDB();
print('<div><img src="images/rssreader.jpg" class="icon" border="0" />');
print('<h3>选择阅读</h3>');
print('<hr/>');
print('<ul>');
foreach($db->query('SELECT * from ' . RSS_LINK_TABLE) as $row) {
	print('<li>');
	print('[' . $row['tags'] . ']<a href="#" onclick="javascript:readrss(\'' . $row['rlink'] . '\')" >' .  $row['rtitle'] . '</a>');
	print ('</li>');
}
print("<ul></div>");
endPage(); ?>