
if (!CURRENT_LANG) {
	CURRENT_LANG = "zh";
}
$(document).ready(function () {
	$("#datepicker").datepicker({onSelect:function (dateText, inst) {
		loadSubjectList(inst.selectedYear, inst.selectedMonth + 1, inst.selectedDay);
	}});
	var date = new Date();
	loadSubjectList(date.getFullYear(), date.getMonth() + 1, date.getDate());
	setLayoutSize();
	window.onresize = function () {
		setLayoutSize();
	};
	window.finished = function () {
		return true;
	};
	v();
	$("#topshrinkline").click(function () {
		$("#banner").toggle();
		setLayoutSize();
	});
	$("#shrinkline").click(function () {
		$("#menuarea").toggle();
		setLayoutSize();
	});
	$("#shrinkline").click();
});
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
}
function loadSubjectList(year, month, day) {
	if (month < 10) {
		month = "0" + month;
	}
	if (day < 10) {
		day = "0" + day;
	}
	var prefix = year + "/" + month + "/" + day + "/";
	var url = prefix + "list.xml";
	var handler = function (xml) {
		var items = $(xml).find("list>item");
		var titlelist = $("#titlelist");
		titlelist.html("");
		if (!items || items.length == 0) {
			noArticleNotice(year + "/" + month + "/" + day);
		} else {
			$("#outputmsg").html("");
			$(items).each(function () {
				var item = $(this);
				var subject = item.find("subject").text();
				var fileName = item.find("path").text();
				var li = $("<li></li>").appendTo(titlelist);
				var a = $("<a href=\"#\"></a>").appendTo(li);
				a.text(subject);
				a.click(function () {
					var frame = li.find("iframe");
					if (frame.exists()) {
						frame.remove();
					} else {
						loadArtile(prefix + fileName, subject, li);
					}
				});
			});
		}
	};
	var errorHandler = function (xhr, ajaxOptions, thrownError) {
		if (xhr.status == "404") {
			noArticleNotice(year + "/" + month + "/" + day);
		}
	};
	try {
		jtool.get(url, null, true, "xml", handler, errorHandler);
	}
	catch (e) {
		alert(e);
	}
}
function loadArtile(url, subject, li) {
	var frame = $("<iframe></iframe>").css("border", "0").css("width", "100%");
	frame.css("overflow", "hidden");
	frame.css("height", "1px");
	frame.css("padding", "0");
	frame.load(function () {
		var height;
		if (this.contentDocument) {
			height = $(this.contentDocument).height() + 10;
		} else {
			height = $(this.contentWindow).height() + 10;
		}
		frame.css("height", height + "px");
	});
	frame.attr("src", url).appendTo(li);
}
function noArticleNotice(date) {
	if (CURRENT_LANG == "en") {
		$("#outputmsg").html("No article at " + date + "!");
	} else {
		$("#outputmsg").html(date + "\u6ca1\u6709\u6587\u7ae0!");
	}
}
function slownote() {
	if (CURRENT_LANG == "en") {
		$("#outputmsg").html("Detecting your internet speed is slow!");
	} else {
		$("#outputmsg").html("\u68c0\u67e5\u53d1\u73b0\u4f60\u7684\u7f51\u901f\u5f88\u6162!");
	}
}

