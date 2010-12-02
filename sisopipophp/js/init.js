/*---------------------------------------
Load a array list of Script file 
---------------------------------------*/
function script(u) {
	for ( var i in u) {
		var s = document.getElementsByTagName('script')[0];
		var script = document.createElement("script");
		script.type = "text/javascript";
		script.async = true;
		script.src = u[i];
		s.parentNode.appendChild(script);
	}
}
/*---------------------------------------
 Load a array list of Link file 
 ---------------------------------------*/
function link(u) {
	for ( var i in u) {
		var s = document.getElementsByTagName('link')[0];
		var link = document.createElement("link");
		link.type = "text/css";
		link.rel = "stylesheet";
		link.href = u[i];
		s.parentNode.appendChild(link);
	}
}
/*---------------------------------------
 Map ojbect definition
 ---------------------------------------*/
function Map() {
	this.arr = function() {
		if (!this.mapobj) {
			this.mapobj = {};
			this.mapsize = 0;
		}
		return this.mapobj;
	};
	this.put = function(key, val) {
		if (!this.arr()[key]) {
			this.mapsize++;
		}
		this.arr()[key] = val;
	};
	this.get = function(key) {
		return this.arr()[key];
	};
	this.clear = function() {
		this.arr() = {};
		this.mapsize = 0;
	};
	this.iterate = function() {
		return this.arr();
	};
	this.size = function() {
		return this.mapsize;
	};
}
/*---------------------------------------
 Google analysis loading code
 ---------------------------------------*/
var _gaq = _gaq || [];
_gaq.push([ '_setAccount', 'UA-17986043-2' ]);
_gaq.push([ '_trackPageview' ]);
_gaq.push([ '_addOrganic', 'bing', 'q' ]);
_gaq.push([ '_addOrganic', 'baidu', 'word' ]);
_gaq.push([ '_addOrganic', 'soso', 'w' ]);
_gaq.push([ '_addOrganic', '3721', 'name' ]);
_gaq.push([ '_addOrganic', 'yodao', 'q' ]);
_gaq.push([ '_addOrganic', 'vnet', 'kw' ]);
_gaq.push([ '_addOrganic', 'sogou', 'query' ]);
_gaq.push([ '_setDomainName', '.sisopipo.com' ]);
_gaq.push([ '_setVar', 'index' ]);
(function() {
	var ga = document.createElement('script');
	ga.type = 'text/javascript';
	ga.async = true;
	ga.src = ('https:' == document.location.protocol ? 'https://ssl'
			: 'http://www')
			+ '.google-analytics.com/ga.js';
	var s = document.getElementsByTagName('script')[0];
	s.parentNode.insertBefore(ga, s)
})();