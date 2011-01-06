
/*
 * @version 1.0
 * @date:2010/10/01
 * @author:Geln Yang
 */
(function () {
	jtool.validator = {isNumeric:function (input) {
		return (input - 0) == input && input.length > 0;
	}};
	if (!jtool.msg) {
		jtool.msg = {};
	}
	jtool.msg.MSG_CANT_BE_NULL = "\u4e0d\u80fd\u4e3a\u7a7a";
	jtool.msg.MSG_FORMAT_ERROR = "\u683c\u5f0f\u9519\u8bef";
	jtool.msg.MSG_MUST_BE_NUMBER = "\u5fc5\u9700\u4e3a\u6570\u5b57";
	jtool.limiter = {notNull:function (object, name) {
		var obj = $(object);
		name = name ? name : obj.attr("name");
		if (obj.val() == "") {
			obj.addClass("error");
			throw name + jtool.msg.MSG_CANT_BE_NULL + "!";
		} else {
			obj.removeClass("error");
		}
	}, number:function (object, name) {
		var obj = $(object);
		name = name ? name : obj.attr("name");
		if (jtool.validator.isNumeric(obj.val())) {
			obj.removeClass("error");
		} else {
			obj.addClass("error");
			throw name + jtool.msg.MSG_MUST_BE_NUMBER + "!";
		}
	}};
})(jtool);

