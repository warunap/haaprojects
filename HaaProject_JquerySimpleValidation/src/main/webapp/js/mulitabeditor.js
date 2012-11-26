/*-------------------------------------------------
author:gelnyang@gmail.com
*/
/*-------------------------------------------------
 the prefix of the IDs of edit box DIVs*/
var EDIT_BOX_PREFIX = "editBox_";
/*
 * ------------------------------------------------- the prefix of the IDs of edit titile FONTs
 */
var EDIT_TITLE_PREFIX = "editTitle_";
var CLASS_ACTIVE_EDIT = "editactive";
var CLASS_VALID_ERROR = "validerror";
var ATTR_VALID = "isvalid";
/*-------------------------------------------------
document.ready
*/
$j(document).ready(function () {
	$j("div[id^='editBox_']").hide();
	$j("div[id^='editBox_01']").show();
	var editElements = $j("input[dataformat],input[datatype],input[datanull],input[limittextsize],textarea[limittextsize]");
	editElements.on("blur", function () {
		triggerValidateInput(this);
	});
	editElements.on("change", function () {
		triggerValidateInput(this);
	});
	editElements.on("focus", function () {
		/* remove valid attribute to valid again on submit */
		$j(this).removeAttr(ATTR_VALID);
	});
	$j("select[datanull][nullvalue]").on("change", function () {
		triggerValidateInput(this);
	});
	bindLimittextsize();	
});

/*-------------------
 bind 'limitextsieze' attribute 
 */
function bindLimittextsize(id){
	var input;
	if(!id) {
		input = $j("#" + id);
	} else {
		input = $j("input[limittextsize],textarea[limittextsize]");
	}
	input.on("change",function(){
		limitInputTextSize($j(this), $j(this).attr("limittextsize"));
	});
	input.on("blur",function () {
		limitInputTextSize($j(this), $j(this).attr("limittextsize"));
	});
}
/*-------------------------------------------------
goToEditBox(btn, forward,num, validator, postProcessor)

btn:the button of link triggering the event
forward:whether forward ahead
num:the number of the tab forwarding to
validator:the validating function before forwarding, it don't forward if failed to valid  
postProcessor:the function invoked after validing and before forwarding
*/
function goToEditBox(btn, forward, num, validator, postProcessor) {
	var div = getDomParentByIdPrefix(btn, EDIT_BOX_PREFIX);
	if (forward && (!validateEditBox(div) | ( validator && !validator() ))) {
		return;
	}
	if (forward && postProcessor) {
		postProcessor();
	}
	
	$j("div[id^='" + EDIT_BOX_PREFIX + "']").hide();
	$j("font[id^='" + EDIT_TITLE_PREFIX + "']").removeClass(CLASS_ACTIVE_EDIT);
	$j("div[id^='" + EDIT_BOX_PREFIX + num + "']").show();
	$j("font[id^='" + EDIT_TITLE_PREFIX + num + "']").addClass(CLASS_ACTIVE_EDIT);
	
}
/*-------------------------------------------------
validFormOnsubmit(form, validator)
return true when success validating, false otherwise
*/
function validFormOnsubmit(form, validator) {
	if (!validateEditBox(form)) {
		return false;
	}
	if (validator && !validator()) {
		return false;
	}
	return true;
}
/*-------------------------------------------------
submitValidForm(btn, validator, postProcessor)

btn:the button of link triggering the event
validator:the validating function before submitting, it don't submit if failed to valid  
postProcessor:the function invoked after validing and before submitting
*/
function submitValidForm(btn, validator, postProcessor) {
	var form = getDomParent(btn, function (obj) {
		obj = $j(obj);
		if (obj.length > 0 && obj.get(0).nodeName.toLowerCase() == "form") {
			return true;
		}
		return false;
	});
	if (!validFormOnsubmit(form, validator)) {
		return;
	}
	if (postProcessor) {
		postProcessor();
	}
	try{$j(form).mask(i18n.page_loading_text , 0 ,null,true);}catch(e){}
	$j(form).submit();
}
/*-------------------------------------------------
validateEditBox
*/
function validateEditBox(box) {
	var valid = true;
	var editBox = $j(box);
	if (editBox.length > 0) {
		/* trigger the blur event to validate for the focus element */
		editBox.find("input:focus,textarea:focus").blur();
		
		editBox.find("input:text:not([" + ATTR_VALID + "]),input:password:not([" + ATTR_VALID + "]),textarea:not([" + ATTR_VALID + "]),select:not([" + ATTR_VALID + "])").each(function () {
			if (!validateInput(this)) {
				valid = false;
			}
		});
		
		var validErrorElement = editBox.find("input:text[" + ATTR_VALID + "=false],input:password[" + ATTR_VALID + "=false],textarea[" + ATTR_VALID + "=false],select[" + ATTR_VALID + "=false]");
		if (validErrorElement.length > 0) {
			valid = false;
		}
		
		if (validErrorElement.length > 0) {
			var first = null;
			for(var i = 0;i<validErrorElement.length;i++){
				if(!($j(validErrorElement[i]).is(':hidden'))){
					first = validErrorElement[i].focus();
					break;
				}
			}
			if(first != null){
				setTimeout(function(){if(!existsValidMessageBox(first)) validateInput(first)},10);
			}
		}
	}
	return valid;
}
/*-------------------
triggerValidateInput
*/
function triggerValidateInput(item){
	var item=$j(item);
	var processing = item.attr("validateprocessing");
	if (typeof processing == "undefined" || processing != "true") {
		item.attr("validateprocessing","true");
		setTimeout(function(){
			try{
				validateInput(item);
			}catch(e){}
			item.removeAttr("validateprocessing");}
		,10);
	}
}
/*-------------------------------------------------
validateInput
*/
function validateInput(input) {
	var valid = true;
	var item = $j(input);
	item.attr(ATTR_VALID, true);
	var val = item.val();
	/*----valid null-----*/
	var datanull = item.attr("datanull");
	var defaultNullValue = "";
	if(item.is("select")){
		defaultNullValue = item.attr("nullvalue");
	}
	var msg = null;
	if (typeof datanull != "undefined"  ) {
		if(datanull == "false" && $j.trim(val) == defaultNullValue){
			valid = false;
			var nullmessage = item.attr("nullmessage");
			if (typeof nullmessage == "undefined"  ){
				if(item.is("select")){
					nullmessage = i18n.notice_common_info_pleaseSelect;
				}else{
					nullmessage= i18n.validation_common_error_notnull;
				}
			}
			addValidError(item, nullmessage);
			return false;
		}
		if(datanull == "true" && $j.trim(val) == defaultNullValue){
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
		if(dataformat.indexOf("^")!=0 && !dataformat.indexOf("$")!=dataformat.length){
			dataformat="^"+dataformat+"$";
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
				val=val.toLowerCase();
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
			
			if(datatype == "passwd" && (val.length<6 || val.length>15)){
				valid= false;
			}
			
			if (valid && regex != null && regex != "") {
				if (!new RegExp(regex, "g").test(val)) {
					valid = false;
				}
			}
			if (datatype == "cardnum16") {
				valid = (validateCardNum16(val));
				if(!valid){
					msg = i18n.validation_common_error_cardformaterror;
				}
			}
			if(datatype == "idno"){
				valid = ( checkIdNo(item[0]) || validateTaiwanIdNum(val)) ;
				if(!valid){
					msg = i18n.validation_common_error_invalididno;
				}
			}else if(datatype == "idnonative"){
				valid = (validateTaiwanIdNum(val)) ;
				if(!valid){
					msg = i18n.validation_common_error_invalididno;
				}
			}else if(datatype == "idnoforeign"){
				valid = ( checkIdNo(item[0])) ;
				if(!valid){
					msg = i18n.validation_common_error_invalididno;
				}
			}
			
			if(valid && datatype == "serialnum"){
				valid = checkSerialnum(val);
			}
			
			if(valid && datatype == "twnumberdate"){
				valid = checkBirth(val);
			}
			
		}
	}
	
	if(valid){
		var limittextsize = item.attr("limittextsize");
		if (typeof limittextsize != "undefined" && limittextsize != "") {
			var count = countSubString(val, "\n");
			var length = stringBytesLength(val) + count;
			if(length>parseInt(limittextsize)){
				valid=false;
				msg=i18n.validation_common_error_limit;
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



/*-------------------------------------------------
getValidMessageBoxId
*/
function getValidMessageBoxId(item) {
	var msgid = "msg" + $j(item).attr("name") + $j(item).attr("id");
	msgid = msgid.replace(/\.|\[|\]/g, "_");
	return msgid;
}

/*-------------------------------------------------
existsValidMessageBox
*/
function existsValidMessageBox(item) {
	return $j(item).parent().find("font[id='" + getValidMessageBoxId(item) + "']").length > 0;
}

/*-------------------------------------------------
removeValidError
*/
function removeValidError(item) {
	$j(item).removeAttr(ATTR_VALID);
	removeValidErrorMsg(item);
}
/*-------------------------------------------------
removeValidErrorMsg
*/
function removeValidErrorMsg(item) {
	item = $j(item);
	var msgid = getValidMessageBoxId(item);
	item.parent().find("font[id='" + msgid + "']").remove();
	item.removeClass(CLASS_VALID_ERROR);
}
/*-------------------------------------------------
addValidError
*/
function addValidError(item, msg) {
	item.attr(ATTR_VALID, false);
	var msgid = getValidMessageBoxId(item);
	var msgbox = $j(item).parent().find("font[id='" + msgid + "']");
	if (msgbox.length == 0) {
		msgbox = $j("<font>").attr("id", msgid).insertAfter(item);
		msgbox.addClass("msgbox").addClass("nopreview");
	}
	if (!msg) {
		msg = i18n.validation_common_error_formaterror;
	}
	msgbox.text(msg);
	item.addClass(CLASS_VALID_ERROR);
}
/*-------------------------------------------------
addNoticeMessage
*/
function addNoticeMessage(item, msg) {
	var msgid = getValidMessageBoxId(item);
	var msgbox = $j(item).parent().find("font[id='" + msgid + "']");
	if (msgbox.length == 0) {
		msgbox = $j("<font>").addClass("msgbox").attr("id", msgid).insertAfter(item);
		msgbox.addClass("msgbox").addClass("nopreview");
	}
	msgbox.text(msg);
}
/*-------------------------------------------------
getDomParentByIdPrefix
*/
function getDomParentByIdPrefix(obj, id) {
	obj = $j(obj).parent();
	while (obj.length > 0 && (!obj.attr("id") || obj.attr("id").indexOf(id) != 0)) {
		obj = obj.parent();
	}
	if (obj.length == 0) {
		return null;
	}
	return obj;
}
/*-------------------------------------------------
getDomParentByIdPrefix
*/
function getDomParent(obj, compareFunc) {
	obj = $j(obj); // compare whether the current obj is the expected
	while (!compareFunc(obj)) {
		obj = obj.parent();
	}
	if (obj.length == 0) {
		return null;
	}
	return obj;
}
/*-------------------------------------------------
domPreview
*/
function domPreview(editDom, previewDom) {
	$j(".dompreview", editDom).remove();
	$j(editDom).find("input:text:not(.nopreview)").each(function () {
		var item = $j(this);
		var val = item.val();
		$j("<font>").addClass("dompreview").text(val).insertAfter(item);
	});
	
	$j(editDom).find("select:not(.nopreview)").each(function () {
		var item = $j(this);
		var text = item.find(":selected").text();
		$j("<font>").addClass("dompreview").text(text).insertAfter(item);
	});
	var box = $j(editDom).clone();
	$j(box).find("input:radio:checked:not(.nopreview)").each(function () {
		var item = $j(this);
		var val = item.attr("previewText");
		var parent = item.parent();
		parent.html("");
		$j("<font>").addClass("dompreview").text(val).appendTo(parent);
	});
	box.find(".nopreview").remove();
	box.find("img[onclick]").remove();
	$j(previewDom).html(box.html());
	$j(previewDom).find("input,select").remove();
	$j(previewDom).find(".dompreview").show();
	$j(previewDom).find(".hiddenpreview").show();
}
/*-------------------------------------------------
limitInputTextSize
*/
function limitInputTextSize(item,limitNum){
	item =$j(item);
	val = item.val();
	var count = countSubString(val, "\n");
	var length = stringBytesLength(val) + count;
	if (length > limitNum) {
		// while(length > limitNum){
		// val = val.substring(0, val.length -1);
		// length = stringBytesLength(val) + countSubString(val, "\n");
		// }
		item.val(val);
		addNoticeMessage(item,i18n.validation_common_error_limit);
	} else {
		if(item.prop("tagName")=="TEXTAREA"){
			addNoticeMessage(item,(limitNum - length) + " left");
		}
	}
}
/*-------------------------------------------------
count string byte length
*/
function stringBytesLength(str) {
	var count = 0;
	var escapedStr = encodeURI(str);
	escapedStr = escapedStr.replace(new RegExp("%20", "g")," ");
    if (escapedStr.indexOf("%") != -1) {
        var ucount = escapedStr.split("%").length - 1;
        if (ucount == 0) ucount++;  // perverse case; can't happen with real UTF-8
        var tmp = escapedStr.length - (ucount * 3);

        ucount = parseInt((ucount/3)*2); // utf8 length to big5 length
        count = ucount + tmp;
    } else {
        count = escapedStr.length;
    }
    
	return count;
}
/*-------------------------------------------------
count substring number
*/
function countSubString(mainStr, subStr) {
	var count = 0;
	var offset = 0;
	do {
		offset = mainStr.indexOf(subStr, offset);
		if (offset != -1) {
			count++;
			offset += subStr.length;
		}
	} while (offset != -1);
	return count;
}
