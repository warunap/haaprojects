/*---------------------------------------
Load a array list of Script file 
---------------------------------------*/
function script(u) {
	for (var i in u) {
		var script = document.createElement("script");
		script.type = "text/javascript";
		script.src = u[i];
		document.body.appendChild(script);
	}
}
/*---------------------------------------
Load a array list of Link file 
---------------------------------------*/
function link(u) {
	for (var i in u) {
		var link = document.createElement("link");
		link.type = "text/css";
		link.rel = "stylesheet";
		link.href = u[i];
		document.body.appendChild(link);
	}
}
/*---------------------------------------
Map ojbect definition
---------------------------------------*/
function Map() {
	this.arr = function () {
		if (!this.mapobj) {
			this.mapobj = {};
			this.mapsize = 0;
		}
		return this.mapobj;
	};
	this.put = function (key, val) {
		if (!this.arr()[key]) {
			this.mapsize++;
		}
		this.arr()[key] = val;
	};
	this.get = function (key) {
		return this.arr()[key];
	};
	this.clear = function () {
		this.arr() = {};
		this.mapsize = 0;
	};
	this.iterate = function () {
		return this.arr();
	};
	this.size = function () {
		return this.mapsize;
	};
}

