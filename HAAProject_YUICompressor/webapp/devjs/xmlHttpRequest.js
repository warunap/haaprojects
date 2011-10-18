function lelenole_xmlhttprequest(onerror)
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

