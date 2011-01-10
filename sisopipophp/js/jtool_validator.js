
/*
 * @version 1.0
 * @date:2010/10/01
 * @author:Geln Yang
 */
(function () {
	jtool.validator = {isNumeric:function (o) {
		return !isNaN(parseFloat(o)) && isFinite(o);
	}};
	if (!jtool.msg) {
		jtool.msg = {};
	}
	jtool.msg.MSG_CANT_BE_NULL = "\u4e0d\u80fd\u4e3a\u7a7a";
	jtool.msg.MSG_FORMAT_ERROR = "\u683c\u5f0f\u9519\u8bef";
	jtool.msg.MSG_MUST_BE_NUMBER = "\u5fc5\u9700\u4e3a\u6570\u5b57";
	jtool.limiter = {limit:function (msg, func, obj) {
		if (!func()) {
			if (obj) {
				$(obj).addClass("error");
			}
			throw msg;
		} else {
			if (obj) {
				$(obj).removeClass("error");
			}
		}
	}, notNull:function (object, name) {
		var obj = $(object);
		name = name ? name : obj.attr("name");
		jtool.limiter.limit(name + jtool.msg.MSG_CANT_BE_NULL + "!", function () {
			return obj.val() != "";
		}, obj);
	}, number:function (object, name) {
		var obj = $(object);
		name = name ? name : obj.attr("name");
		jtool.limiter.limit(name + jtool.msg.MSG_MUST_BE_NUMBER + "!", function () {
			return jtool.validator.isNumeric(obj.val());
		}, obj);
	}};
})(jtool);

