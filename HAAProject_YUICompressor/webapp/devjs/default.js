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

