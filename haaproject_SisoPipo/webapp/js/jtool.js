
jQuery.fn.exists = function () {
	return jQuery(this).length > 0;
};
(function () {
	var jtool = window.jtool = {init:function (obj) {
		this.obj = obj;
	}, test:function () {
		alert("calling a test method!");
	}, addParam:function (key, value) {
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
	}, addUrlParam:function (sourceUrl, parameterName, parameterValue, replaceDuplicates) {
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
	}, encodeURL:function (url) {
		return encodeURI(encodeURI(url));
	}, goto:function (url) {
		window.location.href = jtool.encodeURL(url);
	}, goInto:function (url) {
		url = jtool.formatURLReffer(url);
		window.location.href = url;
	}, formatURLReffer:function (targetURL) {
		var url = escape(document.location.href);
		targetURL = jtool.addUrlParam(targetURL, "urlfrom", url);
		var pagetitle = $($("title")[0]).text();
		targetURL = jtool.addUrlParam(targetURL, "urltext", pagetitle);
		return targetURL;
	}, openPage:function (url) {
		window.open(encodeURI(url));
	}, open:function (url) {
		window.open(encodeURI(url));
	}, get:function (targetUrl, handler, params, toCache, dataType) {
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
		$.ajax({url:targetUrl, type:"GET", data:(params), dataType:dataType, success:function (content) {
			handler(content);
			if (toCache) {
				jtool.ajaxcache[url] = content;
			}
		}});
	}, load:function (containerId, targetUrl, params, toCache) {
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
	}, addOption:function (selectObj, values) {
		/*add a map into a select object*/
		$.each(values, function (key, value) {
			$(selectObj).append($("<option></option>").attr("value", key).text(value));
		});
	}, page:{goto:function (pageno) {
		var url = jtool.addUrlParam(null, "page.currentPage", pageno, true);
		window.location.href = url;
	}}, flashview:function (containerId) {
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
	}};
	jtool.ajaxcache = {};
})();

