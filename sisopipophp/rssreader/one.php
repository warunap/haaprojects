<?php
/** ===============================================
 * 2010&copy;SisoPipo.com
 * Website Project
 *
 * @author Geln Yang
 * @version 1.0
 ==================================================== */
require_once '_initialization_rss.php';
startPage('RSS Reader');
$link = $_REQUEST['link'];
if($link!=null)
{
	print('<div id="loading">Loading ...</div>');
	print('<font id="outputmsg"></font>');
	print('<div id="container" style="display: none;">');

	print('<div id="banner">');
	print('<a href="../index.html"><img src="../images/logo.gif" height="25" border="0" /> </a> ');
	print('<font>RSS阅读</font> >> ');
	print('<font id="rsstitle"></font> ');
	print('</div>');

	print('<div id="topshrinkline">&nbsp;</div>');

	print('<ul class="inlinelayout">');
	print('<li class="left" id="menuarea">');
	print('<div class="title">文章列表 </div>');
	print('<div id="titileList"><ul class="subject"></ul></div>');
	print('<div id="copyright">&copy;2010 SisoPipo</div>');
	print('</li>');

	print('<li class="mid" id="shrinkline">&nbsp;</li>');

	print('<li class="right" id="viewarea">');
	print('<div id="listHeader" class="title">文章阅读  >> </div>');
	print('<div id="viewcontainer">');
	print('</div>');
	print('</li>');

	print('</ul>');
	print('</div>');

	print('<script type="text/javascript">');
	print('var RSSRouterURL="' . currentURLDir() . 'rssxml.php";');
	print('var RSS_LINK="' . $link . '";');
	print('</script>');

	loadGlobalJs('js/jtool.js');
	loadModuleJS('js/rssreaderone.js');
	loadModuleCSS('css/rssreader.css');

}
else{
	print('No RSS LINK!');
}
endPage();
?>
