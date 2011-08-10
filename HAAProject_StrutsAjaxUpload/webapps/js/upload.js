
var _UPLOADER = context_path + "/js/uploadify.swf";
var _UPLOAD_SCRIPT = context_path + "/ajaxupload/upload.do";
var _DELETE_SCRIPT = context_path + "/ajaxupload/delete.do";
var _RECORD2SESSION_SCRIPT = context_path + "/ajaxupload/recordToSession.do";
var _CANCEL_IMAGE = context_path + "/images/cancel.png";
var _FILE_NAME_PREFIX = "ajaxupload_";
var _AJAX_FILE_NAME = "ajaxFile";
var _FILE_SIZE_LIMIT = 1048576 * 5; //limit to 5 MB
var _FILE_UPLOADING = false;
function isFileUploading() {
	return _FILE_UPLOADING;
}
function bindUploadFilePlugin(uploadid) {
	var fileInputDom = jQuery("#" + uploadid);
	var setting = {};
	setting["uploader"] = _UPLOADER;
	setting["script"] = _UPLOAD_SCRIPT;
	setting["cancelImg"] = _CANCEL_IMAGE;
	setting["sizeLimit"] = _FILE_SIZE_LIMIT;
	setting["auto"] = true;
	setting["multi"] = true;
	setting["removeCompleted"] = false;
	setting["fileDataName"] = _AJAX_FILE_NAME;
	setting["buttonText"] = "UPLOAD";
	setting["onComplete"] = function (event, ID, fileObj, response) {
		var name = _FILE_NAME_PREFIX + fileInputDom.attr("name");
		var fileId = jQuery(response).find("id").text();
		jQuery.ajax({"url":_RECORD2SESSION_SCRIPT, "method":"POST", "data":{"fileId":fileId}});
		var container = jQuery("#" + jQuery(event.target).attr("id") + ID);
		jQuery("<input class='ufile' type='hidden'/>").attr("name", name).val(fileId).appendTo(container);
	};
	setting["onCancel"] = function (event, ID, fileObj, data) {
		var container = jQuery("#" + jQuery(event.target).attr("id") + ID);
		var fileId = container.find("input.ufile").val();
		if (fileId != "") {
			jQuery.ajax({"url":_DELETE_SCRIPT, "method":"POST", "data":{"fileId":fileId}});
		}
	};
	setting["onProgress"] = function (event, ID, fileObj, data) {
		_FILE_UPLOADING = true;
		return true;
	};
	setting["onError"] = function (event, ID, fileObj, errorObj) {
		jQuery("#" + jQuery(event.target).attr("id") + ID).remove();
		if ("File Size" == errorObj.type) {
			alert("Fiel size is over the max " + (_FILE_SIZE_LIMIT / 1048576) + "MB!");
		} else {
			alert(errorObj.type + ":" + errorObj.info);
		}
		return false;
	};
	setting["onAllComplete"] = function (event, ID, fileObj, data) {
		_FILE_UPLOADING = false;
	};
	jQuery("#" + uploadid).uploadify(setting);
}

