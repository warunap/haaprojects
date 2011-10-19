//m=fasle then ignoring case
String.prototype.indexOf = function(f,m){
	var mm = (m == false) ? "i":"";
	var re = eval("/"+ f +"/"+mm);
	var rt = this.match(re);
	return (rt == null) ? -1:rt.index;	
}

var	lightKeyWords =function(e){	
	var	obj	= document.getElementById(e); 
	for(var i=0;i<keywords.length;i++) {
		highlightWord(obj,keywords[i]);
	} 
	//obj.innerHTML=tmp;
} 

function highlightWord(node,word) {
	// Iterate into	this nodes childNodes

	if (node.hasChildNodes)	{
		for	(var i=0;i<node.childNodes.length;i++) {
			highlightWord(node.childNodes[i],word);
		}
	}
	// And do this node	itself

	if (node.nodeType == 3)	{ // text node
		tempNodeVal	= node.nodeValue.toLowerCase();
		tempWordVal	= word.toLowerCase();
		if (tempNodeVal.indexOf(tempWordVal,false) !=	-1)	{
			pn = node.parentNode;
			if (pn.className !=	"keyword") {
				// word	has	not	already	been highlighted!
				nv = node.nodeValue;


				ni = tempNodeVal.indexOf(tempWordVal,false);
				// Create a	load of	replacement	nodes
				before = document.createTextNode(nv.substr(0,ni));
				docWordVal = nv.substr(ni,word.length);
				after =	document.createTextNode(nv.substr(ni+word.length));
				hiwordtext = document.createTextNode(docWordVal);
				hiword = document.createElement("span");
				hiword.className = "keyword";
				hiword.appendChild(hiwordtext);
				pn.insertBefore(before,node);
				pn.insertBefore(hiword,node);
				pn.insertBefore(after,node);
				pn.removeChild(node);
			}
		}
	} 
}


function SearchHighlight() {
	if (!document.createElement) return;
	href = document.location.href;
	if (href.indexOf('?',false) == -1) return;
	qs = href.substr(href.indexOf('?',false)+1);	
	qsa = qs.split('&');
	for (i=0;i<qsa.length;i++) {
		qsip = qsa[i].split('=');
		if (qsip.length == 1) continue;
		if (qsip[0] == 's') {
			//words = unescape(decodeURIComponent(qsip[1].replace(/\+/g,' '))).split(/\s+/);
			//for (w=0;w<words.length;w++) {
			//	highlightWord(document.getElementById("main"),words[w]);
			//}
		}
	}
}

function   utf8(wide)   {  
	var   c,   s;  
	var   enc   =   "";  
	var   i   =   0;  
	while(i<wide.length)   {  
		c=   wide.charCodeAt(i++);  
		//   handle   UTF-16   surrogates  
		if   (c>=0xDC00   &&   c<0xE000)   continue;  
		if   (c>=0xD800   &&   c<0xDC00)   {  
			if   (i>=wide.length)   continue;  
			s=   wide.charCodeAt(i++);  
			if   (s<0xDC00   ||   c>=0xDE00)   continue;  
			c=   ((c-0xD800)<<10)+(s-0xDC00)+0x10000;  
		}  
		//   output   value  
		if   (c<0x80)   enc   +=   String.fromCharCode(c);  
		else   if   (c<0x800)   enc   +=   String.fromCharCode(0xC0+(c>>6),0x80+(c&0x3F));  
		else   if   (c<0x10000)   enc   +=   String.fromCharCode(0xE0+(c>>12),0x80+(c>>6&0x3F),0x80+(c&0x3F));  
		else   enc   +=   String.fromCharCode(0xF0+(c>>18),0x80+(c>>12&0x3F),0x80+(c>>6&0x3F),0x80+(c&0x3F));  
	}  
	return   enc;  
}  
	
var   hexchars   =   "0123456789ABCDEF";  
var   okURIchars   =   "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";  
function   toHex(n)   {  
	return   hexchars.charAt(n>>4)+hexchars.charAt(n   &   0xF);  
}
function   encodeURIComponentNew(s)   {  
	var   s   =   utf8(s);  
	var   c;  
	var   enc   =   "";  
	for   (var   i=   0;   i<s.length;   i++)   {  
		if   (okURIchars.indexOf(s.charAt(i))==-1)  
			enc   +=   "%"+toHex(s.charCodeAt(i));  
		else  
			enc   +=   s.charAt(i);  
	}  
	return   enc;  
}function lelenole_xmlhttprequest(onerror)
{
	this.xmlhttpObj=null;
	this.onError=onerror?onerror:this.defaultOnError;
	this.isIE=navigator.appName.indexOf("Microsoft")!=-1?true:false;
	if(this.isIE)
	{
		try{this.xmlhttpObj=new ActiveXObject("Msxml2.XMLHTTP");}
		catch(e){
			try{this.xmlhttpObj=new ActiveXObject("Microsoft.XMLHTTP");}
			catch(e){	this.onError.call(this,e);	}
		}
	}
	else
	{
		this.xmlhttpObj=new XMLHttpRequest();
		if(this.xmlhttpObj.overrideMimeType)
		{
			this.xmlhttpObj.overrideMimeType("text/xml;charset=UTF-8");
		}
	}
}
lelenole_xmlhttprequest.prototype={
 
	defaultOnError:function(error)
	{
		if(navigator.appName.indexOf("Microsoft")!=-1)
		alert(error.message);
		else
		alert(error);
	},
	open:function(url,method,callback){
		var request=this.xmlhttpObj;
		method=method=="POST"?"POST":"GET";		
		request.onreadystatechange = function(){
		  if(request.readyState == 4) {
			  //alert("test1");
			  if(request.status == 200) {
				  callback.call(this,request);
				  //alert("test");
			  }
			 else{
				 var msgbox = document.getElementById("msgbox");
				 if(msgbox){ msgbox.innerHTML="Loading...";}
			 }
		  }
		   
		};
		request.open("GET", url, true);
		request.send(null);
	}
}

