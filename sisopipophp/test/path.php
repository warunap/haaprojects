<?php
/** Define ABSPATH as this files directory */

define( 'ABSPATH', dirname(__FILE__) . '/' );

echo 'dirname(__FILE__) --> ' . ABSPATH;
echo "<br/>";

echo 'dirname(ABSPATH) --> ' . dirname(ABSPATH);
echo "<br/>";

if ( file_exists( ABSPATH . 'index.php') ) {
	echo "exists index.php";
}

$arr =array('hello','world');
echo end($arr);


?>

<!DOCTYPE HTML>
<html lang="zh">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>RSS Reader</title>
<script type="text/javascript" src="http://127.0.0.1/js/init.js"></script>
<script type="text/javascript" src="http://127.0.0.1/js/jquery.js"></script>
<link type="text/css" rel="stylesheet"
	href="http://127.0.0.1/css/reset.css">
<link type="text/css" rel="stylesheet"
	href="http://127.0.0.1/css/common.css">
</head>
<body>
<div id="loading">
<H1>Loading ...</H1>
</div>
<div id="container" style="display: none;"><font id="outputmsg"></font>
<div id="banner"><a href="../index.html"><img src="../images/logo.gif"
	height="25" border="0" /> </a> <font>信息导读</font> <font id="lang"><a
	href="index-en.html">English</a> 中文 </font></div>
<div id="topshrinkline">&nbsp;</div>
<ul class="inlinelayout">
	<li class="left" id="menuarea">
	<div class="title">文章列表</div>
	<div>
	<ul id="titileList"></ul>
	</div>
	<div id="copyright">&copy;2010 SisoPipo</div>
	</li>
	<li class="mid" id="shrinkline">&nbsp;</li>
	<li class="right" id="viewarea">
	<div id="listHeader"><font class="title"><font id="todaydate"></font>文章阅读
	</font> >> <font id="tags" class="tags"> </font></div>
	<div id="viewcontainer"></div>
	</li>
</ul>
</div>
<script type="text/javascript">var RSS_LINK="http://wind6266.blog.163.com/rss/";</script>
<script type="text/javascript">script(["http://127.0.0.1/js/jtool.js"]);</script>
<script type="text/javascript">script(["http://127.0.0.1/rssreader/js/rssreader.js"]);</script>
<script type="text/javascript">link(["http://127.0.0.1/rssreader/css/rssreader.css"]);</script>
</body>
</html>
