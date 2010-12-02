/*============================================
 * 2010&copy;SisoPipo.com Website Project
 * 
 * @author Geln Yang
 * @version 1.0
 ============================================*/
var times = 0;
var FINISHED_LOAD_RSS = false;
var NOT_BEGIN_LOAD = true;
var loadFontSize = 24;
function v() {
	var loading = document.getElementById("loading");
	if ($ && jtool && NOT_BEGIN_LOAD) {
		NOT_BEGIN_LOAD = false;
		loadRSS();
	}
	if ((FINISHED_LOAD_RSS) || times++ > 10) {
		if (loading) {
			document.body.removeChild(loading);
		}
		document.getElementById("container").style.display = "";
		if (times > 10) {
			slownote();
		}
		initialPage();
	} else {
		if (loading) {
			var text = loading.innerHTML;
			loading.innerHTML = text + ".";
			loading.style.fontSize = loadFontSize + "px";
			loadFontSize += 5;
		}
		setTimeout("v()", 1000);
	}
}
setTimeout("v()", 1000);
/*---------------------------------------
 Initial Page Layout
 ---------------------------------------*/
function initialPage() {
	window.onresize = function() {
		setLayoutSize();
	};
	$("#topshrinkline").click(function() {
		$("#banner").toggle();
		setLayoutSize();
	});
	$("#shrinkline").click(function() {
		$("#menuarea").toggle();
		setLayoutSize();
	});
	setLayoutSize();
	$("#shrinkline").click();
	showmsg("");
};

/*---------------------------------------
 Load RSS Link
 ----------------------------------------*/
function loadRSS() {
	var handler = function(xml) {
		var rss_title = $(xml).find("rss>channel>title").text();
		rss_title = rss_title.replace(/^\[CDATA\[/, '').replace(/\]\]$/, '');
		$("#rsstitle").text(rss_title);
		document.title = rss_title;
		var articleList = $(xml).find("rss>channel>item");
		if (!articleList || articleList.length == 0) {
			alert("No article!");
		} else {
			var titileListContainer = $("#titileList").find("ul")[0];
			titileListContainer = $(titileListContainer);
			$(articleList).each(function() {
				var item = $(this);
				var title = item.find("title").text();
				// var link = item.find("link").text();
				var description = item.find("description").text();
				description = description.replace(/^\[CDATA\[/, '').replace(/\]\]$/, '');
				var li = $("<li></li>").appendTo(titileListContainer);
				var a = $("<a href=\"#\"></a>").appendTo(li);
				a.text(title);
				a.click(function() {
					$("#viewcontainer").html(description);
				});

			});

			titileListContainer.find("li>a:first").click();

		}
	};
	var errorHandler = function(xhr, ajaxOptions, thrownError) {
		if (xhr.status == "404") {
			alert("Can't find RSS Resource:" + RSS_LINK);
		}
	};
	try {
		jtool.get(RSSRouterURL, {
			"link" : RSS_LINK
		}, true, "xml", handler, errorHandler);
	} catch (e) {
		alert(e);
	}

}
/*---------------------------------------
 Set Page Layout Size
 ----------------------------------------*/
function setLayoutSize() {
	var leftwidth = $("#menuarea").width();
	if (!$("#menuarea").is(":visible")) {
		leftwidth = 0;
	}
	var bannerHeight = $("#banner").height();
	if (!$("#banner").is(":visible")) {
		bannerHeight = 0;
	}
	var shrinklineWidth = $("#shrinkline").width();
	var currentWidth = $(window).width() - leftwidth - shrinklineWidth - 3;
	var currentHeight = $(window).height() - bannerHeight - $("#topshrinkline").height();
	$("#shrinkline").css("height", currentHeight + "px");
	$("#viewSubject").css("width", currentWidth + "px");
	$("#viewarea").css("width", currentWidth + "px");
	var containerHeight = $(window).height() - bannerHeight - $("#topshrinkline").height() - $("#listHeader").height() - 3;
	$("#viewcontainer").css("height", containerHeight + "px");
	$("#titileList").css("height", containerHeight + "px");
}
/*---------------------------------------
 Show message that the internate speed is slow!
 ---------------------------------------*/
function slownote() {
	showmsg("\u68c0\u67e5\u53d1\u73b0\u4f60\u7684\u7f51\u901f\u5f88\u6162!");
}
/*---------------------------------------
 Show message 
 ---------------------------------------*/
function showmsg(msg) {
	var container = $("#outputmsg");
	if (!container.exists()) {
		container = $("<font id=\"outputmsg\"></font>").appendTo($("body"));
	}
	container.html(msg);
}
