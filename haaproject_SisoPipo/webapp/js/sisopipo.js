
$(document).ready(function () {
	$("#datepicker").datepicker({onSelect:function (dateText, inst) {
		loadSubjectList(inst.selectedYear, inst.selectedMonth + 1, inst.selectedDay);
	}});
	setFrameSize();
	window.onresize = function () {
		setFrameSize();
	};
});
function setFrameSize() {
	var docwidth = $(document).width();
	var docheight = $(document).height();
	var leftwidth = $("#menuarea").width();
	var currentWidth = docwidth - leftwidth - 10;
	$("#viewSubject").css("width", currentWidth + "px");
	$("#viewframe").css("width", currentWidth + "px");
	$("#viewframe").css("height", (docheight - 150) + "px");
}
function loadSubjectList(year, month, day) {
	var prefix = year + "/" + month + "/" + day + "/";
	var url = prefix + "list.xml";
	var handler = function (xml) {
		var items = $(xml).find("list>item");
		var titlelist = $("#titlelist");
		titlelist.html("");
		$(items).each(function () {
			var item = $(this);
			var subject = item.find("subject").text();
			var fileName = item.find("path").text();
			var li = $("<li></li>").appendTo(titlelist);
			var a = $("<a href=\"#\"></a>").appendTo(li);
			a.click(function () {
				loadArtile(prefix + fileName, subject);
			});
			a.text(subject);
		});
	};
	try {
		jtool.get(url, handler, null, true, "xml");
	}
	catch (e) {
	}
}
function loadArtile(url, subject) {
	$("#viewSubject").text(subject);
	$("#viewframe").attr("src", url);
}

