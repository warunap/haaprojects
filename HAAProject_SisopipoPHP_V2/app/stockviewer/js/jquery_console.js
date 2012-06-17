
(function ($) {
	var console = window.console = {msg:function (text) {
		var msgbox = $("#page_msg_box");
		if (msgbox.length <= 0) {
			msgbox = $("<div>").attr("id", "page_msg_box").appendTo($("body"));
		}
		msgbox.text(text);
	}, reset:function () {
		$("#page_msg_box").remove();
	}};
})(jQuery);

