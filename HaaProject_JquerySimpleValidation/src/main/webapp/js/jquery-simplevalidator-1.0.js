(function($) {
	var isUndefined = function(obj) {
		return typeof obj === "undefined";
	}, nativetypes = {}, nativeattrs = {}, isNativeType = function(type) {
		if (isUndefined(nativetypes[type])) {
			var i = document.createElement("input");
			i.setAttribute("type", type);
			nativetypes[type] = (i.type !== "text");
		}
		return nativetypes[type];
	}, isNativeAttr = function(type, attr) {
		var key = type + "_" + attr;
		if (isUndefined(nativeattrs[key])) {
			nativeattrs[key] = (attr in document.createElement(type));
		}
		return nativeattrs[key];
	}, CF = {
		CLASS_VALID_ERROR : "validerror",
		ATTR_VALID : "isvalid"
	}, i18n = {
		fromat_error : "format error!"
	}, getText = function(key) {
		return i18n[key];
	}, regexMap = {
		"email" : "^(([_\\-][a-z0-9]+)|([a-z0-9]+))([\\._\\-][a-z0-9]+)*[a-z0-9]*@[a-z0-9\\-]+(\\.[a-z0-9\\-]+)*(\\.[a-z]{2,4})$",
		"date" : "^\\d{4}/\\d{2}/\\d{2}$",
		"number" : "^\\d+$",
		"passwd" : "^.*[a-zA-Z]+.*$",
		"date" : "^\\d{4}/\\d{1,2}/\\d{1,2}$"
	},
	/*-------------------------------------------------
	getValidMessageBoxId
	 */
	getValidMessageBoxId = function getValidMessageBoxId(item) {
		var msgid = "errormsg" + $(item).attr("name") + $(item).attr("id");
		msgid = msgid.replace(/\.|\[|\]/g, "_");
		return msgid;
	},

	/*-------------------------------------------------
	existsValidMessageBox
	 */
	existsValidMessageBox = function(item) {
		return $(item).parent().find("lable[id='" + getValidMessageBoxId(item) + "']").length > 0;
	},
	/*-------------------------------------------------
	removeValidErrorMsg
	 */
	removeValidErrorMsg = function(item) {
		item = $(item);
		var msgid = getValidMessageBoxId(item);
		item.parent().find("lable[id='" + msgid + "']").remove();
		item.removeClass(CF.CLASS_VALID_ERROR);
	},
	/*-------------------------------------------------
	removeValidError
	 */
	removeValidError = function(item) {
		$(item).removeAttr(CF.ATTR_VALID);
		removeValidErrorMsg(item);
	},
	/*-------------------------------------------------
	addValidError
	 */
	addValidError = function(item, msg) {
		item.attr(CF.ATTR_VALID, false);
		var msgid = getValidMessageBoxId(item);
		var msgbox = $(item).parent().find("lable[id='" + msgid + "']");
		if (msgbox.length == 0) {
			msgbox = $("<lable>").attr("id", msgid).insertAfter(item);
			msgbox.addClass("errormessage").addClass("nopreview");
		}
		if (!msg) {
			msg = getText('fromat_error');
		}
		msgbox.text(msg);
		item.addClass(CF.CLASS_VALID_ERROR);
	}, inputValidatorChain = [
	/* required validator */
	function(item) {
		var val = item.val();
		var defaultNullValue = "";
		if (item.is("select") && !isUndefined(item.attr("nullvalue"))) {
			defaultNullValue = item.attr("nullvalue");
		}
		if (!isUndefined(item.attr("required")) && !isNativeAttr("input", "required")) {
			if ($.trim(val) == defaultNullValue) {
				valid = false;
				var nullmessage = item.attr("requiredmessage");
				if (isUndefined(nullmessage)) {
					if (item.is("select")) {
						nullmessage = getText('please_select');
					} else {
						nullmessage = getText('required');
					}
				}
				addValidError(item, nullmessage);
				return false;
			}
		}
		return true;
	},
	/* length validator */
	function(item) {
		var minlength = item.attr("minlength");
		if (!isUndefined(minlength) && minlength != "" && parseInt(minlength) > 0 && !isNativeAttr("input", "minlength")) {
			if (item.val().length < parseInt(minlength)) {
				addValidError(item, input.attr("lengtherrormessage"));
				return false;
			}
		}
		return true;
	},
	/* pattern validator */
	function(item) {
		var pattern = item.attr("pattern");
		if (!isUndefined(pattern) && pattern != "" && !isNativeAttr("input", "pattern")) {
			if (pattern.indexOf("^") != 0 && !pattern.indexOf("$") != pattern.length) {
				pattern = "^" + pattern + "$";
			}
			var regex = new RegExp(pattern, "g");
			if (!regex.test(val)) {
				addValidError(item, input.attr("patternerrormessage"));
				return false;
			}
		}
		return true;
	},
	/* type validator */
	function(item) {
		var type = item.attr("type");
		if (!isUndefined(type) && type != "" && !isNativeType(type)) {
			type = type.toLowerCase();
			var regex = regexMap[type];
			if (regex != null && regex != "") {
				if (!new RegExp(regex, "g").test(item.val())) {
					addValidError(item, item.attr("typeerrormessage"));
					return false;
				}
			}
		}
		return true;
	} ];
	/*-------------------------------------------------
	validateInput
	 */
	function validateInput(input) {
		for ( var i in inputValidatorChain) {
			if (!inputValidatorChain[i].call(this, $(input))) {
				$(input).attr(CF.ATTR_VALID, false);
				return false;
			}
		}
		removeValidError(input);
		return true;
	}

	/*-------------------
	triggerValidateInput
	 */
	function triggerValidateInput(item) {
		if (isBinded(item)) {
			item = $(item);
			var processing = item.attr("validateprocessing");
			if (typeof processing == "undefined" || processing != "true") {
				item.attr("validateprocessing", "true");
				setTimeout(function() {
					try {
						validateInput(item);
					} catch (e) {
						alert(e);
					}
					item.removeAttr("validateprocessing");
				}, 10);
			}
		}
	}

	function bind(obj, func) {
		if (!isBinded(obj)) {
			$(obj).attr("sv_binded", true);
			func.call();
		}
	}
	function unbind(obj) {
		$(obj).removeAttr("sv_binded");
	}
	function isBinded(obj) {
		return !(typeof $(obj).attr("sv_binded") === "undefined");
	}
	function bindInput(input) {
		bind(input, function() {
			$(input).on("blur", function() {
				triggerValidateInput(this);
			});
			$(input).on("change", function() {
				triggerValidateInput(this);
			});
		});
	}
	function bindForm(form) {
		bind(form, function() {
			$(form).find("input:not(.notvalid[type=button][type=submit]),select:not(.notvalid[type=button][type=submit])").each(function() {
				bindInput(this);
			});
		});
	}
	$.fn.extend({
		bindSimpleValidator : function() {
			var obj = $(this);
			if (obj.is("input") || obj.is("select")) {
				bindInput(obj);
			} else if (obj.is("form")) {
				bindForm(obj);
			}
		}
	});
})(jQuery);