(function($) {
	function isUndefined(obj) {
		return typeof obj === "undefined";
	}
	var option = {
		CLASS_VALID_ERROR : "validerror",
		ATTR_VALID : "isvalid",
		validation_error_formaterror : "format error!"
	}

	function isNativeType(type) {
		if (isUndefined(window.inputtype)) {
			window.inputtype = {};
		}
		if (isUndefined(window.inputtype[type])) {
			var i = document.createElement("input");
			i.setAttribute("type", type);
			window.inputtype[type] = (i.type !== "text");
		}
		return window.inputtype[type];
	}
	function isNativeAttr(type, attr) {
		return attr in document.createElement(type);
	}

	/*-------------------------------------------------
	getValidMessageBoxId
	 */
	function getValidMessageBoxId(item) {
		var msgid = "errormsg" + $(item).attr("name") + $(item).attr("id");
		msgid = msgid.replace(/\.|\[|\]/g, "_");
		return msgid;
	}

	/*-------------------------------------------------
	existsValidMessageBox
	 */
	function existsValidMessageBox(item) {
		return $(item).parent().find("lable[id='" + getValidMessageBoxId(item) + "']").length > 0;
	}

	/*-------------------------------------------------
	removeValidError
	 */
	function removeValidError(item) {
		$(item).removeAttr(option.ATTR_VALID);
		removeValidErrorMsg(item);
	}
	/*-------------------------------------------------
	removeValidErrorMsg
	 */
	function removeValidErrorMsg(item) {
		item = $(item);
		var msgid = getValidMessageBoxId(item);
		item.parent().find("lable[id='" + msgid + "']").remove();
		item.removeClass(option.CLASS_VALID_ERROR);
	}
	/*-------------------------------------------------
	addValidError
	 */
	function addValidError(item, msg) {
		item.attr(option.ATTR_VALID, false);
		var msgid = getValidMessageBoxId(item);
		var msgbox = $(item).parent().find("lable[id='" + msgid + "']");
		if (msgbox.length == 0) {
			msgbox = $("<lable>").attr("id", msgid).insertAfter(item);
			msgbox.addClass("errormessage").addClass("nopreview");
		}
		if (!msg) {
			msg = option.validation_common_error_formaterror;
		}
		msgbox.text(msg);
		item.addClass(option.CLASS_VALID_ERROR);
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
					}
					item.removeAttr("validateprocessing");
				}, 10);
			}
		}
	}
	/*-------------------------------------------------
	validateInput
	 */
	function validateInput(input) {
		var valid = true;
		var item = $(input);
		item.attr(option.ATTR_VALID, true);
		var val = item.val();
		/*----valid null-----*/
		var required = item.attr("required");
		var defaultNullValue = "";
		if (item.is("select")) {
			defaultNullValue = item.attr("nullvalue");
		}
		var msg = null;
		if (!isUndefined(required) && isNativeAttr("input", "required")) {
			if ($.trim(val) == defaultNullValue) {
				valid = false;
				var nullmessage = item.attr("nullmessage");
				if (typeof nullmessage == "undefined") {
					if (item.is("select")) {
						nullmessage = i18n.notice_common_info_pleaseSelect;
					} else {
						nullmessage = i18n.validation_common_error_notnull;
					}
				}
				addValidError(item, nullmessage);
				return false;
			}
			if (required == "true" && $.trim(val) == defaultNullValue) {
				removeValidError(item);
				return true;
			}
		}
		if (val == null) {
			val = "";
		}
		/*----valid format or type-----*/
		var dataformat = item.attr("dataformat");
		var datatype = item.attr("datatype");
		if (typeof dataformat != "undefined" && dataformat != "") {
			if (dataformat.indexOf("^") != 0 && !dataformat.indexOf("$") != dataformat.length) {
				dataformat = "^" + dataformat + "$";
			}
			var regex = new RegExp(dataformat, "g");
			if (!regex.test(val)) {
				valid = false;
			}
		} else {
			if (typeof datatype != "undefined" && datatype != "") {
				datatype = datatype.toLowerCase();
				var regex = null;

				switch (datatype) {
				case "email":
					val = val.toLowerCase();
					regex = "^(([_\\-][a-z0-9]+)|([a-z0-9]+))([\\._\\-][a-z0-9]+)*[a-z0-9]*@[a-z0-9\\-]+(\\.[a-z0-9\\-]+)*(\\.[a-z]{2,4})$";
					break;
				case "date":
					regex = "^\\d{4}/\\d{2}/\\d{2}$";
					break;
				case "number":
					regex = "^\\d+$";
					break;
				case "passwd":
					regex = "^.*[a-zA-Z]+.*$";
					break;
				case "cardnum":
					regex = "^((\\d{8})|(\\d{10})|(20\\d{11})|(80[123]01\\d{11}))$";
					break;
				case "cardnum16":
					regex = "^80[123]01\\d{11}$";
					break;
				case "serialnum":
					regex = "^\\d{8}$";
					break;
				case "twnumberdate":
					regex = "^\\d{6}$";
					break;
				case "twmobilephone":
					regex = "^09\\d{8}$";
					break;
				default:
					regex = null;
				}

				if (datatype == "passwd" && (val.length < 6 || val.length > 15)) {
					valid = false;
				}

				if (valid && regex != null && regex != "") {
					if (!new RegExp(regex, "g").test(val)) {
						valid = false;
					}
				}
				if (datatype == "cardnum16") {
					valid = (validateCardNum16(val));
					if (!valid) {
						msg = i18n.validation_common_error_cardformaterror;
					}
				}
				if (datatype == "idno") {
					valid = (checkIdNo(item[0]) || validateTaiwanIdNum(val));
					if (!valid) {
						msg = i18n.validation_common_error_invalididno;
					}
				} else if (datatype == "idnonative") {
					valid = (validateTaiwanIdNum(val));
					if (!valid) {
						msg = i18n.validation_common_error_invalididno;
					}
				} else if (datatype == "idnoforeign") {
					valid = (checkIdNo(item[0]));
					if (!valid) {
						msg = i18n.validation_common_error_invalididno;
					}
				}

				if (valid && datatype == "serialnum") {
					valid = checkSerialnum(val);
				}

				if (valid && datatype == "twnumberdate") {
					valid = checkBirth(val);
				}

			}
		}

		if (valid) {
			var limittextsize = item.attr("limittextsize");
			if (typeof limittextsize != "undefined" && limittextsize != "") {
				var count = countSubString(val, "\n");
				var length = stringBytesLength(val) + count;
				if (length > parseInt(limittextsize)) {
					valid = false;
					msg = i18n.validation_common_error_limit;
				}
			}
		}

		if (valid) {
			removeValidError(item);
			/* add valid attribute to avoid validating twice */
			item.attr(ATTR_VALID, true);
		} else {
			addValidError(item, msg);
		}
		return valid;
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
			$(form).find("input:not([.notvalid][type=button][type=submit]),select:not([.notvalid][type=button][type=submit])").each(function() {
				bindInput(this);
			});
		});
	}
	var SimpleValidator = window.SimpleValidator = {};
	$.fn.extend({
		bindSimpleValidator : function() {
			var obj = $(this);
			if (obj.is("input") || obj.is("select")) {
				SimpleValidator.bindInput(obj);
			} else if (obj.is("form")) {
				SimpleValidator.bindForm(obj);
			}
		}
	});
})(jQuery);