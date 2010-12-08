/*
 * @version 1.1
 * @date:2010/10/01
 * @author:Geln Yang
 */
(function () {
	/*---------------------------------------
	Whether or not a Jquery object exists
	---------------------------------------*/
	jQuery.fn.exists = function () {
		return jQuery(this).length > 0;
	};
	/*---------------------------------------
	Whether a Jquery ojbect has a attribute and its lower case value equals to "true".
	---------------------------------------*/
	jQuery.fn.attrTrue = function (attr) {
		var v = jQuery(this).attr(attr);
		return v && v.toLowerCase() == "true";
	};
	/*---------------------------------------
	jtool Ojbect initialization.
	---------------------------------------*/
	var jtool = window.jtool = {
	/*------------------
	a test function
	--------------------*/
	init:function (obj) {
		this.obj = obj;
	}, 
	/*------------------
	a test funciton
	--------------------*/
	test:function () {
		alert("calling a test method!");
	}, 
	/*------------------
	add a prameter to the current page URL.
	--------------------*/
	addParam:function (key, value) {
		key = escape(key);
		value = escape(value);
		var s = document.location.search;
		var kvp = key + "=" + value;
		var r = new RegExp("(&|\\?)" + key + "=[^&]*");
		s = s.replace(r, "$1" + kvp);
		if (!RegExp.$1) {
			s += (s.length > 0 ? "&" : "?") + kvp;
		}
		document.location.search = s;
	}, 
	/*------------------
	add a prameter to the target source URL.
	--------------------*/
	addUrlParam:function (sourceUrl, parameterName, parameterValue, replaceDuplicates) {
		if ((sourceUrl == null) || (sourceUrl.length == 0)) {
			sourceUrl = document.location.href;
		}
		var urlParts = sourceUrl.split("?");
		var newQueryString = "";
		if (urlParts.length > 1) {
			var parameters = urlParts[1].split("&");
			for (var i = 0; (i < parameters.length); i++) {
				var parameterParts = parameters[i].split("=");
				if (!(replaceDuplicates && parameterParts[0] == parameterName)) {
					if (newQueryString == "") {
						newQueryString = "?";
					} else {
						newQueryString += "&";
					}
					newQueryString += parameterParts[0] + "=" + parameterParts[1];
				}
			}
		}
		if (newQueryString == "") {
			newQueryString = "?";
		} else {
			newQueryString += "&";
		}
		newQueryString += parameterName + "=" + parameterValue;
		return urlParts[0] + newQueryString;
	}, 
	
	/*------------------
	encode url
	--------------------*/
	encodeURL:function (url) {
		return encodeURI(encodeURI(url));
	},
	/*------------------
	Jump to the target URL.
	--------------------*/
	goto:function (url) {
		window.location.href = jtool.encodeURL(url);
	}, 
	/*------------------
	Jump to the target URL which will be added two parameters "urlfrom" and "urltext" into.
	--------------------*/
	goInto:function (url) {
		url = jtool.formatURLReffer(url);
		window.location.href = url;
	}, 

	/*------------------
	add two parameters "urlfrom" and "urltext" to the target URL.
	--------------------*/
	formatURLReffer:function (targetURL) {
		var url = escape(document.location.href);
		targetURL = jtool.addUrlParam(targetURL, "urlfrom", url);
		var pagetitle = $($("title")[0]).text();
		targetURL = jtool.addUrlParam(targetURL, "urltext", pagetitle);
		return targetURL;
	}, 

	/*------------------
	open a URL which will be encoded first.
	--------------------*/
	openPage:function (url) {
		window.open(encodeURI(url));
	}, 

	/*------------------
	open a URL which will be encoded first.The function is same as function "openPage".
	--------------------*/
	open:function (url) {
		window.open(encodeURI(url));
	}, 

	/*------------------
	Get a URL content and handle it. The content can be cached in current page.
	--------------------*/
	get:function (targetUrl, params, toCache, dataType, handler, errorHandler) {
		if (!dataType) {
			dataType = "html";
		}
		var url = targetUrl;
		if (params) {
			url += $.param(params);
		}
		if (toCache) {
			var cacheContent = jtool.ajaxcache[url];
			if (cacheContent) {
				handler(cacheContent);
				return;
			}
		}
		$.ajax({url:targetUrl, type:"POST", data:(params), dataType:dataType, success:function (content) {
			handler(content);
			if (toCache) {
				jtool.ajaxcache[url] = content;
			}
		}, error:function (xhr, ajaxOptions, thrownError) {
			errorHandler(xhr, ajaxOptions, thrownError);
		}});
	}, 
	/*------------------
	load a page content into the target DOM container.
	--------------------*/
	load:function (containerId, targetUrl, params, toCache) {
		var container = $("#" + containerId);
		if (container.exists()) {
			if (toCache) {
				var cacheContent = jtool.ajaxcache[targetUrl + params];
				if (cacheContent) {
					container.html(content);
					return;
				}
			}
			$.ajax({url:targetUrl, type:"GET", data:(params), success:function (content) {
				container.html(content);
				if (toCache) {
					jtool.ajaxcache[targetUrl + params] = content;
				}
			}});
		}
	}, 

	/*------------------
	add a Option into a Select.
	--------------------*/
	addOption:function (selectObj, values) {
		/* add a map into a select object */
		$.each(values, function (key, value) {
			$(selectObj).append($("<option></option>").attr("value", key).text(value));
		});
	},
	/*------------------
	Page functions initialization
	--------------------*/
	page:{
		/*------------------
		change location href parameter "page.currentPage" then jump to the page.
		--------------------*/
		goto:function (pageno) {
			var url = jtool.addUrlParam(null, "page.currentPage", pageno, true);
			window.location.href = url;
		}
	},
	/*------------------
	create a flash view mode by fading in-out-in. 
	--------------------*/
	flashview:function (containerId) {
		$(document).ready(function () {
			var obj = $("#" + containerId);
			obj.hide();
			obj.addClass("flashview");
			obj.fadeIn(1000);
			obj.fadeOut(1000, function () {
				obj.removeClass("flashview");
			});
			obj.fadeIn(1000);
		});
	},
	/*------------------
	change all the link target value to "_blank" under the container .
	--------------------*/
	linkAll2Blank:function (container) {
		var obj =$(container);
		if(typeof(containerId)=="string"){
			obj = $("#"+container);
		}
		 obj.find("a").each(function(){
			 $(this).attr("target","_blank");
		 });
	}};
	jtool.ajaxcache = {};
})(jQuery);

