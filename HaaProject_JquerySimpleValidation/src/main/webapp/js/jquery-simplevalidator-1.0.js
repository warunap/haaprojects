(function($) {
	var CF = {
		CLASS_VALID_ERROR : "validerror",
		ATTR_VALID : "isvalid",
		ATTR_BIND : "sv_binded"
	}, regexMap = {
		"email" : "^(([_\\-][a-z0-9]+)|([a-z0-9]+))([\\._\\-][a-z0-9]+)*[a-z0-9]*@[a-z0-9\\-]+(\\.[a-z0-9\\-]+)*(\\.[a-z]{2,4})$",
		"date" : "^\\d{4}/\\d{2}/\\d{2}$",
		"number" : "^\\d+$",
		"passwd" : "^.*[a-zA-Z]+.*$",
		"date" : "^\\d{4}/\\d{1,2}/\\d{1,2}$"
	}, i18n = {
		please_select : "please select!",
		required : "required!",
		fromat_error : "format error!"
	}, getText = function(key) {
		return i18n[key];
	}, isUndefined = function(obj) {
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
	},
	/* reference:http://sisopipo.com/blog/archives/351 */
	utf8ByteLength = function(str) {
		if (!str)
			return 0;
		var escapedStr = encodeURI(str);
		var match = escapedStr.match(/%/g);
		return match ? (escapedStr.length - match.length * 2) : escapedStr.length;
	}, dbcsByteLength = function(str) {
		if (!str)
			return 0;
		var count = 0;
		for ( var i = 0; i < str.length; i++) {
			count++;
			if (str.charCodeAt(i) >> 8)
				count++;
		}
		return count;
	}
	/*-------------------------------------------------
	getValidMessageBoxId
	 */
	getValidMessageBox = function(item) {
		var lable = $(item).next();
		if (lable.length <= 0) {
			lable = $("<lable>").addClass("errormessage").addClass("nopreview").insertAfter(item);
		}
		return lable;
	},
	/*-------------------------------------------------
	removeValidError
	 */
	removeValidError = function(item) {
		getValidMessageBox(item).remove();
		$(item).removeAttr(CF.ATTR_VALID);
		$(item).removeClass(CF.CLASS_VALID_ERROR);
	},
	/*-------------------------------------------------
	addValidError
	 */
	addValidError = function(item, msg) {
		item.attr(CF.ATTR_VALID, false);
		item.addClass(CF.CLASS_VALID_ERROR);
		var msgbox = getValidMessageBox(item);
		if (!msg) {
			msg = getText('fromat_error');
		}
		msgbox.text(msg);
	}, inputValidatorChain = [
	/* required validator */
	function(item) {
		if (isUndefined(item.attr("required")) || (item.is("input") && isNativeAttr("input", "required")) || (item.is("textarea") && isNativeAttr("textarea", "required"))) {
			return true;
		}
		var val = item.val();
		var defaultNullValue = "";
		if (item.is("select") && !isUndefined(item.attr("nullvalue"))) {
			var optionval = item.find("option.nullvalue").val();
			if (optionval) {
				defaultNullValue = optionval;
			}
		}
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
		return true;
	},
	/* length validator */
	function(item) {
		if ((item.is("input") && isNativeAttr("input", "maxlength")) || (item.is("textarea") && isNativeAttr("textarea", "maxlength")) || item.val() == "") {
			return true;
		}
		var valid = true;
		var maxlength = item.attr("maxlength");
		if (!isUndefined(maxlength) && maxlength != "" && parseInt(maxlength) > 0) {
			if (item.val().length > parseInt(maxlength)) {
				valid = false;
			}
		}
		if (valid) {
			var maxUTF8ByteLength = item.attr("maxUTF8ByteLength");
			if (!isUndefined(maxUTF8ByteLength) && maxUTF8ByteLength != "" && parseInt(maxUTF8ByteLength) > 0) {
				if (utf8ByteLength(item.val()) > parseInt(maxUTF8ByteLength)) {
					valid = false;
				}
			}
		}
		if (valid) {
			var maxDBCSByteLength = item.attr("maxDBCSByteLength");
			if (!isUndefined(maxDBCSByteLength) && maxDBCSByteLength != "" && parseInt(maxDBCSByteLength) > 0) {
				if (dbcsByteLength(item.val()) > parseInt(maxDBCSByteLength)) {
					valid = false;
				}
			}
		}

		if (!valid) {
			addValidError(item, item.attr("lengtherrormessage"));
		}
		return valid;
	},
	/* pattern validator */
	function(item) {
		if (!item.is("input") || isNativeAttr("input", "pattern")) {
			return true;
		}
		var pattern = item.attr("pattern");
		var val = item.val();
		if (!isUndefined(pattern) && pattern != "" && val != "") {
			if (pattern.indexOf("^") != 0 && !pattern.indexOf("$") != pattern.length) {
				pattern = "^" + pattern + "$";
			}
			var regex = new RegExp(pattern, "g");
			if (!regex.test(val)) {
				addValidError(item, item.attr("patternerrormessage"));
				return false;
			}
		}
		return true;
	},
	/* type validator */
	function(item) {
		if (!item.is("input") || isNativeType(item.attr("type"))) {
			return true;
		}
		var val = item.val();
		var type = item.attr("type").toLowerCase();
		var regex = regexMap[type];
		if (regex != null && regex != "" && val != "") {
			if (!new RegExp(regex, "g").test(val)) {
				addValidError(item, item.attr("typeerrormessage"));
				return false;
			}
		}
		return true;
	},
	/* range validator */
	function(item) {
		if (!item.is("input") || item.attr("type") != "range" || isNativeType("range")) {
			return true;
		}
		var val = item.val();
		var min = item.attr("min");
		var max = item.attr("max");
		if (val != "" && (!isUndefined(min) || !isUndefined(max))) {
			try {
				if (!isUndefined(min) && (parseFloat(val) < parseFloat(min)) || (!isUndefined(max) && parseFloat(val) > parseFloat(max))) {
					addValidError(item, item.attr("rangeerrormessage"));
					return false;
				}
			} catch (e) {
				alert(e);
				addValidError(item, item.attr("formaterrormessage"));
				return false;
			}
		}
		return true;
	},
	/* relation validator */
	function(item) {
		if (!item.is("input")) {
			return true;
		}
		var valid = true;
		var equalTo = item.attr("equalTo");
		if (!isUndefined(equalTo)) {
			var target = $('' + equalTo);
			if (target.length > 0) {
				try {
					if (item.val() != target.val()) {
						valid = false;
					}
				} catch (e) {
					alert(e);
					valid = false;
				}
				if (!valid) {
					var msg = item.attr("equalerrormessage");
					addValidError(item, msg ? msg : "not equal to " + target.attr("name"));
					return false;
				}
			}
		}
		var greatThan = item.attr("greatThan");
		if (!isUndefined(greatThan)) {
			var target = $('' + greatThan);
			if (target.length > 0) {
				try {
					if (parseFloat(item.val()) < parseFloat(target.val())) {
						valid = false;
					}
				} catch (e) {
					alert(e);
					valid = false;
				}
				if (!valid) {
					var msg = item.attr("greatthanerrormessage");
					addValidError(item, msg ? msg : "must great than " + target.val());
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
		item = $(item);
		if (isUndefined(item.attr("validateprocessing"))) {
			item.attr("validateprocessing", "true");
			try {
				validateInput(item);
			} catch (e) {
				alert(e);
			}
			/* don't validate again in 200ms */
			setTimeout(function() {
				item.removeAttr("validateprocessing");
			}, 200);
		}
	}
	/*-------------------
	validateForm
	 */
	function validateForm(form, validator) {
		var valid = true;
		form = $(form);
		/* trigger the blur event to validate for the focus element */
		form.find("input:focus,textarea:focus").blur();
		form.find("input[" + CF.ATTR_BIND + "]:not([" + CF.ATTR_VALID + "]),textarea[" + CF.ATTR_BIND + "]:not([" + CF.ATTR_VALID + "]),select[" + CF.ATTR_BIND + "]:not([" + CF.ATTR_VALID + "])")
				.each(function() {
					if (!validateInput(this)) {
						valid = false;
					}
				});

		var errorElements = form.find("input[" + CF.ATTR_VALID + "=false],textarea[" + CF.ATTR_VALID + "=false],select[" + CF.ATTR_VALID + "=false]");
		if (errorElements.length > 0) {
			valid = false;
		}

		if (errorElements.length > 0) {
			for ( var i = 0; i < errorElements.length; i++) {
				if (!($(errorElements[i]).is(':hidden'))) {
					errorElements[i].focus();
					break;
				}
			}
		}
		return valid ? (!validator || validator.call()) : false;
	}
	/*-------------------
	isBinded
	 */
	function isBinded(obj) {
		return !isUndefined($(obj).attr(CF.ATTR_BIND));
	}
	function bind(obj, func) {
		if (!isBinded(obj)) {
			$(obj).attr(CF.ATTR_BIND, true);
			func.call();
		}
	}
	function unbind(obj) {
		$(obj).removeAttr(CF.ATTR_BIND);
	}
	function bindInput(input) {
		bind(input, function() {
			$(input).on("blur", function() {
				if (isBinded(this))
					triggerValidateInput(this);
			});
			$(input).on("change", function() {
				if (isBinded(this))
					triggerValidateInput(this);
			});
		});
	}
	function unbindInput(input) {
		unbind(input);
		$(input).unbind("blur").unbind("change");
		removeValidError(input);
	}
	function bindForm(form, validator) {
		bind(form, function() {
			$(form).find("input:not(.novalid[type=button][type=submit]),select:not(.novalid),textarea:not(.novalid)").each(function() {
				bindInput(this);
			});
			$(form).submit(function() {
				return !isBinded(form) || validateForm(form, validator);
			});
		});
	}
	function unbindForm(form) {
		unbind(form);
		$(form).unbind('submit').find("input[" + CF.ATTR_BIND + "],select[" + CF.ATTR_BIND + "],textarea[" + CF.ATTR_BIND + "]").each(function() {
			unbindInput(this);
		});
	}

	/* extend jquery */
	$.fn.extend({
		bindSimpleValidator : function(validator) {
			var obj = $(this);
			if (obj.is("input") || obj.is("select") || obj.is("textarea")) {
				bindInput(obj);
			} else if (obj.is("form")) {
				bindForm(obj, validator);
			}
		},
		unbindSimpleValidator : function() {
			var obj = $(this);
			if (obj.is("input") || obj.is("select") || obj.is("textarea")) {
				unbindInput(obj);
			} else if (obj.is("form")) {
				unbindForm(obj);
			}
		},
		simpleValidate : function() {
			var obj = $(this);
			if (obj.is("input") || obj.is("select") || obj.is("textarea")) {
				return validateInput(obj);
			} else if (obj.is("form")) {
				return validateForm(obj);
			}
		},
		addValidError : function(msg) {
			addValidError($(this), msg);
		},
		removeValidError : function() {
			removeValidError($(this));
		}
	});
})(jQuery);