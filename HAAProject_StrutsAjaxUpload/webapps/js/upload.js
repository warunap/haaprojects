
var _UPLOADER = context_path + "/js/uploadify.swf";
var _UPLOAD_SCRIPT = context_path + "/upload.do";
var _CANCEL_IMAGE = context_path + "/images/cancel.png";
jQuery(document).ready(function () {
	bindUploadFilePlugin("upload_file");
});
function bindUploadFilePlugin(uploadid) {
	var fileInputDom = jQuery("#" + uploadid);
	var filelist = jQuery("#" + uploadid + "_filelist");
	filelist.insertBefore(fileInputDom);
	jQuery("#" + uploadid).uploadify({"uploader":_UPLOADER, "script":_UPLOAD_SCRIPT, "cancelImg":_CANCEL_IMAGE, "auto":true, "fileDataName":"fileData", "removeCompleted":false, "onComplete":function (event, ID, fileObj, response) {
		var name = fileInputDom.attr("name") + "_up";
		var fileId = jQuery(response).find("id").text();
		var input = jQuery("<input type=\"hidden\"/>").attr("name", name).val(fileId);
		input.insertBefore(fileInputDom);
	}});
}

