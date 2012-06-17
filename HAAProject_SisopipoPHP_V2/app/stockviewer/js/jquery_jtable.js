
(function ($) {
	function chkjtable(obj) {
		if ($(obj).get(0).tagName.toLowerCase() != "table") {
			throw "Not a table DOM!";
		}
	}
	$.fn.extend({jtable:function () {
		return this.each(function () {
			chkjtable(this);
			this.is_jtable = true;
		});
	}, addTableLine:function (arr) {
		return this.each(function () {
			chkjtable(this);
			var tr = $("<tr>").appendTo(this);
			for (var i = 0; i < arr.length; i++) {
				$("<td>").appendTo(tr).text(arr[i]);
			}
		});
	}});
})(jQuery);

